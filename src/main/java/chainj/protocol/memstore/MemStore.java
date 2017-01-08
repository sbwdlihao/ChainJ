package chainj.protocol.memstore;

import chainj.protocol.Store;
import chainj.protocol.bc.Block;
import chainj.protocol.state.Snapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by sbwdlihao on 30/12/2016.
 *
 * MemStore provides a Store implementation that
 * It is used in tests to avoid needing a database.
 */
public class MemStore implements Store{

    private final Lock blocksLock = new ReentrantLock();
    private final Lock snapshotLock = new ReentrantLock();

    private Map<Long, Block> blocks = new HashMap<>();
    private Snapshot state;
    private long stateHeight;

    private final Logger logger = LogManager.getLogger();

    @Override
    public long height() {
        blocksLock.lock();
        long height;
        try {
            height = blocks.size(); // Map的size最多返回Integer.MAX_VALUE，不符合接口需求，不过对于内存测试而言足够了
        } finally {
            blocksLock.unlock();
        }
        return height;
    }

    @Override
    public Block getBlock(long height) {
        blocksLock.lock();
        Block block;
        try {
            block = blocks.get(height);
        } finally {
            blocksLock.unlock();
        }
        return block;
    }

    @Override
    public void saveBlock(Block block) {
        Objects.requireNonNull(block);

        blocksLock.lock();
        try {
            long height = block.getHeight();
            Block existing = blocks.get(height);
            if (existing != null && existing.getHash() != block.getHash()) {
                throw new IllegalArgumentException("already have a block at getHeight " + height);
            }
            blocks.put(height, block);
        } finally {
            blocksLock.unlock();
        }
    }

    @Override
    public void finalizeBlock(long height) {}

    @Override
    public Snapshot latestSnapshot(long[] height) {
        if (height == null || height.length == 0) {
            throw new IllegalArgumentException("getHeight length must >1");
        }
        snapshotLock.lock();
        Snapshot snapshot;
        try {
            if (state == null) {
                state = new Snapshot();
            }
            snapshot = state.copy();
            height[0] = stateHeight;
        } finally {
            snapshotLock.unlock();
        }
        return snapshot;
    }

    @Override
    public void saveSnapshot(long height, Snapshot snapshot) {
        snapshotLock.lock();
        try {
            state = snapshot.copy();
            stateHeight = height;
        } finally {
            snapshotLock.unlock();
        }
    }
}
