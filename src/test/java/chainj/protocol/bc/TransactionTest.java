package chainj.protocol.bc;

import chainj.protocol.bc.exception.BadAssetIDException;
import chainj.protocol.bc.txinput.IssuanceInput;
import chainj.protocol.bc.txinput.SpendInput;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sbwdlihao on 23/12/2016.
 */
public class TransactionTest {

    @Test
    public void testTransaction() {
        byte[] issuanceScript = new byte[]{1};
        String initialBlockHashHex = "03deff1d4319d67baa10a6d26c1fea9c3e8d30e33474efee1a610a9bb49d758d";
        Hash initialBlockHash = BCTest.mustDecodeHash(initialBlockHashHex);
        AssetID assetID = AssetID.computeAssetID(issuanceScript, initialBlockHash, 1);

        TestTransactionCase[] cases = new TestTransactionCase[]{
                new TestTransactionCase(
                        new Transaction(
                                new TxData(1, new TxInput[0], new TxOutput[0], 0, 0, new byte[0])
                        ),
                        "07" + // serflags
                        "01" + // transaction version
                        "02" + // common fields extensible string length
                        "00" + // common fields, mintime
                        "00" + // common fields, maxtime
                        "00" + // common witness extensible string length
                        "00" + // inputs count
                        "00" + // outputs count
                        "00" // reference data
                        ,
                        BCTest.mustDecodeHash("359a3b1987538bce4369d1e73f1e77b6f04f01fdbb46c627564732c74b49a337")
                ),
                new TestTransactionCase(
                        new Transaction(
                                new TxData(1, new TxInput[]{
                                        new IssuanceInput(new byte[]{10,9,8}, 1000000000000L, "input".getBytes(), initialBlockHash, issuanceScript, new byte[][]{new byte[]{1,2,3}}, new byte[0])
                                }, new TxOutput[]{
                                        new TxOutput(new AssetID(), 1000000000000L, new byte[]{1}, "output".getBytes())
                                }, 0, 0, "issuance".getBytes())
                        ),
                        "07" + // serflags
                        "01" + // transaction version
                        "02" + // common fields extensible string length
                        "00" + // common fields, mintime
                        "00" + // common fields, maxtime
                        "00" + // common witness extensible string length
                        "01" + // inputs count
                        "01" + // input 0, asset version
                        "2b" + // input 0, input commitment length prefix
                        "00" + // input 0, input commitment, "issuance" type
                        "03" + // input 0, input commitment, nonce length prefix
                        "0a0908" + // input 0, input commitment, nonce
                        assetID.toString() + // input 0, input commitment, asset id
                        "80a094a58d1d" + // input 0, input commitment, amount
                        "05696e707574" + // input 0, reference data
                        "29" + // input 0, issuance input witness length prefix
                        initialBlockHashHex + // input 0, issuance input witness, initial block
                        "00" + // input 0, issuance input witness, asset definition
                        "01" + // input 0, issuance input witness, vm version
                        "01" + // input 0, issuance input witness, issuance program length prefix
                        "01" + // input 0, issuance input witness, issuance program
                        "01" + // input 0, issuance input witness, arguments count
                        "03" + // input 0, issuance input witness, argument 0 length prefix
                        "010203" + // input 0, issuance input witness, argument 0
                        "01" + // outputs count
                        "01" + // output 0, asset version
                        "29" + // output 0, output commitment length
                        "0000000000000000000000000000000000000000000000000000000000000000" + // output 0, output commitment, asset id
                        "80a094a58d1d" + // output 0, output commitment, amount
                        "01" + // output 0, output commitment, vm version
                        "0101" + // output 0, output commitment, control program
                        "066f7574707574" + // output 0, reference data
                        "00" + // output 0, output witness
                        "0869737375616e6365" // reference data
                        ,
                        BCTest.mustDecodeHash("9567a15d24e906fd6ab65ae76ab6a45da41c47ea212a0e9c593a7b45c5ddfbbe")
                ),
                new TestTransactionCase(
                        new Transaction(
                                new TxData(1, new TxInput[]{
                                        new SpendInput(new OutputID(BCTest.mustDecodeHash("dd385f6fe25d91d8c1bd0fa58951ad56b0c5229dcc01f61d9f9e8b9eb92d3292")), new byte[0][], new AssetID(), 1000000000000L, new byte[]{1}, "input".getBytes())
                                }, new TxOutput[]{
                                        new TxOutput(AssetID.computeAssetID(issuanceScript, initialBlockHash, 1), 600000000000L, new byte[]{1}, new byte[0]),
                                        new TxOutput(AssetID.computeAssetID(issuanceScript, initialBlockHash, 1), 400000000000L, new byte[]{2}, new byte[0]),
                                }, 1492590000, 1492590591, "distribution".getBytes())
                        ),
                        "07" + // serflags
                        "01" + // transaction version
                        "0a" + // common fields extensible string length
                        "b0bbdcc705" + // common fields, mintime
                        "ffbfdcc705" + // common fields, maxtime
                        "00" + // common witness extensible string length
                        "01" + // inputs count
                        "01" + // input 0, asset version
                        "4b" + // input 0, input commitment length prefix
                        "01" + // input 0, input commitment, "spend" type
                        "dd385f6fe25d91d8c1bd0fa58951ad56b0c5229dcc01f61d9f9e8b9eb92d3292" + // input 0, spend input commitment, outpoint tx hash
                        "29" + // input 0, spend input commitment, output commitment length prefix
                        "0000000000000000000000000000000000000000000000000000000000000000" + // input 0, spend input commitment, output commitment, asset id
                        "80a094a58d1d" + // input 0, spend input commitment, output commitment, amount
                        "01" + // input 0, spend input commitment, output commitment, vm version
                        "0101" + // input 0, spend input commitment, output commitment, control program
                        "05696e707574" + // input 0, reference data
                        "01" + // input 0, input witness length prefix
                        "00" + // input 0, input witness, number of args
                        "02" + // outputs count
                        "01" + // output 0, asset version
                        "29" + // output 0, output commitment length
                        "a9b2b6c5394888ab5396f583ae484b8459486b14268e2bef1b637440335eb6c1" + // output 0, output commitment, asset id
                        "80e0a596bb11" + // output 0, output commitment, amount
                        "01" + // output 0, output commitment, vm version
                        "0101" + // output 0, output commitment, control program
                        "00" + // output 0, reference data
                        "00" + // output 0, output witness
                        "01" + // output 1, asset version
                        "29" + // output 1, output commitment length
                        "a9b2b6c5394888ab5396f583ae484b8459486b14268e2bef1b637440335eb6c1" + // output 1, output commitment, asset id
                        "80c0ee8ed20b" + // output 1, output commitment, amount
                        "01" + // output 1, vm version
                        "0102" + // output 1, output commitment, control program
                        "00" + // output 1, reference data
                        "00" + // output 1, output witness
                        "0c646973747269627574696f6e" // reference data
                        ,
                        BCTest.mustDecodeHash("c1429e3145f22844d131b4e8b177dbfc358ec760e11f4e1084743089761ee198")
                ),
        };
        for (TestTransactionCase aCase : cases) {
            byte[] got = BCTest.serialize(aCase.tx);
            byte[] want = Hex.decode(aCase.hex);
            Assert.assertArrayEquals(want, got);
            Assert.assertEquals(aCase.hash, aCase.tx.ID());
        }
    }

