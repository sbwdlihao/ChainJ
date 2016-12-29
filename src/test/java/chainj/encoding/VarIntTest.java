package chainj.encoding;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by sbwdlihao on 11/29/16.
 */
public class VarIntTest {

    @Test
    public void testPutUVarInt() {
        byte[] buf = new byte[10];
        Assert.assertEquals(1, VarInt.putUVarInt(buf, 0)); // [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        Assert.assertEquals(0, buf[0]);
        buf = new byte[10];
        Assert.assertEquals(2, VarInt.putUVarInt(buf, 1234)); // [-46, 9, 0, 0, 0, 0, 0, 0, 0, 0]
        Assert.assertEquals(-46, buf[0]);
        Assert.assertEquals(9, buf[1]);
        buf = new byte[10];
        Assert.assertEquals(5, VarInt.putUVarInt(buf, 0x7fffffff)); // [-1, -1, -1, -1, 7, 0, 0, 0, 0, 0]
        Assert.assertEquals(-1, buf[0]);
        Assert.assertEquals(7, buf[4]);
        buf = new byte[10];
        Assert.assertEquals(5, VarInt.putUVarInt(buf, 0x80000000L)); // [-128, -128, -128, -128, 8, 0, 0, 0, 0, 0]
        Assert.assertEquals(-128, buf[0]);
        Assert.assertEquals(8, buf[4]);
        buf = new byte[10];
        Assert.assertEquals(9, VarInt.putUVarInt(buf, 0x7fffffffffffffffL)); // [-1, -1, -1, -1, -1, -1, -1, -1, 127, 0]
        Assert.assertEquals(-1, buf[0]);
        Assert.assertEquals(127, buf[8]);
        buf = new byte[10];
        Assert.assertEquals(10, VarInt.putUVarInt(buf, 0x8000000000000000L)); // [-128, -128, -128, -128, -128, -128, -128, -128, -128, 1]
        Assert.assertEquals(-128, buf[0]);
        Assert.assertEquals(1, buf[9]);
        buf = new byte[10];
        Assert.assertEquals(10, VarInt.putUVarInt(buf, 0xffffffffffffffffL)); // [-1, -1, -1, -1, -1, -1, -1, -1, -1, 1]
        Assert.assertEquals(-1, buf[0]);
        Assert.assertEquals(1, buf[9]);
    }

    @Test
    public void testUVarInt() throws IOException {
        long[] tests = new long[]{0, 1234, 0x7fffffffffffffffL, 0x8000000000000000L, 0xffffffffffffffffL};
        for (long  test : tests) {
            byte[] buf = new byte[10];
            VarInt.putUVarInt(buf, test);
            Assert.assertEquals(test, VarInt.uVarInt(buf));
            int[] n = new int[1];
            long v = VarInt.readUVarInt(new ByteArrayInputStream(buf), n);
            Assert.assertEquals(test, v);
        }
    }
}
