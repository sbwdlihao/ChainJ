package com.lihao.encoding;

import java.math.BigInteger;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class VarInt {

    private static BigInteger b128 = new BigInteger("128");
    private static BigInteger b127 = new BigInteger("127");
    private static BigInteger b1 = new BigInteger("1");

    public static int putUVarInt(byte[] buf, BigInteger x) {
        if (buf == null) {
            throw new IllegalArgumentException("buf cannot be null");
        }
        int i = 0;
        while (x.compareTo(b128) != -1) { // x >= 0x80
            if (buf.length <= i) {
                throw new IllegalArgumentException("buf size is too small");
            }
            buf[i++] = x.or(b128).byteValue();
            x = x.shiftRight(7);
        }
        if (buf.length <= i) {
            throw new IllegalArgumentException("buf size is too small");
        }
        buf[i] = x.byteValue();
        return i + 1;
    }

    public static BigInteger uVarInt(byte[] buf) {
        if (buf == null || buf.length == 0) {
            throw new IllegalArgumentException("buf cannot be empty");
        }
        BigInteger x = new BigInteger("0");
        int s = 0;
        for (int i = 0; i < buf.length; i++) {
            BigInteger b = new BigInteger(String.valueOf(buf[i] & 0xff));
            if (b.compareTo(b128) == -1) { // b < 0x80
                if (i > 9 || i == 9 && b.compareTo(b1) == 1) {
                    throw new IllegalArgumentException("var is too large");
                }
                return x.or(b.shiftLeft(s));
            }
            x = x.or(b.and(b127).shiftLeft(s));
            s += 7;
        }
        return x;
    }
}
