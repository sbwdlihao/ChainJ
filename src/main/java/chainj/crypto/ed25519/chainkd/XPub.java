package chainj.crypto.ed25519.chainkd;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.math.GroupElement;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.bouncycastle.util.encoders.Hex;

import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class XPub {

    private static final int extendedPublicKeySize = 64;

    private byte[] data;

    public XPub(byte[] data) {
        this.data = data;
    }

    public byte[] bytes() {
        return data;
    }

    public XPub child(byte[] sel) {
        byte[] key = Arrays.copyOfRange(data, 0, 32);
        byte[] salt = Arrays.copyOfRange(data, 32, 64);
        byte[] out = Util.hashKeySaltSelector((byte) 1, key, salt, sel);
        byte[] f = Arrays.copyOfRange(out, 0, 32);
        EdDSANamedCurveSpec edc = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);
        GroupElement F = edc.getB().scalarMultiply(f);
        GroupElement P = new GroupElement(edc.getCurve(), key);
        GroupElement Fc = F.toCached();
        GroupElement R = P.add(Fc);
        GroupElement P2 = R.toP3();
        byte[] pubkey = P2.toByteArray();
        System.arraycopy(pubkey, 0, out, 0, 32);
        return new XPub(out);
    }

    public XPub derive(byte[][] path) {
        XPub res = this;
        if (path != null) {
            for (byte[] bytes : path) {
                res = res.child(bytes);
            }
        }
        return res;
    }

    public boolean verify(byte[] msg, byte[] sig) throws InvalidKeyException, SignatureException {
        EdDSAEngine engine = new EdDSAEngine();
        engine.initVerify(publicKey());
        engine.update(msg);
        return engine.verify(sig);
    }

    public PublicKey publicKey() {
        EdDSANamedCurveSpec edc = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);
        byte[] pk = Arrays.copyOfRange(data, 0, 32);
        return new EdDSAPublicKey(new EdDSAPublicKeySpec(pk, edc));
    }

    public byte[] marshalText() {
        return Hex.encode(this.data);
    }

    public XPub unMarshalText(byte[] inp) {
        if (inp == null || inp.length != extendedPublicKeySize * 2) {
            throw new IllegalArgumentException("bad key string");
        }
        return new XPub(Hex.decode(inp));
    }

    public String toString() {
        return new String(marshalText());
    }
}
