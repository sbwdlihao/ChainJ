package com.lihao.protocol.patricia;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.lihao.crypto.Sha3;
import com.lihao.protocol.bc.Hash;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 27/12/2016.
 */
public class PatriciaTest {

    @Test
    public void testRootHashBug() throws IOException {
        Tree tr = new Tree();
        tr.insert(new byte[]{(byte)0x94}, new byte[]{(byte)0x01});
        tr.insert(new byte[]{(byte)0x36}, new byte[]{(byte)0x02});
        Hash before = tr.rootHash();
        tr.insert(new byte[]{(byte)0xba}, new byte[]{(byte)0x03});
        Hash after = tr.rootHash();
        Assert.assertNotEquals(after, before);
    }

    @Test
    public void testLeafVsInternalNodes() throws IOException {
        Tree tr0 = new Tree();
        tr0.insert(new byte[]{(byte)0x01}, new byte[]{(byte)0x01});
        tr0.insert(new byte[]{(byte)0x02}, new byte[]{(byte)0x02});
        tr0.insert(new byte[]{(byte)0x03}, new byte[]{(byte)0x03});
        tr0.insert(new byte[]{(byte)0x04}, new byte[]{(byte)0x04});

        // Create a second tree using an internal node from tr1.
        Tree tr1 = new Tree();
        tr1.insert(new byte[]{(byte)0x02}, Hex.decode("82b08f644c16985d2d9961b4104cc4bf4ba2be6bb5c3d0df2ecb94149f212fc9")); // this is an internal node of tr0
        tr1.insert(new byte[]{(byte)0x04}, new byte[]{(byte)0x04});
        Assert.assertNotEquals(tr0.rootHash(), tr1.rootHash());
    }

