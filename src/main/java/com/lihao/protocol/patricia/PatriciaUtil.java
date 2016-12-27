package com.lihao.protocol.patricia;

/**
 * Created by sbwdlihao on 27/12/2016.
 */
class PatriciaUtil {

    // bitKey takes a byte array and returns a key that can
    // be used inside insert and delete operations.
    static int[] bitKey(byte[] byteKey) {
        if (byteKey == null) {
            return new int[0];
        }
        int[] key = new int[byteKey.length * 8];
        for (int i = 0; i < byteKey.length; i++) {
            int b = byteKey[i] & 0xff;
            for (int j = 0; j < 8; j++) {
                int flag = 0x80 >> j;
                key[i*8+j] = (b & flag) >> (7 - j);
            }
        }
        return key;
    }

    // byteKey is the inverse of bitKey.
    static byte[] byteKey(int[] bitKey) {
        if (bitKey == null) {
            return new byte[0];
        }
        byte[] key = new byte[bitKey.length/8];
        for (int i = 0, j = 0; i < bitKey.length; i += 8) {
            int v = 0;
            for (int k = 0; k < 8; k++) {
                int bit = bitKey[i + k];
                v |= (bit << (7 - k));
            }
            key[j++] = (byte) v;
        }
        return key;
    }

    static int commonPrefixLen(int[] a, int[] b) {
        int common = 0;
        if (a != null && b != null) {
            for (int i = 0; i < a.length && i < b.length; i++) {
                if (a[i] != b[i]) {
                    break;
                }
                common++;
            }
        }
        return common;
    }
}
