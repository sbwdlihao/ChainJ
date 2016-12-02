package com.lihao.crypto.ed25519.chainkd;

import com.lihao.encoding.VarInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            logger.catching(e);
            return null;
        }
        md.update("Chain seed".getBytes());
        md.update(entropy);
        byte[] digest = md.digest();
        modifyScalar(digest);
        return new XPrv(digest);
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

    static byte[] hashKeySaltSelector(byte version, byte[] key, byte[] salt, byte[] sel) {
        MessageDigest helper = hashKeySaltHelper(version, key, salt);
        byte[] l = new byte[10];
        int n = VarInt.putUvarint(l, sel.length);
        helper.update(l, 0, n);
        helper.update(sel);
        byte[] out = helper.digest();
        modifyScalar(out);
        return out;
    }

    static byte[] hashKeySalt(byte version, byte[] key, byte[] salt) {
        MessageDigest helper = hashKeySaltHelper(version, key, salt);
        return helper.digest();
    }

    private static MessageDigest hashKeySaltHelper(byte version, byte[] key, byte[] salt) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            logger.catching(e);
            return null;
        }
        md.update(version);
        md.update(key);
        md.update(salt);
        return md;
    }

    private static void modifyScalar(byte[] digest) {
        digest[0] &= 248;
        digest[31] &= 127;
        digest[31] |= 64;
    }

}
