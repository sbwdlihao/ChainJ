package chainj.protocol.vm;

import chainj.util.ByteArrayBuffer;
import chainj.util.ByteBufferUtil;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sbwdlihao on 05/01/2017.
 */
class Assemble {

    private static String[] words = new String[] {
            "alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel",
            "india", "juliet", "kilo", "lima", "mike", "november", "oscar", "papa",
            "quebec", "romeo", "sierra", "tango", "uniform", "victor", "whisky", "xray",
            "yankee", "zulu",
    };

    // Assemble converts a string like "2 3 ADD 5 NUMEQUAL" into 0x525393559c.
    // The input should not include PUSHDATA (or OP_<num>) ops; those will
    // be inferred.
    // Input may include jump-target labels of the form $foo, which can
    // then be used as JUMP:$foo or JUMPIF:$foo.
    static byte[] assemble(String s) {
        ByteArrayBuffer res = ByteArrayBuffer.make();

        // maps labels to the location each refers to
        Map<String, Integer> locations = new HashMap<>();

        // maps unResolved uses of labels to the locations that need to be filled in
        Map<String, List<Integer>> unResolved = new HashMap<>();
        Pattern pattern = Pattern.compile("'(?:[^']|(?<=\\\\)')*'|[^'\\s]+");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String token = matcher.group(0);
            OPInfo opInfo = OPInfo.getOPInfoByName(token);
            if (opInfo != null) {
                if (token.startsWith("PUSHDATA") || token.startsWith("JUMP")) {
                    throw new VMRunTimeException(Errors.ErrToken);
                }
                res.append(opInfo.getOp());
            } else if (token.startsWith("JUMP:")) {
                handleJump(StringUtils.substringAfter(token, "JUMP:"), OP.OP_JUMP, res, unResolved);
            } else if (token.startsWith("JUMPIF:")) {
                handleJump(StringUtils.substringAfter(token, "JUMPIF:"), OP.OP_JUMPIF, res, unResolved);
            } else if (token.startsWith("$")) {
                if (locations.get(token) != null) {
                    throw new VMRunTimeException(String.format("label %s redefined", token));
                }
                locations.put(token, res.length());
            } else if (token.startsWith("0x")) {
                byte[] bytes = Hex.decode(StringUtils.substringAfter(token, "0x"));
                res.append(PushData.pushDataBytes(bytes));
            } else if (token.length() >= 2 && token.charAt(0) == '\'' && token.charAt(token.length() - 1) == '\'') {
                ByteArrayBuffer buf = ByteArrayBuffer.make();
                for (int i = 1; i < token.length() - 1; i++) {
                    if (token.charAt(i) == '\\') {
                        i++;
                    }
                    buf.append((byte)token.charAt(i));
                }
                byte[] bytes = PushData.pushDataBytes(buf.toByteArray());
                res.append(bytes);
            } else {
                try {
                    long num = Long.parseLong(token);
                    byte[] bytes = PushData.pushDataInt64(num);
                    res.append(bytes);
                } catch (NumberFormatException e) {
                    throw new VMRunTimeException(Errors.ErrToken);
                }
            }
        }

        unResolved.forEach((label, uses) -> {
            Integer location = locations.get(label);
            if (location == null) {
                throw new VMRunTimeException(String.format("undefined label %s", label));
            }
            uses.forEach(use->{
                byte[] bytes = ByteBufferUtil.int2BLE(location);
                res.copyOfRange(use, bytes);
            });
        });
        return res.toByteArray();
    }
    
    static String disAssemble(byte[] program) {
        if (program == null || program.length == 0) {
            return "";
        }
        List<Instruction> instructions = new ArrayList<>();
        // maps program locations (used as jump targets) to a label for each
        Map<Integer, String> labels = new HashMap<>();

        // first pass: look for jumps
        for (int i = 0; i < program.length;) {
            Instruction instruction = OPS.parseOp(program, i);
            if (instruction.getOp() == OP.OP_JUMP || instruction.getOp() == OP.OP_JUMPIF) {
                int address = ByteBufferUtil.b2IntLE(instruction.getData());
                if (labels.get(address) == null) {
                    int labelNum = labels.size();
                    String label = words[labelNum%words.length];
                    if (labelNum >= words.length) {
                        label += String.format("%d", labelNum/words.length + 1);
                    }
                    labels.put(address, label);
                }
            }
            instructions.add(instruction);
            i += instruction.getLen();
        }
        int location = 0;
        List<String> strings = new ArrayList<>();
        for (Instruction instruction : instructions) {
            String label = labels.get(location);
            if (label != null) {
                strings.add("$" + label);
            }
            String s;
            switch (instruction.getOp()) {
                case OP.OP_JUMP:
                case OP.OP_JUMPIF:
                    int address = ByteBufferUtil.b2IntLE(instruction.getData());
                    s = String.format("%s:$%s", OPInfo.getOPName(instruction.getOp()), labels.get(address));
                    break;
                default:
                    if (instruction.getData().length > 0) {
                        s = String.format("0x%s", new String(Hex.encode(instruction.getData())));
                    } else {
                        s = OPInfo.getOPName(instruction.getOp());
                    }
                    break;
            }
            strings.add(s);
            location += instruction.getLen();
        }
        if (labels.get(location) != null) {
            strings.add("$"+labels.get(location));
        }
        return StringUtils.join(strings, " ");
    };

    private static void handleJump(String address, byte opCode, ByteArrayBuffer res, Map<String, List<Integer>> unResolved) {
        res.append(opCode);
        int l = res.length();
        res.append(new byte[]{0, 0, 0 ,0});
        if (address.startsWith("$")) {
            List<Integer> byteLengths = unResolved.get(address);
            if (byteLengths == null) {
                byteLengths = new ArrayList<>();
            }
            byteLengths.add(l);
            unResolved.put(address, byteLengths);
            return;
        }
        int n = Integer.parseInt(address);
        byte[] bytes = ByteBufferUtil.int2BLE(n);
        res.copyOfRange(l, bytes);
    }
}
