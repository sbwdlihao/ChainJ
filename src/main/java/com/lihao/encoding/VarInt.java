package com.lihao.encoding;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class VarInt {

    public static int putUVarInt(byte[] buf, long x) {
        if (buf == null) {
            throw new IllegalArgumentException("buf cannot be null");
        }
        int i = 0;
        while (x >= 0x80 || x < 0) {
            if (buf.length <= i) {
                throw new IllegalArgumentException("buf size is too small");
            }
            buf[i++] = (byte)((byte)x | 0x80); // 这样处理导致byte中的数值总是在-128和-1之间
            // 无符号右移，如果是一个负数（代表实际的正数，0x8000000000000000到0xffffffffffffffff之间）只要经过一次位移后在java中就变成了正数
            x = x >>> 7;
        }
        if (buf.length <= i) {
            throw new IllegalArgumentException("buf size is too small");
        }
        buf[i] = (byte)x; // 最后一个byte的数值在1到127之间
        return i + 1;
    }

    // 如果返回long小于0，则表示得到真实数字在2^63到2^64-1之间
    public static long uVarInt(byte[] buf) {
        if (buf == null || buf.length == 0) {
            throw new IllegalArgumentException("buf cannot be empty");
        }
        long x = 0;
        int s = 0;
        for (int i = 0; i < buf.length; i++) {
            byte b = buf[i];
            if (b > 0) { // 表示最后产生的数值
                if (i > 9 || i == 9 && b > 1) { // unsigned int64的最大值转换成字节数组后长度是10且最后1个字节值是1
                    throw new IllegalArgumentException("var is too large");
                }
                return x | ((long)b << s);
            }
            // 否则，表示经过移位产生的数值
            x |= (((long)b & 0xff) & 0x7f) << s;
            s += 7;
        }
        return x;
    }

    // 如果返回long小于0，则表示得到真实数字在2^63到2^64-1之间
    public static long readUVarInt(InputStream io) throws IOException {
        long x = 0;
        int s = 0;
        byte b;
        int i = 0;
        do {
            int bi = io.read();
            if (bi == -1) {
                break;
            }
            b = (byte)bi;
            if (b > 0) { // 表示最后产生的数值
                if (i > 9 || i == 9 && b > 1) { // unsigned int64的最大值转换成字节数组后长度是10且最后1个字节值是1
                    throw new IllegalArgumentException("var is too large");
                }
                return x | ((long)b << s);
            }
            // 否则，表示经过移位产生的数值
            x |= (((long)b & 0xff) & 0x7f) << s;
            s += 7;
            i++;
        } while (true);
        return x;
    }
}
