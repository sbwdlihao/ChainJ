package chainj.protocol.vm;

import chainj.util.ByteBufferUtil;

import java.util.Arrays;

/**
 * Created by sbwdlihao on 02/01/2017.
 */
public class Types {

    static byte[] boolBytes(boolean b) {
        return b ? new byte[]{1} : new byte[0];
    }

    static boolean asBool(byte[] bytes) {
        for (byte aByte : bytes) {
            if (aByte != 0) {
                return true;
            }
        }
        return false;
    }

    static byte[] int64Bytes(long n) {
        if (n == 0) {
            return new byte[]{};
        }
        byte[] res = ByteBufferUtil.long2BLE(n);
        int l = res.length;
        while (l > 0 && res[l - 1] == 0) {
            l--;
        }
        return Arrays.copyOfRange(res, 0, l);
    }

    public static long asInt64(byte[] b) {
        if (b.length == 0) {
            return 0;
        }
        if (b.length > 8) {
            throw new VMRunTimeException(Errors.ErrBadValue);
        }
        return ByteBufferUtil.b2LongLE(b);
    }
}
