package chainj.protocol.mempool;

import chainj.protocol.bc.Hash;
import chainj.protocol.bc.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sbwdlihao on 30/12/2016.
 *
 * MemPool provides a Pool implementation that keeps
 * all pending transactions in memory.
 *
 * It is used in tests to avoid needing a database and is not
 * safe for concurrent access.
 */
public class MemPool {

    private List<Transaction> pool = new ArrayList<>();
    private Map<Hash, Boolean> hashes = new HashMap<>();

    private final Lock poolLock = new ReentrantLock();
    private final Logger logger = LogManager.getLogger();

    // Submit adds a new pending tx to the pending tx pool.
    public synchronized void submit(Transaction tx) {
        poolLock.lock();
        try {
            if (!hashes.containsKey(tx.getHash())) {
                hashes.put(tx.getHash(), true);
                pool.add(tx);
            }
        } finally {
            poolLock.unlock();
        }
    }

    public List<Transaction> dump() {
        poolLock.lock();
        List<Transaction> l;
        try {
            l = pool;
            pool = new ArrayList<>();
            hashes = new HashMap<>();
        } finally {
            poolLock.unlock();
        }
        if (!Sort.isTopologicalSorted(l)) {
            logger.info("set of %d txs not in topological order; sorting", l.size());
            l = Sort.topologicalSort(l);
        }
        return l;
    }
}
