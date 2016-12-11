package com.lihao.encoding.blockchain;

import com.lihao.encoding.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 09/12/2016.
 */
public class BlockChain {

    public static int readVarInt31(InputStream io) throws IOException {
        long val = VarInt.readUVarInt(io);
        if (val > Integer.MAX_VALUE || val < 0) {
            throw new ArithmeticException("value out of range");
        }
        return (int)val; // 返回的结果在0到2^31 - 1之间，所以用int表示没有问题
    }

    public static long readVarInt63(InputStream io) throws IOException {
        long val = VarInt.readUVarInt(io);
        if (val < 0) {
            throw new ArithmeticException("value out of range");
        }
        return val;
    }

    public static byte[] readVarStr31(InputStream io) throws IOException {
        int n = readVarInt31(io);
        if (n == 0) {
            return null;
        }
        byte[] str = new byte[n];
        if (io.read(str) != n) {
            throw new IOException("cannot read full val");
        }
        return str;
    }

    public static int writeVarInt31(OutputStream io, long val) throws IOException {
        if (val > Integer.MAX_VALUE || val < 0) {
            throw new ArithmeticException("value out of range");
        }
        // 0到0x7fffffff之间的数经过转换后最多会产生5个字节
        byte[] buf = new byte[5];
        int n = VarInt.putUVarInt(buf, val);
        io.write(buf, 0, n);
        return n;
    }

    public static int writeVarInt63(OutputStream io, long val) throws IOException {
        if (val < 0) {
            throw new ArithmeticException("value out of range");
        }
        // 0到0x7fffffffffffffff之间的数经过转换后最多会产生9个字节
        byte[] buf = new byte[9];
        int n = VarInt.putUVarInt(buf, val);
        io.write(buf, 0, n);
        return n;
    }

    public static int writeVarStr31(OutputStream io, byte[] str) throws IOException {
        int n = writeVarInt31(io, str.length);
        io.write(str);
        return n + str.length;
    }
}
