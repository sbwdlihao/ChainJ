package chainj.protocol.bc;

import chainj.encoding.blockchain.BlockChain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class TxOutput {

    private long assetVersion;

    private OutputCommitment outputCommitment = new OutputCommitment();

    // Unconsumed suffixes of the commitment and witness extensible strings.
    private byte[] commitmentSuffix = new byte[0];

    private byte[] referenceData = new byte[0];

    public long getAssetVersion() {
        return assetVersion;
    }

    private void setOutputCommitment(OutputCommitment outputCommitment) {
        Objects.requireNonNull(outputCommitment);
        this.outputCommitment = outputCommitment;
    }

    public byte[] getReferenceData() {
        return referenceData;
    }

    private void setReferenceData(byte[] referenceData) {
        Objects.requireNonNull(referenceData);
        this.referenceData = referenceData;
    }

    public AssetAmount getAssetAmount() {
        return outputCommitment.getAssetAmount();
    }

    public AssetID getAssertID() {
        return outputCommitment.getAssetID();
    }

    public long getAmount() {
        return outputCommitment.getAmount();
    }

    public long getVmVersion() {
        return outputCommitment.getVmVersion();
    }

    public byte[] getControlProgram() {
        return outputCommitment.getControlProgram();
    }

    private TxOutput(){}

    public TxOutput(long assetVersion, OutputCommitment outputCommitment){
        this.assetVersion = assetVersion;
        setOutputCommitment(outputCommitment);
    }

    public TxOutput(AssetID assetID, long amount, byte[] controlProgram, byte[] referenceData) {
        this.assetVersion = 1;
        setOutputCommitment(new OutputCommitment(new AssetAmount(assetID, amount), 1, controlProgram));
        setReferenceData(referenceData);
    }

    public TxOutput(long assetVersion, long amount, long vmVersion, byte[] controlProgram) {
        this.assetVersion = assetVersion;
        setOutputCommitment(new OutputCommitment(new AssetAmount(new AssetID(), amount), vmVersion, controlProgram));
    }

    Hash witnessHash() {
        return Hash.emptyStringHash;
    }

    public static TxOutput readFrom(InputStream r) throws IOException {
        TxOutput txOutput = new TxOutput();
        txOutput.assetVersion = BlockChain.readVarInt63(r);
        txOutput.commitmentSuffix = txOutput.outputCommitment.readFrom(r, txOutput.assetVersion);
        txOutput.setReferenceData(BlockChain.readVarStr31(r));
        // readFull and ignore the (empty) output witness
        BlockChain.readVarStr31(r);
        return txOutput;
    }

    void writeTo(ByteArrayOutputStream w, int serFlags) {
        BlockChain.writeVarInt63(w, assetVersion);
        writeCommitment(w);
        Transaction.writeRefData(w, referenceData, serFlags);
        // write witness (empty in v1)
        BlockChain.writeVarStr31(w, null);
    }

    private void writeCommitment(ByteArrayOutputStream w) {
        outputCommitment.writeExtensibleString(w, commitmentSuffix, assetVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TxOutput txOutput = (TxOutput) o;

        if (assetVersion != txOutput.assetVersion) return false;
        if (outputCommitment != null ? !outputCommitment.equals(txOutput.outputCommitment) : txOutput.outputCommitment != null)
            return false;
        if (!Arrays.equals(commitmentSuffix, txOutput.commitmentSuffix)) return false;
        return Arrays.equals(referenceData, txOutput.referenceData);
    }

    @Override
    public int hashCode() {
        int result = (int) (assetVersion ^ (assetVersion >>> 32));
        result = 31 * result + (outputCommitment != null ? outputCommitment.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(commitmentSuffix);
        result = 31 * result + Arrays.hashCode(referenceData);
        return result;
    }
}
