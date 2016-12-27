package com.lihao.protocol.patricia;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sbwdlihao on 27/12/2016.
 */
public class PatriciaUtilTest {

    @Test
    public void testBitKey() {
        ByteKeyCase[] cases = new ByteKeyCase[]{
                new ByteKeyCase(new int[]{}, new byte[]{}),
                new ByteKeyCase(new int[]{}, null),
                new ByteKeyCase(new int[]{1, 0, 0, 0, 1, 1, 1, 1}, new byte[]{(byte)0x8f}),
                new ByteKeyCase(new int[]{1, 0, 0, 0, 0, 0, 0, 1}, new byte[]{(byte)0x81}),
                new ByteKeyCase(new int[]{1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1}, new byte[]{(byte)0x81, (byte)0x8f}),
        };
        for (ByteKeyCase aCase : cases) {
            int[] got = PatriciaUtil.bitKey(aCase.byteKey);
            Assert.assertArrayEquals(aCase.bitKey, got);
        }
    }

    @Test
    public void testByteKey() {
        ByteKeyCase[] cases = new ByteKeyCase[]{
                new ByteKeyCase(new int[]{}, new byte[]{}),
                new ByteKeyCase(new int[]{1, 0, 0, 0, 1, 1, 1, 1}, new byte[]{(byte)0x8f}),
                new ByteKeyCase(new int[]{1, 0, 0, 0, 0, 0, 0, 1}, new byte[]{(byte)0x81}),
                new ByteKeyCase(new int[]{1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1}, new byte[]{(byte)0x81, (byte)0x8f}),
        };
        for (ByteKeyCase aCase : cases) {
            byte[] got = PatriciaUtil.byteKey(aCase.bitKey);
            Assert.assertArrayEquals(aCase.byteKey, got);
        }
    }

    class ByteKeyCase {

        int[] bitKey;
        byte[] byteKey;

        ByteKeyCase(int[] bitKey, byte[] byteKey) {
            this.bitKey = bitKey;
            this.byteKey = byteKey;
        }
    }
}
