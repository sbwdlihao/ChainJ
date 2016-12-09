package com.lihao.crypto.ed25519.chainkd;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.SignatureException;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class UtilTest {

  private XPrv xPrv;
  private XPub xPub;

  @Before
  public void setup() {
    byte[] entropy = new byte[32];
    xPrv = Util.newXPrv(entropy);
    if (xPrv != null) {
      xPub = xPrv.xPub();
    }
  }

  @Test
  public void testHashKeySaltSelector() {
    byte[] key = {0,1,2};
    byte[] salt = {3,4,5};
    byte[] selt = {7,8};
    byte[] out = Util.hashKeySaltSelector((byte)0, key, salt, selt);
    Assert.assertNotNull(out);
    Assert.assertEquals(64, out.length);
    Assert.assertEquals(96, out[0] & 0xff);
    Assert.assertEquals(157, out[63] & 0xff);
  }

  @Test
  public void testNewXprv() {
    byte[] xPrvData = xPrv.bytes();
    Assert.assertEquals(64, xPrvData.length);
    Assert.assertEquals(120, xPrvData[0] & 0xff);
    Assert.assertEquals(104, xPrvData[31] & 0xff);

    byte[] xPubData = xPub.bytes();
    Assert.assertEquals(64, xPubData.length);
    Assert.assertEquals(218, xPubData[0] & 0xff);
    Assert.assertEquals(242, xPubData[63] & 0xff);
  }

  @Test
  public void testChild() {
    byte[] selt = {0,1};
    XPrv child = xPrv.child(selt, true);
    Assert.assertEquals(16, child.bytes()[0] & 0xff);
    Assert.assertEquals(201, child.bytes()[63] & 0xff);
    child = xPrv.child(selt, false);
    Assert.assertEquals(36, child.bytes()[0] & 0xff);
    Assert.assertEquals(10, child.bytes()[63] & 0xff);

    XPub pubChild = xPub.child(selt);
    Assert.assertEquals(60, pubChild.bytes()[0] & 0xff);
    Assert.assertEquals(10, pubChild.bytes()[63] & 0xff);
  }

  @Test
  public void testDerive() {
    byte[][] path = {
            {0,1},
            {3,4}
    };
    XPrv derive = xPrv.derive(path);
    Assert.assertEquals(246, derive.bytes()[0] & 0xff);
    Assert.assertEquals(162, derive.bytes()[63] & 0xff);

    XPub pubDerive = xPub.derive(path);
    Assert.assertEquals(103, pubDerive.bytes()[0] & 0xff);
    Assert.assertEquals(162, pubDerive.bytes()[63] & 0xff);
  }

  @Test
  public void testSignature() throws SignatureException, InvalidKeyException {
    byte[] msg = {0,1};
    byte[] sig = xPrv.sign(msg);
    Assert.assertEquals(173, sig[0] & 0xff);
    Assert.assertEquals(11, sig[63] & 0xff);

    Assert.assertEquals(true, xPub.verify(msg, sig));
  }

  @Test
  public void testMarshal() {
    Assert.assertEquals("78ce8a0989f5cebfefbee1bdcae71d4ae2757d9c7e0e8a404444774456d1bc681c199d4171a46bf486704c23522c2f962468afaffee0d6afc1fb84c1e4433ff2", new String(xPrv.marshalText()));
    Assert.assertEquals("da5ddf057de3cf8db186e619915b88f37f797a9be1a5a79195533741f8bec4a21c199d4171a46bf486704c23522c2f962468afaffee0d6afc1fb84c1e4433ff2", new String(xPub.marshalText()));

    Assert.assertArrayEquals(xPrv.unMarshalText(xPrv.marshalText()).bytes(), xPrv.bytes());
    Assert.assertArrayEquals(xPub.unMarshalText(xPub.marshalText()).bytes(), xPub.bytes());
  }
}
