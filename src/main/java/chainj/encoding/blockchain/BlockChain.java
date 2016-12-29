package chainj.encoding.blockchain;

import chainj.encoding.VarInt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 09/12/2016.
 */
public class BlockChain {

    public static int readVarInt31(InputStream in) throws IOException {
        return readVarInt31(in, null);
    }

    public static int readVarInt31(InputStream in, int[] nOut) throws IOException {
        long val = VarInt.readUVarInt(in, nOut);
        if (val > Integer.MAX_VALUE || val < 0) {
            throw new ArithmeticException("value out of range");
        }
        return (int)val; // 返回的结果在0到2^31 - 1之间，所以用int表示没有问题
    }

    public static long readVarInt63(InputStream in) throws IOException {
        return readVarInt63(in, null);
    }

    public static long readVarInt63(InputStream in, int[] nOut) throws IOException {
        long val = VarInt.readUVarInt(in, nOut);
        if (val < 0) {
            throw new ArithmeticException("value out of range");
        }
        return val;
    }

    public static byte[] readVarStr31(InputStream in) throws IOException {
        return readVarStr31(in, null);
    }

    public static byte[] readVarStr31(InputStream in, int[] nOut) throws IOException {
        int n = readVarInt31(in, nOut);
        byte[] str = new byte[n]; // 当n=0时，返回一个长度为0的字节数组
        if (n > 0 && in.read(str) != n) {
            throw new IOException("cannot readFull full val");
        }
        if (nOut != null && nOut.length > 0) {
            nOut[0] += n;
        }
        return str;
    }

    public static int writeVarInt31(OutputStream out, long val) throws IOException {
        if (val > Integer.MAX_VALUE || val < 0) {
            throw new ArithmeticException("value out of range");
        }
        // 0到0x7fffffff之间的数经过转换后最多会产生5个字节
        byte[] buf = new byte[5];
        int n = VarInt.putUVarInt(buf, val);
        out.write(buf, 0, n);
        return n;
    }

    public static int writeVarInt63(OutputStream out, long val) throws IOException {
        if (val < 0) {
            throw new ArithmeticException("value out of range");
        }
        // 0到0x7fffffffffffffff之间的数经过转换后最多会产生9个字节
        byte[] buf = new byte[9];
        int n = VarInt.putUVarInt(buf, val);
        out.write(buf, 0, n);
        return n;
    }

    public static int writeVarStr31(OutputStream io, byte[] str) throws IOException {
        int length = 0;
        if (str != null) {
            length = str.length;
        }
        int n = writeVarInt31(io, length);
        if (str != null) {
            io.write(str);
        }
        return n + length;
    }
}
