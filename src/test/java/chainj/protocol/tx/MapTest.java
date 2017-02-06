package chainj.protocol.tx;

import chainj.protocol.bc.TxData;
import chainj.protocol.bc.TxOutput;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by sbwdlihao on 06/02/2017.
 */
public class MapTest {

    @Test
    public void testMapTx() {
        TxData oldTx = TransactionTest.sampleTx();
        TxOutput[] oldOuts = oldTx.getOutputs();
        MapTxResult result = Map.mapTx(oldTx);
        Header header = result.header;
        java.util.Map<EntryRef, EntryInterface> entryMap = result.entryMap;

        Assert.assertEquals(1, header.getBody().version);
        Assert.assertEquals(oldTx.getMinTime(), header.getBody().minTimeMS);
        Assert.assertEquals(oldTx.getMaxTime(), header.getBody().maxTimeMS);
        Assert.assertEquals(oldOuts.length, header.getBody().results.length);

        for (int i = 0; i < oldOuts.length; i++) {
            TxOutput oldOut = oldOuts[i];
            Assert.assertTrue(entryMap.containsKey(header.getBody().results[i]));
            EntryInterface entryInterface = entryMap.get(header.getBody().results[i]);
            Assert.assertTrue(entryInterface instanceof Output);
            Output newOut = (Output)entryInterface;
            Assert.assertEquals(new AssetAmount(oldOut.getAssetAmount()), newOut.getBody().valueSource.value);
            Assert.assertEquals(1, newOut.getBody().controlProgram.vmVersion);
            Assert.assertArrayEquals(oldOut.getControlProgram(), newOut.getBody().controlProgram.code);
            Assert.assertEquals(new EntryRef(), newOut.getBody().data);
            Assert.assertEquals(new ExtHash(), newOut.getBody().extHash);
        }
    }
}
