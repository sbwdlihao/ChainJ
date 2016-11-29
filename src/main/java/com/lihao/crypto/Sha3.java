package com.lihao.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class Sha3 {
  public static MessageDigest Get256() throws NoSuchAlgorithmException {
    return MessageDigest.getInstance("SHA3-256");
  }

  public static byte[] Sum256(byte[] data) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA3-256");
    digest.update(data);
    return digest.digest();
  }
}
