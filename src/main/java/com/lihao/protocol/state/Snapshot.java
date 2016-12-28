package com.lihao.protocol.state;

import com.lihao.protocol.bc.Hash;
import com.lihao.protocol.patricia.Tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sbwdlihao on 28/12/2016.
 */
public class Snapshot {

    private Tree tree;
    // issuances maps an "issuance hash" to the time (in Unix millis)
    // at which it should expire from the issuance memory.
    private Map<Hash, Long> issuances;

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        Objects.requireNonNull(tree);
        this.tree = tree;
    }

    public Map<Hash, Long> getIssuances() {
        return issuances;
    }

    public void setIssuances(Map<Hash, Long> issuances) {
        Objects.requireNonNull(issuances);
        this.issuances = issuances;
    }

    public Snapshot() {
        setTree(new Tree());
        setIssuances(new HashMap<>());
    }

    // pruneIssuances modifies a Snapshot, removing all issuance hashes
    // with expiration times earlier than the provided timestamp.
    public void pruneIssuances(long timestampMS) {
        issuances.entrySet().removeIf(e->timestampMS>e.getValue());
    }

    // copy makes a copy of provided snapshot. Copying a snapshot is an
    // O(n) operation where n is the number of issuance hashes in the
    // snapshot's issuance memory.
    public Snapshot copy() {
        Snapshot copy = new Snapshot();
        copy.setTree(tree.copy());
        copy.setIssuances(new HashMap<>(issuances)); // map is a shadow copy
        return copy;
    }
}
