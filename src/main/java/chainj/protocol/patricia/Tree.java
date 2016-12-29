package chainj.protocol.patricia;

import chainj.crypto.Sha3;
import chainj.protocol.bc.Hash;
import com.google.common.primitives.Ints;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 27/12/2016.
 */
public class Tree {

    private static final int leafPrefix = 0x00;
    static final int interiorPrefix = 0x01;

    private static final String ErrPrefix = "key provided is a prefix to other keys";

    private Node root;

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public Tree() {
    }

    public Tree(Node root) {
        setRoot(root);
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
            setRoot(new Node(bitKey, hash, true));
            return;
        }

        setRoot(insert(root, bitKey, hash));
    }

    public boolean contains(byte[] key, byte[] value) throws IOException {
        if (root == null) {
            return false;
        }
        int[] bitKey = PatriciaUtil.bitKey(key);
        Node node = lookup(root, bitKey);
        return node != null && node.getHash().equals(valueHash(value));
    }

    // Delete removes up to one value with a matching key.
    // After removing the node, it will rearrange the tree
    // to the optimal structure.
    public void delete(byte[] key) {
        int[] bitKey = PatriciaUtil.bitKey(key);
        if (root == null) {
            return;
        }
        setRoot(delete(root, bitKey));
    }

    // copy returns a new tree with the same root as this tree. It
    // is an O(1) operation.
    public Tree copy() {
        return new Tree(root);
    }

    private Node delete(Node node, int[] key) {
        if (Arrays.equals(node.getKey(), key)) {
            if (!node.isLeaf()) {
                throw new IllegalArgumentException(ErrPrefix);
            }
            return null;
        }
        if (Ints.indexOf(key, node.getKey()) == -1) {
            return node;
        }
        int branch = key[node.getKey().length];
        Node newChild = delete(node.getChildren()[branch], key);
        if (newChild == null) {
            return node.getChildren()[1 - branch];
        }
        node.getChildren()[branch] = newChild;
        node.setHash(null);
        return node;
    }


    private Node insert(Node node, int[] key, Hash hash) {
        if (Arrays.equals(node.getKey(), key)) {
            if (!node.isLeaf()) {
                throw new IllegalArgumentException(ErrPrefix);
            }
            node.setHash(hash);
            node.setLeaf(true);
            return node;
        }

        if (Ints.indexOf(key, node.getKey()) != -1) {
            if (node.isLeaf()) {
                throw new IllegalArgumentException(ErrPrefix);
            }
            int childIndex = key[node.getKey().length];
            Node child = node.getChildren()[childIndex];
            child = insert(child, key, hash);
            node.getChildren()[childIndex] = child;
            node.setHash(null);
            return node;
        }

        int common = PatriciaUtil.commonPrefixLen(node.getKey(), key);
        int[] newNodeKey = Arrays.copyOfRange(key, 0, common);
        Node newNode = new Node(newNodeKey);
        newNode.getChildren()[key[common]] = new Node(key, hash, true);
        newNode.getChildren()[1 - key[common]] = node;
        return newNode;
    }

    Node lookup(Node node, int[] key) {
        if (Arrays.equals(key, node.getKey())) {
            return node.isLeaf() ? node : null;
        }
        if (Ints.indexOf(key, node.getKey()) == -1) {
            return null;
        }
        int branch = key[node.getKey().length];
        return lookup(node.getChildren()[branch], key);
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
