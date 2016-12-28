package com.lihao.protocol.patricia;

import com.lihao.crypto.Sha3;
import com.lihao.protocol.bc.Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public int[] getKey() {
        return key;
    }

    public void setKey(int[] key) {
        this.key = key;
    }

    public Hash getHash() {
        return hash;
    }

    public void setHash(Hash hash) {
        this.hash = hash;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public Node[] getChildren() {
        return children;
    }

    public void setChildren(Node[] children) {
        if (children == null || children.length != 2) {
            throw new IllegalArgumentException("children length must be 2");
        }
        this.children = children;
    }

    public Node() {
    }

    public Node(int[] key) {
        setKey(key);
    }

    public Node(int[] key, Hash hash, boolean isLeaf) {
        setKey(key);
        setHash(hash);
        setLeaf(isLeaf);
    }

    public Node(int[] key, Hash hash, Node[] children) {
        setKey(key);
        setHash(hash);
        setChildren(children);
    }

    // Key returns the key for the current node as bytes, as it was provided to Insert.
    byte[] key() {
        return PatriciaUtil.byteKey(key);
    }

    // Hash will return the hash for this node.
    Hash hash() throws IOException {
        calcHash();
        return hash;
    }

    private void calcHash() throws IOException {
        if (hash != null) {
            return;
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(Tree.interiorPrefix);
        for (Node child : children) {
            if (child != null) {
                buf.write(child.hash().getValue());
            }
        }
        setHash(new Hash(Sha3.Sum256(buf.toByteArray())));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (isLeaf != node.isLeaf) return false;
        if (!Arrays.equals(key, node.key)) return false;
        if (hash != null ? !hash.equals(node.hash) : node.hash != null) return false;
        return Arrays.equals(children, node.children);
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
