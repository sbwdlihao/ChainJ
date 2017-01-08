package chainj.protocol.patricia;

import chainj.crypto.Sha3;
import chainj.protocol.bc.Hash;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 26/12/2016.
 *
 * node is a leaf or branch node in a tree
 */
class Node {

    private int[] key;
    private Hash hash;
    private boolean isLeaf;
    private Node[] children = new Node[2];

    int[] getKey() {
        return key;
    }

    Hash getHash() {
        return hash;
    }

    void setHash(Hash hash) {
        this.hash = hash;
    }

    boolean isLeaf() {
        return isLeaf;
    }

    void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    Node[] getChildren() {
        return children;
    }

    private void setChildren(Node[] children) {
        if (children == null || children.length != 2) {
            throw new IllegalArgumentException("children length must be 2");
        }
        this.children = children;
    }

    Node(int[] key) {
        this.key = key;
    }

    Node(int[] key, Hash hash, boolean isLeaf) {
        this.key = key;
        this.hash = hash;
        this.isLeaf = isLeaf;
    }

    Node(int[] key, Hash hash, Node[] children) {
        this.key = key;
        this.hash = hash;
        setChildren(children);
    }

    // Key returns the key for the current node as bytes, as it was provided to Insert.
    byte[] key() {
        return PatriciaUtil.byteKey(key);
    }

    // Hash will return the hash for this node.
    Hash hash() {
        calcHash();
        return hash;
    }

    private void calcHash() {
        if (hash != null) {
            return;
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(Tree.interiorPrefix);
        for (Node child : children) {
            if (child != null) {
                buf.write(child.hash().getValue(), 0, child.hash().getValue().length);
            }
        }
        setHash(new Hash(Sha3.sum256(buf.toByteArray())));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return isLeaf == node.isLeaf &&
                Arrays.equals(key, node.key) &&
                (hash != null ? hash.equals(node.hash) : node.hash == null) &&
                Arrays.equals(children, node.children);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(key);
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (isLeaf ? 1 : 0);
        result = 31 * result + Arrays.hashCode(children);
        return result;
    }
}
