package com.lihao.encoding;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class VarInt {

  // golang中的PutUVarint支持的数值是从0到0xffffffffffffffff，而java中的支持的数值是0到0x7fffffffffffffff
  public static int putUvarint(byte[] buf, long x) {
    if (buf == null) {
      throw new IllegalArgumentException("buf cannot be null");
    }
    if (x < 0) {
      throw new IllegalArgumentException("x must be unsigned long");
    }
    int i = 0;
    while (x >= 0x80) { // 0x80 = 128 = 0b10000000
      if (buf.length <= i) {
        throw new IllegalArgumentException("buf size is too small");
      }
      buf[i++] = (byte) (x | 0x80);
      x >>= 7;
    }
    if (buf.length <= i) {
      throw new IllegalArgumentException("buf size is too small");
    }
    buf[i] = (byte)x;
    return i + 1;
  }

  public static long uvarint(byte[] buf) {
    if (buf == null || buf.length == 0) {
      throw new IllegalArgumentException("buf cannot be empty");
    }
    long x = 0;
    long s = 0;
    for (int i = 0; i < buf.length; i++) {
      byte b = buf[i];
      if (b >= 0) { // 0x00~0x7f
        if (i > 9 || i == 9 && b > 1) {
          throw new IllegalArgumentException("var is too large");
        }
        return x | (long)b << s;
      }
      x |= (long)(b&0x7f) << s;
      s += 7;
    }
    return x;
  }
}
