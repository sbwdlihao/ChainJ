package com.lihao.crypto;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sbwdlihao on 11/28/16.
 */
public class Sha3Test {

    @Test
    public void testSum256() {
        byte[] data = {0, 1, 2};
        byte[] hash = Sha3.Sum256(data);
        byte[] target = {17, -122, -44, -102, 74, -42, 32, 97, -113, 118, 15, 41, -38, 44, 89, 59, 46, -62, -52, 44, -19, 105, -36, 22, -127, 115, -112, -40, 97, -26, 34, 83,};
        Assert.assertArrayEquals(hash, target);

        byte[] hash1 = Sha3.Sum256(null);
        byte[] target1 = {-89, -1, -58, -8, -65, 30, -41, 102, 81, -63, 71, 86, -96, 97, -42, 98, -11, -128, -1, 77, -28, 59, 73, -6, -126, -40, 10, 75, -128, -8, 67, 74};
        Assert.assertArrayEquals(hash1, target1);
    }
}
