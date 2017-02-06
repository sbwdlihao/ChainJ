package chainj.protocol.tx;

import chainj.Case;
import chainj.protocol.bc.*;
import chainj.protocol.bc.txinput.SpendInput;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sbwdlihao on 06/02/2017.
 */
public class TransactionTest {

    static TxData sampleTx() {
        AssetID assetID = AssetID.computeAssetID(
                new byte[]{1},
                BCTest.mustDecodeHash("03deff1d4319d67baa10a6d26c1fea9c3e8d30e33474efee1a610a9bb49d758d"),
                1
        );
        return new TxData(
                1,
                new TxInput[]{
                        new SpendInput(
                                new OutputID(BCTest.mustDecodeHash("dd385f6fe25d91d8c1bd0fa58951ad56b0c5229dcc01f61d9f9e8b9eb92d3292")),
                                new byte[][]{},
                                assetID,
                                1000000000000L,
                                new byte[]{1},
                                "input".getBytes()
                        ),
                        new SpendInput(
                                new OutputID(new Hash((byte)17)),
                                new byte[][]{},
                                assetID,
                                1,
                                new byte[]{2},
                                "input2".getBytes()
                        ),
                },
                new TxOutput[]{
                        new TxOutput(assetID, 600000000000L, new byte[]{1}, new byte[]{}),
                        new TxOutput(assetID, 400000000000L, new byte[]{2}, new byte[]{}),
                },
                1492590000,
                1492590591,
                "distribution".getBytes()
        );
    }

    @Test
    public void testTxHashes() {
        List<Case<TxData, Hash>> cases = new ArrayList<>(Arrays.asList(
                new Case<>(
                        new TxData(),
                        BCTest.mustDecodeHash("827b87bafb63c999922d0190010351435bb73a4d96612beea007b4d811607fb0")
                ),
                new Case<>(
                        sampleTx(),
                        BCTest.mustDecodeHash("fbca3b1e447c46f7926931950960b60fc86237a9402ce68c78e6144da02a5d82")
                )
        ));

        cases.forEach(c -> {
            TxHashes hashes = Transaction.txHashes(c.data);
            Assert.assertEquals(c.data.getInputs().length, hashes.getVmContexts().length);
            Assert.assertEquals(c.want, hashes.getId());
        });
    }
}
