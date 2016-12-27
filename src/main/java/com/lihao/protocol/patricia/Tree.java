package com.lihao.protocol.patricia;

import com.google.common.primitives.Ints;
import com.lihao.crypto.Sha3;
import com.lihao.protocol.bc.Hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 27/12/2016.
 */
public class Tree {

    static final int leafPrefix = 0x00;
    static final int interiorPrefix = 0x01;

    private static final String ErrPrefix = "key provided is a prefix to other keys";

    Node root;

    public Tree() {
    }

    public Tree(Node root) {
        this.root = root;
    }

    // Insert enters data into the tree.
    // If the key is not already present in the tree,
    // a new node will be created and inserted,
    // rearranging the tree to the optimal structure.
    // If the key is present, the existing node is found
    // and its value is updated, leaving the structure of
    // the tree alone.
    public void insert(byte[] key, byte[] value) throws IOException {
        int[] bitKey = PatriciaUtil.bitKey(key);
        Hash hash = valueHash(value);

        if (root == null) {
            root = new Node(bitKey, hash, true);
            return;
        }

        root = insert(root, bitKey, hash);
    }

    public boolean contains(byte[] key, byte[] value) throws IOException {
        if (root == null) {
            return false;
        }
        int[] bitKey = PatriciaUtil.bitKey(key);
        Node node = lookup(root, bitKey);
        return node != null && node.hash.equals(valueHash(value));
    }

    // Delete removes up to one value with a matching key.
    // After removing the node, it will rearrange the tree
    // to the optimal structure.
    public void delete(byte[] key) {
        int[] bitKey = PatriciaUtil.bitKey(key);
        if (root == null) {
            return;
        }
        root = delete(root, bitKey);
    }

    private Node delete(Node node, int[] key) {
        if (Arrays.equals(node.key, key)) {
            if (!node.isLeaf) {
                throw new IllegalArgumentException(ErrPrefix);
            }
            return null;
        }
        if (Ints.indexOf(key, node.key) == -1) {
            return node;
        }
        int branch = key[node.key.length];
        Node newChild = delete(node.children[branch], key);
        if (newChild == null) {
            return node.children[1 - branch];
        }
        node.children[branch] = newChild;
        node.hash = null;
        return node;
    }


    private Node insert(Node node, int[] key, Hash hash) {
        if (Arrays.equals(node.key, key)) {
            if (!node.isLeaf) {
                throw new IllegalArgumentException(ErrPrefix);
            }
            node.hash = hash;
            node.isLeaf = true;
            return node;
        }

        if (Ints.indexOf(key, node.key) != -1) {
            if (node.isLeaf) {
                throw new IllegalArgumentException(ErrPrefix);
            }
            int childIndex = key[node.key.length];
            Node child = node.children[childIndex];
            child = insert(child, key, hash);
            node.children[childIndex] = child;
            node.hash = null;
            return node;
        }

        int common = PatriciaUtil.commonPrefixLen(node.key, key);
        int[] newNodeKey = Arrays.copyOfRange(key, 0, common);
        Node newNode = new Node(newNodeKey);
        newNode.children[key[common]] = new Node(key, hash, true);
        newNode.children[1 - key[common]] = node;
        return newNode;
    }

    Node lookup(Node node, int[] key) {
        if (Arrays.equals(key, node.key)) {
            return node.isLeaf ? node : null;
        }
        if (Ints.indexOf(key, node.key) == -1) {
            return null;
        }
        int branch = key[node.key.length];
        return lookup(node.children[branch], key);
    }

    Hash rootHash() throws IOException {
        if (root == null) {
            return new Hash();
        }
        return root.hash();
    }

    private Hash valueHash(byte[] value) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(leafPrefix);
        buf.write(value);
        return new Hash(Sha3.Sum256(buf.toByteArray()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tree tree = (Tree) o;

        return root != null ? root.equals(tree.root) : tree.root == null;
    }

    @Override
    public int hashCode() {
        return root != null ? root.hashCode() : 0;
    }
}
