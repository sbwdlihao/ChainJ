package com.lihao.crypto.ed25519.chainkd;

import net.i2p.crypto.eddsa.math.GroupElement;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class XPrv {

    private static Logger logger = LogManager.getLogger();

    private static final int extendedPrivateKeySize = 64;

    private byte[] data;

    public XPrv(byte[] data) {
        this.data = data;
    }

    public byte[] bytes() {
        return data;
    }

    public XPub xPub() {
        byte[] buf = new byte[64];
        byte[] b0 = Arrays.copyOfRange(data, 0, 32);
        EdDSANamedCurveSpec edc = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);
        GroupElement p3 = edc.getB();
        p3 = p3.scalarMultiply(b0);
        b0 = p3.toByteArray();
        System.arraycopy(b0, 0, buf, 0, 32);
        System.arraycopy(data, 32, buf, 32, 32);
        return new XPub(buf);
    }

    public XPrv child(byte[] sel, boolean hardened) {
        byte[] key = Arrays.copyOfRange(data, 0, 32);
        byte[] salt = Arrays.copyOfRange(data, 32, 64);
        if (hardened) {
            return new XPrv(Util.hashKeySaltSelector((byte) 0, key, salt, sel));
        }
        EdDSANamedCurveSpec edc = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);
        GroupElement p3 = edc.getB();
        p3 = p3.scalarMultiply(key);
        byte[] pubkey = p3.toByteArray();
        byte[] out = Util.hashKeySaltSelector((byte) 1, pubkey, salt, sel);
        byte[] f = Arrays.copyOfRange(out, 0, 32);
        byte[] one = new byte[32];
        one[0] = 1;
        byte[] s2 = edc.getScalarOps().multiplyAndAdd(one, f, key);
        System.arraycopy(s2, 0, out, 0, 32);
        return new XPrv(out);
    }

    public XPrv derive(byte[][] path) {
        XPrv res = this;
        if (path != null) {
            for (byte[] bytes : path) {
                res = res.child(bytes, false);
            }
        }
        return res;
    }

    public byte[] sign(byte[] msg) {
        byte[] key = Arrays.copyOfRange(data, 0, 32);
        byte[] salt = Arrays.copyOfRange(data, 32, 64);
        byte[] h = Util.hashKeySalt((byte) 2, key, salt);
        EdDSANamedCurveSpec edc = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);
        GroupElement p3 = edc.getB().scalarMultiply(key);
        byte[] pubkey = p3.toByteArray();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            logger.catching(e);
            return null;
        }
        md.update(h, 0, 32);
        md.update(msg);
        byte[] r = md.digest();
        byte[] rReduced = edc.getScalarOps().reduce(r);
        p3 = edc.getB().scalarMultiply(rReduced);
        byte[] R = p3.toByteArray();

        md.reset();
        md.update(R);
        md.update(pubkey);
        md.update(msg);
        byte[] k = md.digest();
        byte[] kReduced = edc.getScalarOps().reduce(k);
        byte[] S = edc.getScalarOps().multiplyAndAdd(kReduced, key, rReduced);
        byte[] sign = new byte[R.length + S.length]; // 64个字节
        System.arraycopy(R, 0, sign, 0, R.length);
        System.arraycopy(S, 0, sign, R.length, S.length);
        return sign;
    }

    public byte[] marshalText() {
        return Hex.encode(this.data);
    }

    public XPrv unMarshalText(byte[] inp) {
        if (inp == null || inp.length != extendedPrivateKeySize * 2) {
            throw new IllegalArgumentException("bad key string");
        }
        return new XPrv(Hex.decode(inp));
    }

    public String toString() {
        return new String(marshalText());
    }
}
