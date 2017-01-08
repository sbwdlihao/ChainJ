package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 23/12/2016.
 *
 * TxData encodes a transaction in the blockchain.
 * Most users will want to use Tx instead;
 * it includes the hash.
 */
public class TxData {

    private long version;

    private TxInput[] inputs = new TxInput[0];

    private TxOutput[] outPuts = new TxOutput[0];

    private long minTime;

    private long maxTime;

    private byte[] referenceData = new byte[0];

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public TxInput[] getInputs() {
        return inputs;
    }

    public void setInputs(TxInput[] inputs) {
        Objects.requireNonNull(inputs, "inputs can not be null");
        this.inputs = inputs;
    }

    public TxOutput[] getOutPuts() {
        return outPuts;
    }

    public void setOutPuts(TxOutput[] outPuts) {
        Objects.requireNonNull(outPuts, "outPuts can not be null");
        this.outPuts = outPuts;
    }

    public long getMinTime() {
        return minTime;
    }

    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public byte[] getReferenceData() {
        return referenceData;
    }

    public void setReferenceData(byte[] referenceData) {
        Objects.requireNonNull(referenceData, "referenceData can not be null");
        this.referenceData = referenceData;
    }

    public TxData() {
    }

    public TxData(TxInput[] inputs) {
        setInputs(inputs);
    }

    public TxData(long version, TxInput[] inputs, TxOutput[] outPuts, byte[] referenceData) {
        setVersion(version);
        setInputs(inputs);
        setOutPuts(outPuts);
        setReferenceData(referenceData);
    }

    public TxData(long version, TxInput[] inputs, TxOutput[] outPuts, long minTime, long maxTime, byte[] referenceData) {
        setVersion(version);
        setInputs(inputs);
        setOutPuts(outPuts);
        setMinTime(minTime);
        setMaxTime(maxTime);
        setReferenceData(referenceData);
    }

    public TxData(long version, TxOutput[] outPuts) {
        setVersion(version);
        setOutPuts(outPuts);
    }

    public TxData(long version) {
        setVersion(version);
    }

    void unMarshalText(byte[] p) throws IOException {
        byte[] b = Hex.decode(p);
        readFrom(new ByteArrayInputStream(b));
    }

    void readFrom(InputStream r) throws IOException {
        readSerFlags(r);
        setVersion(BlockChain.readVarInt63(r));
        readCommonFields(r);
        // Common witness, empty in v1
        BlockChain.readVarStr31(r);
        readInputsFrom(r);
        readOutputsFrom(r);
        setReferenceData(BlockChain.readVarStr31(r));
    }

    Hash hash() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writeTo(buf, (byte) 0);
        return new Hash(Sha3.Sum256(buf.toByteArray()));
    }

    // assumes w has sticky errors
    void writeTo(ByteArrayOutputStream w, int serFlags) {
        w.write(serFlags);
        BlockChain.writeVarInt63(w, getVersion());

        // common fields
        writeCommonFields(w);

        // common witness
        BlockChain.writeVarStr31(w, new byte[0]);

        writeInputsTo(w, serFlags);
        writeOutputsTo(w, serFlags);

        Transaction.writeRefData(w, referenceData, serFlags);
    }

    // HasIssuance returns true if this transaction has an issuance input.
    boolean hasIssuance() {
        for (TxInput input : inputs) {
            if (input.isIssuance()) {
                return true;
            }
        }
        return false;
    }

    Hash hashForSig(int idx) {
        return new SigHasher(this).hash(idx);
    }

    private void readSerFlags(InputStream r) throws IOException {
        int serFlags = BCUtil.readSerFlags(r);
        if (serFlags != Transaction.SerRequired) {
            throw new IOException(String.format("unsupported serFlags %#x", serFlags));
        }
    }

    private void readCommonFields(InputStream r) throws IOException {
        byte[] commonFields = BlockChain.readVarStr31(r);
        ByteArrayInputStream buf = new ByteArrayInputStream(commonFields);
        int[] n = new int[1];
        setMinTime(BlockChain.readVarInt63(buf, n));
        setMaxTime(BlockChain.readVarInt63(buf, n));
        if (getVersion() == 1 && n[0] < commonFields.length) {
            throw new IOException("unrecognized extra data in common fields for transaction version 1");
        }
    }

    private void readInputsFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r);
        setInputs(new TxInput[n]);
        for (int i = 0; i < n; i++) {
            inputs[i] = TxInput.readFrom(r, getVersion());
        }
    }

    private void readOutputsFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r);
        setOutPuts(new TxOutput[n]);
        for (int i = 0; i < n; i++) {
            outPuts[i] = TxOutput.readFrom(r, getVersion());
        }
    }

    private void writeCommonFields(ByteArrayOutputStream w) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        BlockChain.writeVarInt63(buf, minTime);
        BlockChain.writeVarInt63(buf, maxTime);
        BlockChain.writeVarStr31(w, buf.toByteArray());
    }

    private void writeInputsTo(ByteArrayOutputStream w, int serFlags) {
        BlockChain.writeVarInt31(w, inputs.length);
        for (TxInput input : inputs) {
            input.writeTo(w, serFlags);
        }
    }

    private void writeOutputsTo(ByteArrayOutputStream w, int serFlags) {
        BlockChain.writeVarInt31(w, outPuts.length);
        for (TxOutput outPut : outPuts) {
            outPut.writeTo(w, serFlags);
        }
    }

    void writeInputsWitnessTo(ByteArrayOutputStream w) {
        BlockChain.writeVarInt31(w, inputs.length);
        for (TxInput input : inputs) {
            w.write(input.witnessHash().getValue(), 0, input.witnessHash().getValue().length);
        }
    }

    void writeOutputsWitnessTo(ByteArrayOutputStream w) {
        BlockChain.writeVarInt31(w, outPuts.length);
        for (TxOutput outPut : outPuts) {
            w.write(outPut.witnessHash().getValue(), 0, outPut.witnessHash().getValue().length);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TxData txData = (TxData) o;

        if (version != txData.version) return false;
        if (minTime != txData.minTime) return false;
        if (maxTime != txData.maxTime) return false;
        if (!Arrays.equals(inputs, txData.inputs)) return false;
        if (!Arrays.equals(outPuts, txData.outPuts)) return false;
        return Arrays.equals(referenceData, txData.referenceData);
    }

    @Override
    public int hashCode() {
        int result = (int) (version ^ (version >>> 32));
        result = 31 * result + Arrays.hashCode(inputs);
        result = 31 * result + Arrays.hashCode(outPuts);
        result = 31 * result + (int) (minTime ^ (minTime >>> 32));
        result = 31 * result + (int) (maxTime ^ (maxTime >>> 32));
        result = 31 * result + Arrays.hashCode(referenceData);
        return result;
    }
}
