package chainj.protocol.vm;

import chainj.crypto.ed25519.chainkd.Util;
import chainj.math.checked.Checked;
import chainj.protocol.bc.Hash;
import chainj.protocol.vmutil.Script;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 07/01/2017.
 */
class Crypto {

    static final Function<VirtualMachine, Void> opRipemd160 = vm -> {
        doHash(vm, new RIPEMD160Digest());
        return null;
    };

    static final Function<VirtualMachine, Void> opSha1 = vm -> {
        doHash(vm, new SHA1Digest());
        return null;
    };

    static final Function<VirtualMachine, Void> opSha256 = vm -> {
        doHash(vm, new SHA256Digest());
        return null;
    };

    static final Function<VirtualMachine, Void> opSha3 = vm -> {
        doHash(vm, new SHA3Digest());
        return null;
    };

    static final Function<VirtualMachine, Void> opCheckSig = vm -> {
        vm.applyCost(1024);
        byte[] pubKey = vm.pop(true);
        if (pubKey.length != 32) {
            vm.pushBool(false, true);
            return null;
        }
        byte[] msg = vm.pop(true);
        if (msg.length != 32) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        byte[] sig = vm.pop(true);
        boolean v;
        try {
            v = Util.verify(pubKey, msg, sig);
        } catch (InvalidKeyException | SignatureException e) {
            throw new VMRunTimeException(e);
        }
        vm.pushBool(v, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opCheckMultiSig = vm -> {
        long numPubKeys = vm.popInt64(true);
        if (numPubKeys < 0) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        long pubCost;
        try {
            pubCost = Checked.mulInt64(numPubKeys, 1024);
        } catch (ArithmeticException e) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        vm.applyCost(pubCost);
        long numSigs = vm.popInt64(true);
        try {
            Script.checkMultiSigParams(numSigs, numPubKeys);
        } catch (IllegalArgumentException e) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        byte[][] pubKeys = new byte[(int)numPubKeys][];
        for (int i = 0; i < pubKeys.length; i++) {
            pubKeys[i] = vm.pop(true);
        }
        byte[] msg = vm.pop(true);
        if (msg.length != 32) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        byte[][] sigs = new byte[(int)numSigs][];
        for (int i = 0; i < sigs.length; i++) {
            sigs[i] = vm.pop(true);
        }
        for (byte[] pubKey : pubKeys) {
            if (pubKey.length != 32) {
                vm.pushBool(false, true);
                return null;
            }
        }
        int i = 0, j = 0;
        while (i < pubKeys.length && j < sigs.length) {
            try {
                if (Util.verify(pubKeys[i], msg, sigs[j])) {
                    j++;
                }
                i++;
            } catch (InvalidKeyException | SignatureException e) {
                throw new VMRunTimeException(e);
            }
        }
        vm.pushBool(sigs.length == j, true);
        return null;
    };

    static final Function<VirtualMachine, Void> opTxSigHash = vm -> {
        if (vm.getTx() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        vm.applyCost(256);
        Hash hash = vm.getSigHasher().hash(vm.getInputIndex());
        vm.push(hash.getValue(), false);
        return null;
    };

    static final Function<VirtualMachine, Void> opBlockSigHash = vm -> {
        if (vm.getBlock() == null) {
            throw new VMRunTimeException(Errors.ErrContext);
        }
        Hash hash = vm.getBlock().hashForSig();
        vm.applyCost(4 * hash.getValue().length);
        vm.push(hash.getValue(), false);
        return null;
    };

    private static void doHash(VirtualMachine vm, Digest digest) {
        byte[] x = vm.pop(false);
        int cost = Math.max(x.length, 64);
        vm.applyCost(cost);
        digest.update(x, 0, x.length);
        byte[] out = new byte[digest.getDigestSize()];
        digest.doFinal(out, 0);
        vm.push(out, false);
    }
}
