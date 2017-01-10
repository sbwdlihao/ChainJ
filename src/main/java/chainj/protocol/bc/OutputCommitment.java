package chainj.protocol.bc;

import chainj.encoding.blockchain.BlockChain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class OutputCommitment {

    private AssetAmount assetAmount = new AssetAmount();

    private long vmVersion;

    private byte[] controlProgram = new byte[0];

    public AssetAmount getAssetAmount() {
        return assetAmount;
    }

    private void setAssetAmount(AssetAmount assetAmount) {
        Objects.requireNonNull(assetAmount);
        this.assetAmount = assetAmount;
    }

    public long getVmVersion() {
        return vmVersion;
    }

    public byte[] getControlProgram() {
        return controlProgram;
    }

    private void setControlProgram(byte[] controlProgram) {
        Objects.requireNonNull(controlProgram);
        this.controlProgram = controlProgram;
    }

    AssetID getAssetID() {
        return assetAmount.getAssetID();
    }

    long getAmount() {
        return assetAmount.getAmount();
    }

    public OutputCommitment() {}

    public OutputCommitment(long vmVersion) {
        this.vmVersion = vmVersion;
    }

    public OutputCommitment(AssetAmount assetAmount, long vmVersion, byte[] controlProgram) {
        setAssetAmount(assetAmount);
        this.vmVersion = vmVersion;
        setControlProgram(controlProgram);
    }

    public void readFrom(InputStream r, long txVersion, long assetVersion) throws IOException {
        readFrom(r, txVersion, assetVersion, null);
    }

    public void readFrom(InputStream r, long txVersion, long assetVersion, int[] nOut) throws IOException {
        byte[] b = BlockChain.readVarStr31(r, nOut);
        if (assetVersion != 1) {
            return;
        }
        InputStream in = new ByteArrayInputStream(b);
        int[] n1 = new int[1];
        assetAmount.readFrom(in, n1);
        vmVersion = BlockChain.readVarInt63(in, n1);
        setControlProgram(BlockChain.readVarStr31(in, n1));
        if (txVersion == 1 && n1[0] < b.length) {
            throw new IOException("unrecognized extra data in output commitment for transaction version 1");
        }
    }

    public void writeTo(ByteArrayOutputStream w, long assetVersion) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        if (assetVersion == 1) {
            assetAmount.writeTo(buf);
            BlockChain.writeVarInt63(buf, vmVersion);
            BlockChain.writeVarStr31(buf, controlProgram);
        }
        BlockChain.writeVarStr31(w, buf.toByteArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutputCommitment that = (OutputCommitment) o;

        return vmVersion == that.vmVersion &&
                (assetAmount != null ? assetAmount.equals(that.assetAmount) : that.assetAmount == null) &&
                Arrays.equals(controlProgram, that.controlProgram);
    }

    @Override
    public int hashCode() {
        int result = assetAmount != null ? assetAmount.hashCode() : 0;
        result = 31 * result + (int) (vmVersion ^ (vmVersion >>> 32));
        result = 31 * result + Arrays.hashCode(controlProgram);
        return result;
    }
}
