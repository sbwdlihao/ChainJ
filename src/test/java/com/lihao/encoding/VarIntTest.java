package com.lihao.encoding;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by sbwdlihao on 11/29/16.
 */
public class VarIntTest {

    @Test
    public void testPutUVarInt() {
        byte[] buf = new byte[10];
        Assert.assertEquals(1, VarInt.putUVarInt(buf, new BigInteger("0"))); // [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        Assert.assertEquals(0, buf[0]);
        buf = new byte[10];
        Assert.assertEquals(2, VarInt.putUVarInt(buf, new BigInteger("1234"))); // [-46, 9, 0, 0, 0, 0, 0, 0, 0, 0]
        Assert.assertEquals(-46, buf[0]);
        Assert.assertEquals(9, buf[1]);
        buf = new byte[10];
        Assert.assertEquals(2, VarInt.putUVarInt(buf, new BigInteger("ff", 16))); // [-1, 1, 0, 0, 0, 0, 0, 0, 0, 0]
        Assert.assertEquals(-1, buf[0]);
        Assert.assertEquals(1, buf[1]);
        buf = new byte[10];
        Assert.assertEquals(2, VarInt.putUVarInt(buf, new BigInteger("256"))); // [-128, 2, 0, 0, 0, 0, 0, 0, 0, 0]
        Assert.assertEquals(-128, buf[0]);
        Assert.assertEquals(2, buf[1]);
        buf = new byte[10];
        Assert.assertEquals(9, VarInt.putUVarInt(buf, new BigInteger("7fffffffffffffff", 16))); // [-1, -1, -1, -1, -1, -1, -1, -1, 127, 0]
        Assert.assertEquals(-1, buf[0]);
        Assert.assertEquals(-1, buf[1]);
        Assert.assertEquals(127, buf[8]);
        buf = new byte[10];
        VarInt.putUVarInt(buf, new BigInteger("ffffffffffffffff", 16)); // [-1, -1, -1, -1, -1, -1, -1, -1, -1, 1]
    }

    @Test
    public void testUVarInt() {
        String[] tests = new String[]{"0", "1234", "ff", "100", "7fffffffffffffff", "ffffffffffffffff"};
        for (String  test : tests) {
            byte[] buf = new byte[10];
            VarInt.putUVarInt(buf, new BigInteger(test, 16));
            Assert.assertEquals(test, VarInt.uVarInt(buf).toString(16));
        }
    }
}
