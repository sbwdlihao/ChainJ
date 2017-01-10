package chainj.protocol.vm;

import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 01/01/2017.
 */
public class OPInfo {

    private byte op;

    private String name;

    private Function<VirtualMachine, Void> fn;

    public byte getOp() {
        return op;
    }

    String getName() {
        return name;
    }

    Function<VirtualMachine, Void> getFn() {
        return fn;
    }

    private static final OPInfo[] ops;
    private static final boolean[] isExpansion;
    private static final Map<String, OPInfo> opsByName;

    static {
        ops = new OPInfo[256];
        isExpansion = new boolean[256];
        opsByName = new HashMap<>();
        // data pushing
        addOPInfo(OP.OP_FALSE, "FALSE", PushData.opFalse);

        // sic: the PUSHDATA ops all share an implementation
        addOPInfo(OP.OP_PUSHDATA1, "PUSHDATA1", PushData.opPushdata);
        addOPInfo(OP.OP_PUSHDATA2, "PUSHDATA2", PushData.opPushdata);
        addOPInfo(OP.OP_PUSHDATA4, "PUSHDATA4", PushData.opPushdata);

        addOPInfo(OP.OP_1NEGATE, "1NEGATE", PushData.op1Negate);
        addOPInfo(OP.OP_NOP, "NOP", PushData.opNop);

        // control flow
        addOPInfo(OP.OP_JUMP, "JUMP", Control.opJump);
        addOPInfo(OP.OP_JUMPIF, "JUMPIF", Control.opJumpIf);
        addOPInfo(OP.OP_CHECKPREDICATE, "CHECKPREDICATE", Control.opCheckPredicate);
        addOPInfo(OP.OP_VERIFY, "VERIFY", Control.opVerify);
        addOPInfo(OP.OP_FAIL, "FAIL", Control.opFail);

        // stack
        addOPInfo(OP.OP_TOALTSTACK, "TOALTSTACK", Stack.opToAltStack);
        addOPInfo(OP.OP_FROMALTSTACK, "FROMALTSTACK", Stack.opFromAltStack);
        addOPInfo(OP.OP_2DROP, "2DROP", Stack.op2Drop);
        addOPInfo(OP.OP_2DUP, "2DUP", Stack.op2Dup);
        addOPInfo(OP.OP_3DUP, "3DUP", Stack.op3Dup);
        addOPInfo(OP.OP_2OVER, "2OVER", Stack.op2Over);
        addOPInfo(OP.OP_2ROT, "2ROT", Stack.op2Rot);
        addOPInfo(OP.OP_2SWAP, "2SWAP", Stack.op2Swap);
        addOPInfo(OP.OP_IFDUP, "IFDUP", Stack.opIfDup);
        addOPInfo(OP.OP_DEPTH, "DEPTH", Stack.opDepth);
        addOPInfo(OP.OP_DROP, "DROP", Stack.opDrop);
        addOPInfo(OP.OP_DUP, "DUP", Stack.opDup);
        addOPInfo(OP.OP_NIP, "NIP", Stack.opNip);
        addOPInfo(OP.OP_OVER, "OVER", Stack.opOver);
        addOPInfo(OP.OP_PICK, "PICK", Stack.opPick);
        addOPInfo(OP.OP_ROLL, "ROLL", Stack.opRoll);
        addOPInfo(OP.OP_ROT, "ROT", Stack.opRot);
        addOPInfo(OP.OP_SWAP, "SWAP", Stack.opSwap);
        addOPInfo(OP.OP_TUCK, "TUCK", Stack.opTuck);

        // splice
        addOPInfo(OP.OP_CAT, "CAT", Splice.opCat);
        addOPInfo(OP.OP_SUBSTR, "SUBSTR", Splice.opSubStr);
        addOPInfo(OP.OP_LEFT, "LEFT", Splice.opLeft);
        addOPInfo(OP.OP_RIGHT, "RIGHT", Splice.opRight);
        addOPInfo(OP.OP_SIZE, "SIZE", Splice.opSize);
        addOPInfo(OP.OP_CATPUSHDATA, "CATPUSHDATA", Splice.opCatPushdata);

        // bitwise
        addOPInfo(OP.OP_INVERT, "INVERT", Bitwise.opInvert);
        addOPInfo(OP.OP_AND, "AND", Bitwise.opAnd);
        addOPInfo(OP.OP_OR, "OR", Bitwise.opOr);
        addOPInfo(OP.OP_XOR, "XOR", Bitwise.opXor);
        addOPInfo(OP.OP_EQUAL, "EQUAL", Bitwise.opEqual);
        addOPInfo(OP.OP_EQUALVERIFY, "EQUALVERIFY", Bitwise.opEqualVerify);

        // numeric
        addOPInfo(OP.OP_1ADD, "1ADD", Numeric.op1Add);
        addOPInfo(OP.OP_1SUB, "1SUB", Numeric.op1Sub);
        addOPInfo(OP.OP_2MUL, "2MUL", Numeric.op2Mul);
        addOPInfo(OP.OP_2DIV, "2DIV", Numeric.op2Div);
        addOPInfo(OP.OP_NEGATE, "NEGATE", Numeric.opNegate);
        addOPInfo(OP.OP_ABS, "ABS", Numeric.opAbs);
        addOPInfo(OP.OP_NOT, "NOT", Numeric.opNot);
        addOPInfo(OP.OP_0NOTEQUAL, "0NOTEQUAL", Numeric.op0NotEqual);
        addOPInfo(OP.OP_ADD, "ADD", Numeric.opAdd);
        addOPInfo(OP.OP_SUB, "SUB", Numeric.opSub);
        addOPInfo(OP.OP_MUL, "MUL", Numeric.opMul);
        addOPInfo(OP.OP_DIV, "DIV", Numeric.opDiv);
        addOPInfo(OP.OP_MOD, "MOD", Numeric.opMod);
        addOPInfo(OP.OP_LSHIFT, "LSHIFT", Numeric.opLShift);
        addOPInfo(OP.OP_RSHIFT, "RSHIFT", Numeric.opRShift);
        addOPInfo(OP.OP_BOOLAND, "BOOLAND", Numeric.opBoolAnd);
        addOPInfo(OP.OP_BOOLOR, "BOOLOR", Numeric.opBoolOr);
        addOPInfo(OP.OP_NUMEQUAL, "NUMEQUAL", Numeric.opNumEqual);
        addOPInfo(OP.OP_NUMEQUALVERIFY, "NUMEQUALVERIFY", Numeric.opNumEqualVerify);
        addOPInfo(OP.OP_NUMNOTEQUAL, "NUMNOTEQUAL", Numeric.opNumNotEqual);
        addOPInfo(OP.OP_LESSTHAN, "LESSTHAN", Numeric.opLessThan);
        addOPInfo(OP.OP_GREATERTHAN, "GREATERTHAN", Numeric.opGreaterThan);
        addOPInfo(OP.OP_LESSTHANOREQUAL, "LESSTHANOREQUAL", Numeric.opLessThanOrEqual);
        addOPInfo(OP.OP_GREATERTHANOREQUAL, "GREATERTHANOREQUAL", Numeric.opGreaterThanOrEqual);
        addOPInfo(OP.OP_MIN, "MIN", Numeric.opMin);
        addOPInfo(OP.OP_MAX, "MAX", Numeric.opMax);
        addOPInfo(OP.OP_WITHIN, "WITHIN", Numeric.opWithin);

        // crypto
        addOPInfo(OP.OP_RIPEMD160, "RIPEMD160", Crypto.opRipemd160);
        addOPInfo(OP.OP_SHA1, "SHA1", Crypto.opSha1);
        addOPInfo(OP.OP_SHA256, "SHA256", Crypto.opSha256);
        addOPInfo(OP.OP_SHA3, "SHA3", Crypto.opSha3);
        addOPInfo(OP.OP_CHECKSIG, "CHECKSIG", Crypto.opCheckSig);
        addOPInfo(OP.OP_CHECKMULTISIG, "CHECKMULTISIG", Crypto.opCheckMultiSig);
        addOPInfo(OP.OP_TXSIGHASH, "TXSIGHASH", Crypto.opTxSigHash);
        addOPInfo(OP.OP_BLOCKSIGHASH, "BLOCKSIGHASH", Crypto.opBlockSigHash);

        // introspection
        addOPInfo(OP.OP_CHECKOUTPUT, "CHECKOUTPUT", Introspection.opCheckOutput);
        addOPInfo(OP.OP_ASSET, "ASSET", Introspection.opAsset);
        addOPInfo(OP.OP_AMOUNT, "AMOUNT", Introspection.opAmount);
        addOPInfo(OP.OP_PROGRAM, "PROGRAM", Introspection.opProgram);
        addOPInfo(OP.OP_MINTIME, "MINTIME", Introspection.opMinTime);
        addOPInfo(OP.OP_MAXTIME, "MAXTIME", Introspection.opMaxTime);
        addOPInfo(OP.OP_TXREFDATAHASH, "TXREFDATAHASH", Introspection.opTxRefDataHash);
        addOPInfo(OP.OP_REFDATAHASH, "REFDATAHASH", Introspection.opRefDataHash);
        addOPInfo(OP.OP_INDEX, "INDEX", Introspection.opIndex);
        addOPInfo(OP.OP_OUTPOINT, "OUTPOINT", Introspection.opOutpoint);
        addOPInfo(OP.OP_NONCE, "NONCE", Introspection.opNonce);
        addOPInfo(OP.OP_NEXTPROGRAM, "NEXTPROGRAM", Introspection.opNextProgram);
        addOPInfo(OP.OP_BLOCKTIME, "BLOCKTIME", Introspection.opBlockTime);

        for (int i = 1; i <= 75; i++) {
            addOPInfo((byte)i, "DATA_" + i, PushData.opPushdata);
        }
        for (int i = 0; i < 16; i++) {
            byte op = (byte)(i + OP.OP_1);
            addOPInfo(op, "" + (i + 1), PushData.opPushdata);
        }

        for (OPInfo op : ops) {
            if (op != null) {
                opsByName.put(op.getName(), op);
            }
        }
        opsByName.put("0", ops[OP.OP_FALSE]);
        opsByName.put("TRUE", ops[OP.OP_1]);
        for (int i = 0; i < 256; i++) {
            if (ops[i] == null || Strings.isEmpty(ops[i].getName())) {
                addOPInfo((byte)i, String.format("NOPx%02x", i), PushData.opNop);
                isExpansion[i] = true;
            }
        }
    }

    private static void addOPInfo(byte op, String name, Function<VirtualMachine, Void> fn) {
        ops[op & 0xff] = new OPInfo(op, name, fn);
    }

    static OPInfo getOPInfo(byte op) {
        return ops[op & 0xff];
    }

    static String getOPName(byte op) {
        return getOPInfo(op).getName();
    }

    static OPInfo getOPInfoByName(String name) {
        return opsByName.get(name);
    }

    static boolean isExpansion(byte op) {
        return isExpansion[op & 0xff];
    }

    private OPInfo(byte op, String name, Function<VirtualMachine, Void> fn) {
        this.op = op;
        this.name = name;
        this.fn = fn;
    }
}
