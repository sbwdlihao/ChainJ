package chainj.protocol.bc;

import chainj.encoding.blockchain.BlockChain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class TxOutput {

    private long assetVersion;

    private OutputCommitment outputCommitment = new OutputCommitment();

    private byte[] referenceData = new byte[0];

    public long getAssetVersion() {
        return assetVersion;
    }

    public void setAssetVersion(long assetVersion) {
        this.assetVersion = assetVersion;
    }

    public OutputCommitment getOutputCommitment() {
        return outputCommitment;
    }

    public void setOutputCommitment(OutputCommitment outputCommitment) {
        Objects.requireNonNull(outputCommitment);
        this.outputCommitment = outputCommitment;
    }

    public byte[] getReferenceData() {
        return referenceData;
    }

    public void setReferenceData(byte[] referenceData) {
        Objects.requireNonNull(referenceData);
        this.referenceData = referenceData;
    }

    public TxOutput(){}

    public TxOutput(AssetID assetID, long amount, byte[] controlProgram, byte[] referenceData) {
        setAssetVersion(1);
        setOutputCommitment(new OutputCommitment(new AssetAmount(assetID, amount), 1, controlProgram));
        setReferenceData(referenceData);
    }

    Hash witnessHash() {
        return Hash.emptyHash;
    }

    public static TxOutput readFrom(InputStream r, long txVersion) throws IOException {
        TxOutput txOutput = new TxOutput();
        txOutput.setAssetVersion(BlockChain.readVarInt63(r));
        txOutput.outputCommitment.readFrom(r, txVersion, txOutput.assetVersion);
        txOutput.setReferenceData(BlockChain.readVarStr31(r));
        // readFull and ignore the (empty) output witness
        BlockChain.readVarStr31(r);
        return txOutput;
    }

    void writeTo(OutputStream w, int serFlags) throws IOException {
        BlockChain.writeVarInt63(w, assetVersion);
        writeCommitment(w);
        Transaction.writeRefData(w, referenceData, serFlags);
        BlockChain.writeVarStr31(w, null);
    }

    private void writeCommitment(OutputStream w) throws IOException {
        outputCommitment.writeTo(w, assetVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TxOutput txOutput = (TxOutput) o;

        if (assetVersion != txOutput.assetVersion) return false;
        if (outputCommitment != null ? !outputCommitment.equals(txOutput.outputCommitment) : txOutput.outputCommitment != null)
            return false;
        return Arrays.equals(referenceData, txOutput.referenceData);
    }

    @Override
    public int hashCode() {
        int result = (int) (assetVersion ^ (assetVersion >>> 32));
        result = 31 * result + (outputCommitment != null ? outputCommitment.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(referenceData);
        return result;
    }
}
