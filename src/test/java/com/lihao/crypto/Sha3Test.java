package com.lihao.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class Sha3Test {
  @BeforeClass
  public static void before() {
    Security.addProvider(new BouncyCastleProvider());
  }

  @Test
  public void testSum256() throws NoSuchAlgorithmException {
    byte[] data = {0,1,2};
    byte[] hash = Sha3.Sum256(data);
    byte[] target = {17,-122,-44,-102,74,-42,32,97,-113,118,15,41,-38,44,89,59,46,-62,-52,44,-19,105,-36,22,-127,115,-112,-40,97,-26,34,83,};
    Assert.assertArrayEquals(hash, target);
  }
}
