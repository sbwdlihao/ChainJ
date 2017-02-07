package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;

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

    public byte[] readFrom(InputStream r, long assetVersion) throws IOException {
        return readFrom(r, assetVersion, null);
    }

    public byte[] readFrom(InputStream r, long assetVersion, int[] nOut) throws IOException {
        return BlockChain.readExtensibleString(r, buf -> {
            if (assetVersion == 1) {
                assetAmount.readFrom(buf);
                vmVersion = BlockChain.readVarInt63(buf);
                if (vmVersion != 1) {
                    throw new IOException(String.format("unrecognized VM version %d for asset version 1", vmVersion));
                }
                setControlProgram(BlockChain.readVarStr31(buf));
            }
        }, nOut);
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

    public void writeExtensibleString(ByteArrayOutputStream w, byte[] suffix, long assetVersion) {
        BlockChain.writeExtensibleString(w, suffix, buf -> {
            writeContents(buf, suffix, assetVersion);
            return null;
        });
    }

    private void writeContents(ByteArrayOutputStream buf, byte[] suffix, long assetVersion) {
        if (assetVersion == 1) {
            assetAmount.writeTo(buf);
            BlockChain.writeVarInt63(buf, vmVersion);
            BlockChain.writeVarStr31(buf, controlProgram);
        }
        if (suffix != null && suffix.length > 0) {
            buf.write(suffix, 0, suffix.length);
        }
    }

    public Hash hash(byte[] suffix, long assetVersion) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writeExtensibleString(buf, suffix, assetVersion);
        return new Hash(Sha3.sum256(buf.toByteArray()));
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
