package chainj.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by sbwdlihao on 09/01/2017.
 */
public class ByteBufferUtil {

    public static long b2LongLE(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(bytes);
        buf.rewind();
        return buf.getLong();
    }

    public static int b2IntLE(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.put(bytes);
        buf.rewind();
        return buf.getInt();
    }

    public static byte[] long2BLE(long n) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(n);
        return buffer.array();
    }

    public static byte[] int2BLE(int n) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(n);
        return buffer.array();
    }

    public static byte[] short2BLE(short n) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(n);
        return buffer.array();
    }
}
