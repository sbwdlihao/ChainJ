package chainj.protocol.mempool;

import chainj.protocol.bc.Hash;
import chainj.protocol.bc.Transaction;
import chainj.protocol.bc.TxInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sbwdlihao on 31/12/2016.
 */
class Sort {

    // 将交易之间按照输入依赖关系进行拓扑排序，比如tx1的输入依赖于tx0的输出，那么tx1排在tx0之后
    // 使用Kahn算法
    static List<Transaction> topologicalSort(List<Transaction> pool) {
        if (pool.size() < 2) {
            return pool;
        }
        Map<Hash, Transaction> nodes = new HashMap<>();
        pool.forEach(tx -> nodes.put(tx.getHash(), tx));
        Map<Hash, Integer> incomingEdges = new HashMap<>();
        Map<Hash, List<Hash>> children = new HashMap<>();
        nodes.forEach((hash, tx) -> {
            for (TxInput txInput : tx.getInputs()) {
                if (txInput.isIssuance()) {
                    continue;
                }
                Hash prevTxHash = txInput.outpoint().getHash();
                if (nodes.containsKey(prevTxHash)) {
                    Integer edges = incomingEdges.get(hash);
                    if (edges == null) {
                        edges = 0;
                    }
                    edges += 1;
                    incomingEdges.put(hash, edges);
                    List<Hash> childList = children.get(prevTxHash);
                    if (childList == null) {
                        childList = new ArrayList<>();
                    }
                    childList.add(hash);
                    children.put(prevTxHash, childList);
                }
            }
        });

        List<Transaction> l = new ArrayList<>();
        List<Hash> s = new ArrayList<>();
        nodes.forEach((hash, tx) -> {
            if (incomingEdges.get(hash) == null) {
                s.add(hash);
            }
        });

        while(s.size() > 0) {
            Hash hash = s.get(0);
            l.add(nodes.get(hash));
            List<Hash> childList = children.get(hash);
            if (childList != null && childList.size() > 0) {
                childList.forEach(child -> {
                    Integer edges = incomingEdges.get(child);
                    edges -= 1;
                    if (edges == 0) {
                        s.add(child);
                        incomingEdges.remove(child);
                    } else {
                        incomingEdges.put(child, edges);
                    }
                });
            }
        }

        if (incomingEdges.size() > 0) {
            throw new IllegalTransactionDependencyException("cyclical tx ordering");
        }
        return l;
    }

    static boolean isTopologicalSorted(List<Transaction> pool) {
        if (pool.size() < 2) {
            return true;
        }
        Map<Hash, Boolean> exists = new HashMap<>();
        Map<Hash, Boolean> seen = new HashMap<>();
        pool.forEach(tx -> exists.put(tx.getHash(), true));
        return pool.stream().allMatch(tx -> {
            for (TxInput txInput : tx.getInputs()) {
                if (txInput.isIssuance()) {
                    continue;
                }
                Hash h = txInput.outpoint().getHash();
                if (exists.containsKey(h) && !seen.containsKey(h)) {
                    return false;
                }
                seen.put(tx.getHash(), true);
            }
            return true;
        });
    }
}
