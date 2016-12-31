package chainj.protocol;

import chainj.protocol.bc.Block;
import chainj.protocol.state.Snapshot;

/**
 * Created by sbwdlihao on 30/12/2016.
 *
 * Store provides storage for blockchain data: blocks and state tree
 * snapshots.
 *
 * Note, this is different from a state snapshot. A state snapshot
 * provides access to the state at a given point in time -- outputs
 * and issuance memory. The Chain type uses Store to load state
 * from storage and persist validated data.
 */
public interface Store {

    long height();

    Block getBlock(long height);

    void saveBlock(Block block);

    void finalizeBlock(long height);

    Snapshot latestSnapshot(long[] height);

    void saveSnapshot(long height, Snapshot snapshot);
}
