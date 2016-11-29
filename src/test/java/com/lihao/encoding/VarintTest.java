package com.lihao.encoding;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sbwdlihao on 11/29/16.
 */
public class VarintTest {

  @Test
  public void testPutUvarint() {
    byte[] buf = new byte[10];
    Assert.assertEquals(1, VarInt.putUvarint(buf, 0)); // [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    Assert.assertEquals(0, buf[0]);
    buf = new byte[10];
    Assert.assertEquals(2, VarInt.putUvarint(buf, 1234)); // [-46, 9, 0, 0, 0, 0, 0, 0, 0, 0]
    Assert.assertEquals(-46, buf[0]);
    Assert.assertEquals(9, buf[1]);
    buf = new byte[10];
    Assert.assertEquals(2, VarInt.putUvarint(buf, 0xff)); // [-1, 1, 0, 0, 0, 0, 0, 0, 0, 0]
    Assert.assertEquals(-1, buf[0]);
    Assert.assertEquals(1, buf[1]);
    buf = new byte[10];
    Assert.assertEquals(2, VarInt.putUvarint(buf, 256)); // [-128, 2, 0, 0, 0, 0, 0, 0, 0, 0]
    Assert.assertEquals(-128, buf[0]);
    Assert.assertEquals(2, buf[1]);
    buf = new byte[10];
    Assert.assertEquals(9, VarInt.putUvarint(buf, 0x7fffffffffffffffL)); // [-1, -1, -1, -1, -1, -1, -1, -1, 127, 0]
    Assert.assertEquals(-1, buf[0]);
    Assert.assertEquals(-1, buf[1]);
    Assert.assertEquals(127, buf[8]);
  }

  @Test
  public void testUvarint() {
    long[] tests = new long[]{0, 1234, 0xff, 256, 0x7fffffffffffffffL};
    for (long test : tests) {
      byte[] buf = new byte[10];
      VarInt.putUvarint(buf, test);
      Assert.assertEquals(test, VarInt.uvarint(buf));
    }
  }
}