    @Test
    public void testHasIssuance() {
        TestHasIssuanceCase[] cases = new TestHasIssuanceCase[] {
                new TestHasIssuanceCase(
                        new TxData(
                                new TxInput[]{
                                        new IssuanceInput(new byte[0], 0, new byte[0], new Hash(), new byte[0], new byte[0][], new byte[0])}),
                        true),
                new TestHasIssuanceCase(
                        new TxData(
                                new TxInput[]{
                                        new SpendInput(new OutputID(), new byte[0][], new AssetID(), 0, new byte[0], new byte[0]),
                                        new IssuanceInput(new byte[0], 0, new byte[0], new Hash(), new byte[0], new byte[0][], new byte[0])
                                }),
                        true),
                new TestHasIssuanceCase(
                        new TxData(
                                new TxInput[]{
                                        new SpendInput(new OutputID(), new byte[0][], new AssetID(), 0, new byte[0], new byte[0]),
                                }),
                        false),
                new TestHasIssuanceCase(
                        new TxData(),
                        false),
        };
        for (TestHasIssuanceCase aCase : cases) {
            Assert.assertEquals(aCase.want, aCase.data.hasIssuance());
        }
    }

    @Test(expected = BadAssetIDException.class)
    public void testInvalidIssuance() throws IOException {
        String hex =
                "07" + // serflags
                "01" + // transaction version
                "02" + // common fields extensible string length
                "00" + // common fields, mintime
                "00" + // common fields, maxtime
                "00" + // common witness extensible string length
                "01" + // inputs count
                "01" + // input 0, asset version
                "2b" + // input 0, input commitment length prefix
                "00" + // input 0, input commitment, "issuance" type
                "03" + // input 0, input commitment, nonce length prefix
                "0a0908" + // input 0, input commitment, nonce
                "0000000000000000000000000000000000000000000000000000000000000000" + // input 0, input commitment, WRONG asset id
                "80a094a58d1d" + // input 0, input commitment, amount
                "05696e707574" + // input 0, reference data
                "29" + // input 0, issuance input witness length prefix
                "03deff1d4319d67baa10a6d26c1fea9c3e8d30e33474efee1a610a9bb49d758d" + // input 0, issuance input witness, initial block
                "00" + // input 0, issuance input witness, asset definition
                "01" + // input 0, issuance input witness, vm version
                "01" + // input 0, issuance input witness, issuance program length prefix
                "01" + // input 0, issuance input witness, issuance program
                "01" + // input 0, issuance input witness, arguments count
                "03" + // input 0, issuance input witness, argument 0 length prefix
                "010203" + // input 0, issuance input witness, argument 0
                "01" + // outputs count
                "01" + // output 0, asset version
                "29" + // output 0, output commitment length
                "0000000000000000000000000000000000000000000000000000000000000000" + // output 0, output commitment, asset id
                "80a094a58d1d" + // output 0, output commitment, amount
                "01" + // output 0, output commitment, vm version
                "0101" + // output 0, output commitment, control program
                "066f7574707574" + // output 0, reference data
                "00" + // output 0, output witness
                "0869737375616e6365";
        TxData data = new TxData();
        data.unMarshalText(hex.getBytes());
    }

    @Test
    public void testEmptyOutpoint() {
        Outpoint outpoint = new Outpoint();
        Assert.assertEquals(outpoint.toString(), "0000000000000000000000000000000000000000000000000000000000000000:0");
    }

    @Test
    public void testIssuanceOutpoint() {
        String hex = "fbc27d22c48b9b2533c4e97f7863f3dca805b8924ea2b7c6783f3fd99fdb2c29";
        Outpoint outpoint = new Outpoint(BCTest.mustDecodeHash(hex), 0xffffffff);
        Assert.assertEquals(outpoint.toString(), "fbc27d22c48b9b2533c4e97f7863f3dca805b8924ea2b7c6783f3fd99fdb2c29:4294967295");
    }

    class TestTransactionCase {
        Transaction tx;
        String hex;
        Hash hash;

        TestTransactionCase(Transaction tx, String hex, Hash hash) {
            this.tx = tx;
            this.hex = hex;
            this.hash = hash;
        }
    }

    class TestHasIssuanceCase {
        TxData data;
        boolean want;

        TestHasIssuanceCase(TxData data, boolean want) {
            this.data = data;
            this.want = want;
        }
    }
}
