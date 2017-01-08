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
                        BCTest.mustDecodeHash("74e60d94a75848b48fc79eac11a1d39f41e1b32046cf948929b729a57b75d5be"),
                        BCTest.mustDecodeHash("536cef3158d7ea51194b370e02f27265e8584ff4df1cd2829de0074c11f1f1b2")
                ),
                new TestTransactionCase(
                        new Transaction(
                                new TxData(1, new TxInput[]{
                                        new IssuanceInput(new byte[]{10,9,8}, assetID, 1000000000000L, "input".getBytes(), initialBlockHash, issuanceScript, new byte[][]{new byte[]{1,2,3}})
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
                        "28" + // input 0, issuance input witness length prefix
                        initialBlockHashHex + // input 0, issuance input witness, initial block
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
                        BCTest.mustDecodeHash("d5d90a4b6b179ec4c49badcec24f3c8890b3c03ed7f397a2de89b3f873de74a7"),
                        BCTest.mustDecodeHash("34a7b5eb0a40dbab132b4a4c0ca90044efc9d086d84503e1fb9175d12230ed1f")
                ),
                new TestTransactionCase(
                        new Transaction(
                                new TxData(1, new TxInput[]{
                                        new SpendInput(BCTest.mustDecodeHash("dd385f6fe25d91d8c1bd0fa58951ad56b0c5229dcc01f61d9f9e8b9eb92d3292"), 0, new byte[0][], new AssetID(), 1000000000000L, new byte[]{1}, "input".getBytes())
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
                        "4c" + // input 0, input commitment length prefix
                        "01" + // input 0, input commitment, "spend" type
                        "dd385f6fe25d91d8c1bd0fa58951ad56b0c5229dcc01f61d9f9e8b9eb92d3292" + // input 0, spend input commitment, outpoint tx hash
                        "00" + // input 0, spend input commitment, outpoint index
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
                        "9ed3e85a8c2d3717b5c94bd2db2ab9cab56955b2c4fb4696f345ca97aaab82d6" + // output 0, output commitment, asset id
                        "80e0a596bb11" + // output 0, output commitment, amount
                        "01" + // output 0, output commitment, vm version
                        "0101" + // output 0, output commitment, control program
                        "00" + // output 0, reference data
                        "00" + // output 0, output witness
                        "01" + // output 1, asset version
                        "29" + // output 1, output commitment length
                        "9ed3e85a8c2d3717b5c94bd2db2ab9cab56955b2c4fb4696f345ca97aaab82d6" + // output 1, output commitment, asset id
                        "80c0ee8ed20b" + // output 1, output commitment, amount
                        "01" + // output 1, vm version
                        "0102" + // output 1, output commitment, control program
                        "00" + // output 1, reference data
                        "00" + // output 1, output witness
                        "0c646973747269627574696f6e" // reference data
                        ,
                        BCTest.mustDecodeHash("d2587bdb93c65cd89d2d648f2adba54f9997d8e2d649bd222288519cb7224f49"),
                        BCTest.mustDecodeHash("a87eac712f74deb95bda148cc37375a0d8e16003a992b8d70531f7089dca4333")
                ),
        };
        for (TestTransactionCase aCase : cases) {
            byte[] got = BCTest.serialize(aCase.tx);
            byte[] want = Hex.decode(aCase.hex);
            Assert.assertArrayEquals(want, got);
            Assert.assertEquals(aCase.hash, aCase.tx.getHash());
            Assert.assertEquals(aCase.witnessHash, aCase.tx.witnessHash());
        }
    }

    @Test
    public void testHasIssuance() {
        TestHasIssuanceCase[] cases = new TestHasIssuanceCase[] {
                new TestHasIssuanceCase(
                        new TxData(
                                new TxInput[]{
                                        new IssuanceInput(new byte[0], new AssetID(), 0, new byte[0], new Hash(), new byte[0], new byte[0][])}),
                        true),
                new TestHasIssuanceCase(
                        new TxData(
                                new TxInput[]{
                                        new SpendInput(new Hash(), 0, new byte[0][], new AssetID(), 0, new byte[0], new byte[0]),
                                        new IssuanceInput(new byte[0], new AssetID(), 0, new byte[0], new Hash(), new byte[0], new byte[0][])
                                }),
                        true),
                new TestHasIssuanceCase(
                        new TxData(
                                new TxInput[]{
                                        new SpendInput(new Hash(), 0, new byte[0][], new AssetID(), 0, new byte[0], new byte[0]),
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
                "28" + // input 0, issuance input witness length prefix
                "03deff1d4319d67baa10a6d26c1fea9c3e8d30e33474efee1a610a9bb49d758d" + // input 0, issuance input witness, initial block
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

    @Test
    public void testTxHashForSig() {
        AssetID assetID = AssetID.computeAssetID(new byte[]{1}, BCTest.mustDecodeHash("03deff1d4319d67baa10a6d26c1fea9c3e8d30e33474efee1a610a9bb49d758d"), 1);
        TxData txData = new TxData(1,
                new TxInput[]{
                        new SpendInput(BCTest.mustDecodeHash("d250fa36f2813ddb8aed0fc66790ee58121bcbe88909bf88be12083d45320151"), 0, new byte[][]{new byte[]{1}}, new AssetID(), 0, new byte[0], "input1".getBytes()),
                        new SpendInput(BCTest.mustDecodeHash("d250fa36f2813ddb8aed0fc66790ee58121bcbe88909bf88be12083d45320151"), 1, new byte[][]{new byte[]{2}}, new AssetID(), 0, new byte[0], new byte[0]),
                },
                new TxOutput[]{
                        new TxOutput(assetID, 1000000000000L, new byte[]{3}, new byte[0])
                },
                "transfer".getBytes()
        );
        Map<Integer, String> cases = new HashMap<Integer, String>(){{
            put(0, "698a33855c638fc17c49fa0a2e297a47df4d89498bf7294f8b187cf77e05aa5a");
            put(1, "d5ec94cb0ca0ab1f8ccaae3f0310aa254f18f8877b0225a65965aab544302e69");
        }};

        SigHasher sigHasher = new SigHasher(txData);

        cases.forEach((k,v)->{
            Hash hash = txData.hashForSig(k);
            Assert.assertEquals(v, hash.toString());
            Hash cachedHash = sigHasher.hash(k);
            Assert.assertEquals(v, cachedHash.toString());
        });
    }

    class TestTransactionCase {
        Transaction tx;
        String hex;
        Hash hash;
        Hash witnessHash;

        TestTransactionCase(Transaction tx, String hex, Hash hash, Hash witnessHash) {
            this.tx = tx;
            this.hex = hex;
            this.hash = hash;
            this.witnessHash = witnessHash;
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
