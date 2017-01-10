package chainj.protocol.vm;

import chainj.math.checked.Checked;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by sbwdlihao on 03/01/2017.
 */
public class OPS {

    // parseOp parses the op at position pc in program, returning the parsed
    // instruction (opcode plus any associated data).
    static Instruction parseOp(byte[] program, int index) {
        // chain中会检查program的长度是否会超过1<<31 - 1，也就是java中int的最大值，但在java中program的长度必然不会超过
        Objects.requireNonNull(program);
        int l = program.length;
        if (l == 0 || index < 0 || index >= l) {
            throw new IllegalArgumentException(Errors.ErrShortProgram);
        }
        Instruction inst = new Instruction();
        byte op = program[index];
        inst.setOp(op);
        inst.setLen(1);
        // 读取数值，范围1-16，data由op的值直接转换而来
        if (op >= OP.OP_1 && op <= OP.OP_16) {
            inst.setData(new byte[]{(byte)(op - OP.OP_1 + 1)});
        }
        // 读取字节数组，长度为1-75，program的长度至少为2，最少情况为OP_DATA_1(0x01)+data(1个字节)
        else if (op >= OP.OP_DATA_1 && op <= OP.OP_DATA_75) {
            inst.setLen(inst.getLen() + op - OP.OP_DATA_1 + 1);
            int end = Checked.addInt32(index, inst.getLen());
            if (end > l) {
                throw new IllegalArgumentException(Errors.ErrShortProgram);
            }
            inst.setData(Arrays.copyOfRange(program, index + 1, end));
        }
        // 读取字节数组，长度为76-255，program的长度至少为78，最少情况为OP_PUSHDATA1(0x4c)+data len(0x4c)+data(76个字节)
        else if (op == OP.OP_PUSHDATA1) {
            if (l < 2 || index == l - 1) {
                throw new IllegalArgumentException(Errors.ErrShortProgram);
            }
            inst.setLen(inst.getLen() + 1);
            int n = program[index + 1] & 0xff;
            inst.setLen(inst.getLen() + n);
            int end = Checked.addInt32(index, inst.getLen());
            if (end > l) {
                throw new IllegalArgumentException(Errors.ErrShortProgram);
            }
            inst.setData(Arrays.copyOfRange(program, index + 2, end));
        }
        // 读取字节数组，长度为256-65535，program的长度至少为259，OP_PUSHDATA2(0x4d)+data len(0x0100)+data(256个字节)
        else if (op == OP.OP_PUSHDATA2) {
            if (l < 3 || index > l - 3) {
                throw new IllegalArgumentException(Errors.ErrShortProgram);
            }
            inst.setLen(inst.getLen() + 2);
            ByteBuffer buf = ByteBuffer.allocate(2);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(program[index + 1]);
            buf.put(program[index + 2]);
            buf.rewind();
            int n = buf.getShort() & 0xffff;
            inst.setLen(inst.getLen() + n);
            int end = Checked.addInt32(index, inst.getLen());
            if (end > l) {
                throw new IllegalArgumentException(Errors.ErrShortProgram);
            }
            inst.setData(Arrays.copyOfRange(program, index + 3, end));
        }
        // 读取字节数组，长度为65536-2147483647，program的长度至少为65541，OP_PUSHDATA4(0x4e)+data len(0x00010000)+data(65536个字节)
        else if (op == OP.OP_PUSHDATA4) {
            if (l < 5 || index > l - 5) {
                throw new IllegalArgumentException(Errors.ErrShortProgram);
            }
            inst.setLen(inst.getLen() + 4);
            ByteBuffer buf = ByteBuffer.allocate(4);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.put(program[index + 1]);
            buf.put(program[index + 2]);
            buf.put(program[index + 3]);
            buf.put(program[index + 4]);
            buf.rewind();
            int n = buf.getInt();
            inst.setLen(inst.getLen() + n);
            int end = Checked.addInt32(index, inst.getLen());
            if (end > l) {
                throw new IllegalArgumentException(Errors.ErrShortProgram);
            }
            inst.setData(Arrays.copyOfRange(program, index + 5, end));
        }
        // 读取jump地址，program的长度至少为5，op（1个字节）+jump地址（4个字节）
        else if (op == OP.OP_JUMP || op == OP.OP_JUMPIF) {
            inst.setLen(inst.getLen() + 4);
            int end = Checked.addInt32(index, inst.getLen());
            if (end > l) {
                throw new IllegalArgumentException(Errors.ErrShortProgram);
            }
            inst.setData(Arrays.copyOfRange(program, index + 1, end));
        }
        // 其它操作没有附加data
        return inst;
    }

    public static List<Instruction> parseProgram(byte[] program) {
        Objects.requireNonNull(program);
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < program.length; ) {
            Instruction instruction = parseOp(program, i);
            instructions.add(instruction);
            i = Checked.addInt32(i, instruction.getLen());
        }
        return instructions;
    }
}
