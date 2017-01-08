package chainj.protocol.bc;

import chainj.crypto.Sha3;
import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by sbwdlihao on 23/12/2016.
 */
public class AssetIDTest {

    @Test
    public void testComputeAssetID(){
        byte[] issuanceScript = new byte[]{1};
        Hash initialBlockHash = BCTest.mustDecodeHash("dd506f5d4c3f904d3d4b3c3be597c9198c6193ffd14a28570e4a923ce40cf9e5");
        AssetID assetID = AssetID.computeAssetID(issuanceScript, initialBlockHash, 1);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(initialBlockHash.getValue(), 0, initialBlockHash.getValue().length);
        buf.write(0x01); // assetVersion
        buf.write(0x01); // vmVersion
        buf.write(0x01); // length of issuanceScript
        buf.write(issuanceScript, 0, issuanceScript.length);
        byte[] want = Sha3.Sum256(buf.toByteArray());
        Assert.assertArrayEquals(want, assetID.getValue());
    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Measurement(iterations = 1)
    @Warmup(iterations = 1)
    public void measureComputeAssetID() {
        byte[] issuanceScript = new byte[]{5};
        Hash initialBlockHash = new Hash();
        AssetID.computeAssetID(issuanceScript, initialBlockHash, 1);
    }
}