    @Test
    public void testInsert() throws IOException {
        byte[][] values = new byte[6][];
        Hash[] hashes = new Hash[6];
        makeValues(values, hashes);

        Tree tr = new Tree();
        tr.insert(bits("11111111"), values[0]);
        tr.rootHash();
        Tree want = new Tree(new Node(bools("11111111"), hashes[0], true));
        Assert.assertEquals(want, tr);

        tr.insert(bits("11111111"), values[1]);
        tr.rootHash();
        want = new Tree(new Node(bools("11111111"), hashes[1], true));
        Assert.assertEquals(want, tr);

        tr.insert(bits("11110000"), values[2]);
        tr.rootHash();
        want = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[2], hashes[1]), new Node[]{
                new Node(bools("11110000"), hashes[2], true),
                new Node(bools("11111111"), hashes[1], true),
        }));
        Assert.assertEquals(want, tr);

        tr.insert(bits("11111100"), values[3]);
        tr.rootHash();
        want = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[2], hashForNonLeaf(hashes[3], hashes[1])), new Node[]{
                new Node(bools("11110000"), hashes[2], true),
                new Node(bools("111111"), hashForNonLeaf(hashes[3], hashes[1]), new Node[]{
                        new Node(bools("11111100"), hashes[3], true),
                        new Node(bools("11111111"), hashes[1], true),
                }),
        }));
        Assert.assertEquals(want, tr);

        tr.insert(bits("11111110"), values[4]);
        tr.rootHash();
        want = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[2], hashForNonLeaf(hashes[3], hashForNonLeaf(hashes[4], hashes[1]))), new Node[]{
                new Node(bools("11110000"), hashes[2], true),
                new Node(bools("111111"), hashForNonLeaf(hashes[3], hashForNonLeaf(hashes[4], hashes[1])), new Node[]{
                        new Node(bools("11111100"), hashes[3], true),
                        new Node(bools("1111111"), hashForNonLeaf(hashes[4], hashes[1]), new Node[]{
                                new Node(bools("11111110"), hashes[4], true),
                                new Node(bools("11111111"), hashes[1], true),
                        }),
                }),
        }));
        Assert.assertEquals(want, tr);

        tr.insert(bits("11111011"), values[5]);
        tr.rootHash();
        want = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[2], hashForNonLeaf(hashes[5], hashForNonLeaf(hashes[3], hashForNonLeaf(hashes[4], hashes[1])))), new Node[]{
                new Node(bools("11110000"), hashes[2], true),
                new Node(bools("11111"), hashForNonLeaf(hashes[5], hashForNonLeaf(hashes[3], hashForNonLeaf(hashes[4], hashes[1]))), new Node[]{
                        new Node(bools("11111011"), hashes[5], true),
                        new Node(bools("111111"), hashForNonLeaf(hashes[3], hashForNonLeaf(hashes[4], hashes[1])), new Node[]{
                                new Node(bools("11111100"), hashes[3], true),
                                new Node(bools("1111111"), hashForNonLeaf(hashes[4], hashes[1]), new Node[]{
                                        new Node(bools("11111110"), hashes[4], true),
                                        new Node(bools("11111111"), hashes[1], true),
                                }),
                        }),
                }),
        }));
        Assert.assertEquals(want, tr);
    }

    @Test
    public void testLookup() throws IOException {
        byte[][] values = new byte[5][];
        Hash[] hashes = new Hash[5];
        makeValues(values, hashes);

        Tree tr = new Tree(new Node(bools("11111111"), hashes[0], true));
        Node got = tr.lookup(tr.getRoot(), PatriciaUtil.bitKey(bits("11111111")));
        Assert.assertEquals(tr.getRoot(), got);

        tr = new Tree(new Node(bools("11111110"), hashes[0], true));
        got = tr.lookup(tr.getRoot(), PatriciaUtil.bitKey(bits("11111111")));
        Assert.assertNull(got);

        tr = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[2], hashes[1]), new Node[]{
                new Node(bools("11110000"), hashes[2], true),
                new Node(bools("11111111"), hashes[1], true),
        }));
        got = tr.lookup(tr.getRoot(), PatriciaUtil.bitKey(bits("11110000")));
        Assert.assertEquals(tr.getRoot().getChildren()[0], got);

        tr = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[2], hashForNonLeaf(hashes[3], hashes[1])), new Node[]{
                new Node(bools("11110000"), hashes[2], true),
                new Node(bools("111111"), hashForNonLeaf(hashes[3], hashes[1]), new Node[]{
                        new Node(bools("11111100"), hashes[3], true),
                        new Node(bools("11111111"), hashes[1], true),
                }),
        }));
        got = tr.lookup(tr.getRoot(), PatriciaUtil.bitKey(bits("11111100")));
        Assert.assertEquals(tr.getRoot().getChildren()[1].getChildren()[0], got);
    }

    @Test
    public void testContains() throws IOException {
        byte[][] values = new byte[4][];
        Hash[] hashes = new Hash[4];
        makeValues(values, hashes);

        Tree tr = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[2], hashForNonLeaf(hashes[3], hashes[1])), new Node[]{
                new Node(bools("11110000"), hashes[2], true),
                new Node(bools("111111"), hashForNonLeaf(hashes[3], hashes[1]), new Node[]{
                        new Node(bools("11111100"), hashes[3], true),
                        new Node(bools("11111111"), hashes[1], true),
                }),
        }));

        Assert.assertTrue(tr.contains(bits("11111100"), values[3]));
        Assert.assertFalse(tr.contains(bits("11111111"), values[3]));
    }

    @Test
    public void testDelete() throws IOException {
        byte[][] values = new byte[4][];
        Hash[] hashes = new Hash[4];
        makeValues(values, hashes);

        Tree tr = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[0], hashForNonLeaf(hashes[1], hashForNonLeaf(hashes[2], hashes[3]))), new Node[]{
                new Node(bools("11110000"), hashes[0], true),
                new Node(bools("111111"), hashForNonLeaf(hashes[1], hashForNonLeaf(hashes[2], hashes[3])), new Node[]{
                        new Node(bools("11111100"), hashes[1], true),
                        new Node(bools("1111111"), hashForNonLeaf(hashes[2], hashes[3]), new Node[]{
                                new Node(bools("11111110"), hashes[2], true),
                                new Node(bools("11111111"), hashes[3], true),
                        }),
                }),
        }));
        tr.delete(bits("11111110"));
        tr.rootHash();
        Tree want = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[0], hashForNonLeaf(hashes[1], hashes[3])), new Node[]{
                new Node(bools("11110000"), hashes[0], true),
                new Node(bools("111111"), hashForNonLeaf(hashes[1], hashes[3]), new Node[]{
                        new Node(bools("11111100"), hashes[1], true),
                        new Node(bools("11111111"), hashes[3], true),
                }),
        }));
        Assert.assertEquals(want, tr);

        tr.delete(bits("11111100"));
        tr.rootHash();
        want = new Tree(new Node(bools("1111"), hashForNonLeaf(hashes[0], hashes[3]), new Node[]{
                new Node(bools("11110000"), hashes[0], true),
                new Node(bools("11111111"), hashes[3], true),
        }));
        Assert.assertEquals(want, tr);

        tr.delete(bits("11110011"));
        tr.rootHash();
        Assert.assertEquals(want, tr);

        tr.delete(bits("11110000"));
        tr.rootHash();
        want = new Tree(new Node(bools("11111111"), hashes[3], true));
        Assert.assertEquals(want, tr);

        tr.delete(bits("11111111"));
        tr.rootHash();
        want = new Tree();
        Assert.assertEquals(want, tr);
    }

    private static void makeValues(byte[][] values, Hash[] hashes) {
        for (int i = 0; i < values.length; i++) {
            values[i] = Sha3.Sum256(new byte[]{(byte)i});
            hashes[i] = new Hash(Sha3.Sum256(Bytes.concat(new byte[]{0x00}, values[i])));
        }
    }

    private static byte[] bits(String list) {
        byte[] b = new byte[32];
        b[31] = (byte)Integer.parseInt(list, 2);
        return b;
    }

    private static int[] bools(String list) {
        int[] b = PatriciaUtil.bitKey(bits(list));
        int[] b1 = Arrays.copyOfRange(b, 0, 31 * 8);
        int[] b2 = Arrays.copyOfRange(b, 32 * 8 - list.length(), 32 * 8);
        return Ints.concat(b1, b2);
    }

    private static Hash hashForNonLeaf(Hash a, Hash b) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(Tree.interiorPrefix);
        buf.write(a.getValue());
        buf.write(b.getValue());
        return new Hash(Sha3.Sum256(buf.toByteArray()));
    }
}
