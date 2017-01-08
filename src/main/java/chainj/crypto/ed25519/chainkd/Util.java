package chainj.crypto.ed25519.chainkd;

import chainj.encoding.VarInt;
import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Random;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class Util {

    private static Logger logger = LogManager.getLogger();

    public static XPrv newXPrv(InputStream io) throws IOException {
        byte[] entropy = new byte[32];
        if (io.read(entropy) != entropy.length) {
            throw new IOException("read eof of io, but entropy not full");
        }
        return newXPrv(entropy);
    }

    // if entropy is null, random bytes is used
    public static XPrv newXPrv(byte[] entropy) {
        if (entropy == null) {
            entropy = new byte[32];
            Random random = new Random();
            random.setSeed(System.currentTimeMillis());
            random.nextBytes(entropy);
        }
        if (entropy.length != 32) {
            throw new IllegalArgumentException("entropy length should be 32");
        }
        Digest digest = new SHA512Digest();
        byte[] flag = "Chain seed".getBytes();
        digest.update(flag, 0, flag.length);
        digest.update(entropy, 0, entropy.length);
        byte[] out = new byte[digest.getDigestSize()];
        digest.doFinal(out, 0);
        modifyScalar(out);
        return new XPrv(out);
    }

    public static PublicKey[] xPubKeys(XPub[] xpubs) {
        PublicKey[] res = new PublicKey[xpubs.length];
        for (int i = 0; i < xpubs.length; i++) {
            res[i] = xpubs[i].publicKey();
        }
        return res;
    }

    public static XPub[] deriveXPubs(XPub[] xpubs, byte[][] path) {
        XPub[] res = new XPub[xpubs.length];
        for (int i = 0; i < xpubs.length; i++) {
            res[i] = xpubs[i].derive(path);
        }
        return res;
    }

    public static EdDSAPublicKey newEdDSAPublicKey(byte[] pubKey) {
        EdDSANamedCurveSpec edc = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);
        return new EdDSAPublicKey(new EdDSAPublicKeySpec(pubKey, edc));
    }

    public static boolean verify(byte[] pubKey, byte[] msg, byte[] sig) throws InvalidKeyException, SignatureException {
        EdDSAEngine engine = new EdDSAEngine();
        engine.initVerify(newEdDSAPublicKey(pubKey));
        engine.update(msg);
        return engine.verify(sig);
    }

    static byte[] hashKeySaltSelector(byte version, byte[] key, byte[] salt, byte[] sel) {
        Digest helper = hashKeySaltHelper(version, key, salt);
        byte[] l = new byte[10];
        int n = VarInt.putUVarInt(l, sel.length);
        helper.update(l, 0, n);
        helper.update(sel, 0, sel.length);
        byte[] out = new byte[helper.getDigestSize()];
        helper.doFinal(out, 0);
        modifyScalar(out);
        return out;
    }

    static byte[] hashKeySalt(byte version, byte[] key, byte[] salt) {
        Digest helper = hashKeySaltHelper(version, key, salt);
        byte[] out = new byte[helper.getDigestSize()];
        helper.doFinal(out, 0);
        return out;
    }

    private static Digest hashKeySaltHelper(byte version, byte[] key, byte[] salt) {
        Digest digest = new SHA512Digest();
        digest.update(version);
        digest.update(key, 0, key.length);
        digest.update(salt, 0, salt.length);
        return digest;
    }

    private static void modifyScalar(byte[] digest) {
        digest[0] &= 248;
        digest[31] &= 127;
        digest[31] |= 64;
    }

}
