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

    private TxOutput[] outputs = new TxOutput[0];

    private long minTime;

    private long maxTime;

    private byte[] referenceData = new byte[0];

    TxInput[] getInputs() {
        return inputs;
    }

    private void setInputs(TxInput[] inputs) {
        Objects.requireNonNull(inputs, "inputs can not be null");
        this.inputs = inputs;
    }

    TxOutput[] getOutputs() {
        return outputs;
    }

    private void setOutputs(TxOutput[] outputs) {
        Objects.requireNonNull(outputs, "outputs can not be null");
        this.outputs = outputs;
    }

    long getMinTime() {
        return minTime;
    }

    long getMaxTime() {
        return maxTime;
    }

    byte[] getReferenceData() {
        return referenceData;
    }

    private void setReferenceData(byte[] referenceData) {
        Objects.requireNonNull(referenceData, "referenceData can not be null");
        this.referenceData = referenceData;
    }

    TxData() {
    }

    TxData(TxInput[] inputs) {
        setInputs(inputs);
    }

    public TxData(TxInput[] inputs, TxOutput[] outputs) {
        setInputs(inputs);
        setOutputs(outputs);
    }

    TxData(long version, TxInput[] inputs, TxOutput[] outputs, byte[] referenceData) {
        this.version = version;
        setInputs(inputs);
        setOutputs(outputs);
        setReferenceData(referenceData);
    }

    TxData(long version, TxInput[] inputs, TxOutput[] outputs, long minTime, long maxTime, byte[] referenceData) {
        this.version = version;
        setInputs(inputs);
        setOutputs(outputs);
        this.minTime = minTime;
        this.maxTime = maxTime;
        setReferenceData(referenceData);
    }

    TxData(long version, TxOutput[] outputs) {
        this.version = version;
        setOutputs(outputs);
    }

    TxData(long version) {
        this.version = version;
    }

    void unMarshalText(byte[] p) throws IOException {
        byte[] b = Hex.decode(p);
        readFrom(new ByteArrayInputStream(b));
    }

    void readFrom(InputStream r) throws IOException {
        readSerFlags(r);
        version = BlockChain.readVarInt63(r);
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
        return new Hash(Sha3.sum256(buf.toByteArray()));
    }

    // assumes w has sticky errors
    void writeTo(ByteArrayOutputStream w, int serFlags) {
        w.write(serFlags);
        BlockChain.writeVarInt63(w, version);

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
        minTime = BlockChain.readVarInt63(buf, n);
        maxTime = BlockChain.readVarInt63(buf, n);
        if (version == 1 && n[0] < commonFields.length) {
            throw new IOException("unrecognized extra data in common fields for transaction version 1");
        }
    }

    private void readInputsFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r);
        setInputs(new TxInput[n]);
        for (int i = 0; i < n; i++) {
            inputs[i] = TxInput.readFrom(r, version);
        }
    }

    private void readOutputsFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r);
        setOutputs(new TxOutput[n]);
        for (int i = 0; i < n; i++) {
            outputs[i] = TxOutput.readFrom(r, version);
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
        BlockChain.writeVarInt31(w, outputs.length);
        for (TxOutput outPut : outputs) {
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
        BlockChain.writeVarInt31(w, outputs.length);
        for (TxOutput outPut : outputs) {
            w.write(outPut.witnessHash().getValue(), 0, outPut.witnessHash().getValue().length);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TxData txData = (TxData) o;

        return version == txData.version &&
                minTime == txData.minTime &&
                maxTime == txData.maxTime &&
                Arrays.equals(inputs, txData.inputs) &&
                Arrays.equals(outputs, txData.outputs) &&
                Arrays.equals(referenceData, txData.referenceData);
    }

    @Override
    public int hashCode() {
        int result = (int) (version ^ (version >>> 32));
        result = 31 * result + Arrays.hashCode(inputs);
        result = 31 * result + Arrays.hashCode(outputs);
        result = 31 * result + (int) (minTime ^ (minTime >>> 32));
        result = 31 * result + (int) (maxTime ^ (maxTime >>> 32));
        result = 31 * result + Arrays.hashCode(referenceData);
        return result;
    }
}
