package chainj.protocol.bc;

import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by sbwdlihao on 26/12/2016.
 */
public class BlockTest {

    @Test(expected = DecoderException.class)
    public void testMarshalBlock() throws IOException {
        Block b = new Block(new BlockHeader(1, 1), new Transaction[]{
                new Transaction(new TxData(1, new TxOutput[]{
                        new TxOutput(new AssetID(), 1, new byte[0], new byte[0])
                }))
        });

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        b.writeTo(buf);
        byte[] got = Hex.encode(buf.toByteArray());

        String wantHex = "03" + // serialization flags
                "01" + // version
                "01" + // block height
                "0000000000000000000000000000000000000000000000000000000000000000" + // prev block hash
                "00" + // timestamp
                "41" + // commitment extensible field length
                "0000000000000000000000000000000000000000000000000000000000000000" + // tx merkle root
                "0000000000000000000000000000000000000000000000000000000000000000" + // assets merkle root
                "00" + // consensus program
                "01" + // witness extensible string length
                "00" + // witness number of witness args
                "01" + // num transactions
                "07" + // tx 0, serialization flags
                "01" + // tx 0, tx version
                "02" + // tx 0, common fields extensible length string
                "00" + // tx 0, common fields mintime
                "00" + // tx 0, common fields maxtime
                "00" + // tx 0, common witness extensible string length
                "00" + // tx 0, inputs count
                "01" + // tx 0, outputs count
                "01" + // tx 0 output 0, asset version
                "23" + // tx 0, output 0, output commitment length
                "0000000000000000000000000000000000000000000000000000000000000000" + // tx 0, output 0 commitment, asset id
                "01" + // tx 0, output 0 commitment, amount
                "01" + // tx 0, output 0 commitment vm version
                "00" + // tx 0, output 0 control program
                "00" + // tx 0, output 0 reference data
                "00" + // tx 0, output 0 output witness
                "00"; // tx 0 reference data
        Assert.assertArrayEquals(wantHex.getBytes(), got);

        Block b1 = new Block();
        b1.readFrom(new ByteArrayInputStream(Hex.decode(got)));
        Assert.assertEquals(b, b1);

        got[7] = 'q';
        b1.readFrom(new ByteArrayInputStream(Hex.decode(got)));
    }

    @Test
    public void testEmptyBlock() throws IOException {
        Block b = new Block(new BlockHeader(Block.NewBlockVersion, 1));
        byte[] got = BCTest.serialize(b);
        String wantHex = "03" + // serialization flags
                "01" + // version
                "01" + // block height
                "0000000000000000000000000000000000000000000000000000000000000000" + // prev block hash
                "00" + // timestamp
                "41" + // commitment extensible field length
                "0000000000000000000000000000000000000000000000000000000000000000" + // transactions merkle root
                "0000000000000000000000000000000000000000000000000000000000000000" + // assets merkle root
                "00" + // consensus program
                "01" + // witness extensible string length
                "00" + // witness number of witness args
                "00"; // num transactions
        byte[] want = Hex.decode(wantHex);
        Assert.assertArrayEquals(want, got);

        got = BCTest.serialize(b.getBlockHeader());
        wantHex = "01" + // serialization flags
                "01" + // version
                "01" + // block height
                "0000000000000000000000000000000000000000000000000000000000000000" + // prev block hash
                "00" + // timestamp
                "41" + // commitment extensible field length
                "0000000000000000000000000000000000000000000000000000000000000000" + // transactions merkle root
                "0000000000000000000000000000000000000000000000000000000000000000" + // assets merkle root
                "00" + // consensus program
                "01" + // witness extensible string length
                "00"; // witness number of witness args
        want = Hex.decode(wantHex);
        Assert.assertArrayEquals(want, got);

        Assert.assertEquals(BCTest.mustDecodeHash("7508682af2b4770e327b26ad52809da99bd89d885b91d4fba44e93bd0ad1da2f"), b.getBlockHeader().hash());
        Assert.assertEquals(BCTest.mustDecodeHash("a48b8fc5a149250b68ee77606175c23d36d6933c178d5645b5b1d1e89e130207"), b.getBlockHeader().hashForSig());
        Assert.assertEquals(new Date(0), b.getBlockHeader().time());
    }

    @Test
    public void testSmallBlock() throws IOException {
        Block b = new Block(new BlockHeader(Block.NewBlockVersion, 1), new Transaction[]{
                new Transaction(new TxData(Transaction.CurrentTransactionVersion))
        });
        byte[] got = BCTest.serialize(b);
        String wantHex = "03" + // serialization flags
                "01" + // version
                "01" + // block height
                "0000000000000000000000000000000000000000000000000000000000000000" + // prev block hash
                "00" + // timestamp
                "41" + // commitment extensible field length
                "0000000000000000000000000000000000000000000000000000000000000000" + // transactions merkle root
                "0000000000000000000000000000000000000000000000000000000000000000" + // assets merkle root
                "00" + // consensus program
                "01" + // witness extensible string length
                "00" + // witness num witness args
                "01" + // num transactions
                "070102000000000000"; // transaction
        byte[] want = Hex.decode(wantHex);
        Assert.assertArrayEquals(want, got);
    }
}
