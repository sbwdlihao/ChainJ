package chainj.protocol.vmutil;

import chainj.crypto.ed25519.chainkd.Util;
import chainj.protocol.vm.*;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sbwdlihao on 03/01/2017.
 */
public class Script {

    static boolean isUnSpendable(byte[] program) {
        return program.length > 0 && program[0] == OP.OP_FAIL;
    }

    // BlockMultiSigProgram returns a valid multi signature consensus
    // program where nRequired of the keys in pub keys are required to have
    // signed the block for success.  An ErrBadValue will be returned if
    // nRequired is larger than the number of keys provided.  The result
    // is: BLOCKSIGHASH <pub key>... <nRequired> <nPubKeys> CHECKMULTISIG
    static byte[] blockMultiSigProgram(List<EdDSAPublicKey> publicKeys, int nRequired) {
        Objects.requireNonNull(publicKeys);
        checkMultiSigParams(nRequired, publicKeys.size());
        Builder builder = new Builder();
        builder.addOP(OP.OP_BLOCKSIGHASH);
        for (EdDSAPublicKey publicKey : publicKeys) {
            builder.addData(publicKey.getAbyte());
        }
        builder.addInt64(nRequired).addInt64(publicKeys.size()).addOP(OP.OP_CHECKMULTISIG);
        return builder.getProgram();
    }

    static List<EdDSAPublicKey> parseBlockMultiSigProgram(byte[] script, int[] nRequiredOut) {
        Objects.requireNonNull(script);
        List<Instruction> pops = OPS.parseProgram(script);
        // 多重签名的指令由OP_BLOCKSIGHASH+存储字节数组指令（0个或多个）+存储nRequired数值指令（1个）
        // +存储公钥数组长度指令（1个）+OP.OP_CHECKMULTISIG
        if (pops.size() < 4) {
            throw new IllegalArgumentException(Errors.ErrShortProgram);
        }
        if (pops.get(pops.size() - 1).getOp() != OP.OP_CHECKMULTISIG) {
            throw new IllegalArgumentException(Errors.ErrMultiSigFormat + ": no OP_CHECKMULTISIG");
        }
        long nPubKeys = Types.asInt64(pops.get(pops.size() - 2).getData());
        if (nPubKeys != pops.size() - 4) {
            throw new IllegalArgumentException(Errors.ErrShortProgram);
        }
        long nRequired = Types.asInt64(pops.get(pops.size() - 3).getData());
        checkMultiSigParams(nRequired, nPubKeys);
        if (nRequiredOut != null && nRequiredOut.length > 0) {
            nRequiredOut[0] = (int)nRequired;
        }
        int firstPubKeyIndex = pops.size() - 3 - (int)nPubKeys;
        List<EdDSAPublicKey> publicKeys = new ArrayList<>();
        for (int i = 0; i < nPubKeys; i++) {
            EdDSAPublicKey key = Util.newEdDSAPublicKey(pops.get(firstPubKeyIndex + i).getData());
            publicKeys.add(key);
        }
        return publicKeys;
    }

    static byte[] p2spMultiSigProgram(List<EdDSAPublicKey> publicKeys, int nRequired) {
        Objects.requireNonNull(publicKeys);
        checkMultiSigParams(nRequired, publicKeys.size());
        Builder builder = new Builder();
        // Expected stack: [... NARGS SIG SIG SIG PREDICATE]
        // Number of sigs must match nRequired.
        builder.addOP(OP.OP_DUP).addOP(OP.OP_TOALTSTACK); // stash a copy of the predicate
        builder.addOP(OP.OP_SHA3); // stack is now [... NARGS SIG SIG SIG PREDICATEHASH]
        publicKeys.forEach(k -> builder.addData(k.getAbyte()));
        builder.addInt64(nRequired); // stack is now [... SIG SIG SIG PREDICATEHASH PUB PUB PUB M]
        builder.addInt64(publicKeys.size()); // stack is now [... SIG SIG SIG PREDICATEHASH PUB PUB PUB M N]
        builder.addOP(OP.OP_CHECKMULTISIG).addOP(OP.OP_VERIFY); // stack is now [... NARGS]
        builder.addOP(OP.OP_FROMALTSTACK); // stack is now [... NARGS PREDICATE]
        builder.addInt64(0).addOP(OP.OP_CHECKPREDICATE);
        return builder.getProgram();
    }

    static List<EdDSAPublicKey> parseP2SPBlockMultiSigProgram(byte[] script, int[] nRequiredOut) {
        Objects.requireNonNull(script);
        List<Instruction> pops = OPS.parseProgram(script);
        // todo 从p2spMultiSigProgram的实现逻辑来看，允许空的publicKeys，所以这里的pops.size()应该判断小于10就可以了
        if (pops.size() < 11) {
            throw new IllegalArgumentException(Errors.ErrShortProgram);
        }
        // Count all instructions backwards from the end in case there are
        // extra instructions at the beginning of the program (like a
        // <pushdata> DROP).
        long nPubKeys = Types.asInt64(pops.get(pops.size() - 6).getData());
        if (nPubKeys > pops.size() - 10) {
            throw new IllegalArgumentException(Errors.ErrShortProgram);
        }
        long nRequired = Types.asInt64(pops.get(pops.size() - 7).getData());
        checkMultiSigParams(nRequired, nPubKeys);
        if (nRequiredOut != null && nRequiredOut.length > 0) {
            nRequiredOut[0] = (int)nRequired;
        }
        int firstPubKeyIndex = pops.size() - 7 - (int)nPubKeys;
        List<EdDSAPublicKey> publicKeys = new ArrayList<>();
        for (int i = 0; i < nPubKeys; i++) {
            EdDSAPublicKey key = Util.newEdDSAPublicKey(pops.get(firstPubKeyIndex + i).getData());
            publicKeys.add(key);
        }
        return publicKeys;
    }

    // 允许nRequired和nPubKeys都为0
    public static void checkMultiSigParams(long nRequired, long nPubKeys) {
        if (nRequired < 0) {
            throw new IllegalArgumentException("negative quorum");
        }
        if (nPubKeys < 0) {
            throw new IllegalArgumentException("negative pub key count");
        }
        if (nRequired > nPubKeys) {
            throw new IllegalArgumentException("quorum too big");
        }
        if (nRequired == 0 && nPubKeys > 0) {
            throw new IllegalArgumentException("quorum empty with non-empty pub key list");
        }
    }
}
