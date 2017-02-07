package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import chainj.protocol.bc.txinput.IssuanceInput;
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

    // The unconsumed suffix of the common fields extensible string
    private byte[] commonFieldsSuffix = new byte[0];

    // The unconsumed suffix of the common witness extensible string
    private byte[] commonWitnessSuffix = new byte[0];

    private byte[] referenceData = new byte[0];

    public long getVersion() {
        return version;
    }

    public TxInput[] getInputs() {
        return inputs;
    }

    private void setInputs(TxInput[] inputs) {
        Objects.requireNonNull(inputs, "inputs can not be null");
        this.inputs = inputs;
    }

    public TxOutput[] getOutputs() {
        return outputs;
    }

    private void setOutputs(TxOutput[] outputs) {
        Objects.requireNonNull(outputs, "outputs can not be null");
        this.outputs = outputs;
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    private void setCommonFieldsSuffix(byte[] commonFieldsSuffix) {
        Objects.requireNonNull(commonFieldsSuffix);
        this.commonFieldsSuffix = commonFieldsSuffix;
    }

    private void setCommonWitnessSuffix(byte[] commonWitnessSuffix) {
        Objects.requireNonNull(commonWitnessSuffix);
        this.commonWitnessSuffix = commonWitnessSuffix;
    }

    public byte[] getReferenceData() {
        return referenceData;
    }

    private void setReferenceData(byte[] referenceData) {
        Objects.requireNonNull(referenceData);
        this.referenceData = referenceData;
    }

    public TxData() {
    }

    public TxData(long version) {
        this.version = version;
    }

    TxData(long version, TxOutput[] outputs) {
        this.version = version;
        setOutputs(outputs);
    }

    public TxData(long version, TxInput[] inputs) {
        this.version = version;
        setInputs(inputs);
    }

    public TxData(long version, TxInput[] inputs, TxOutput[] outputs) {
        this.version = version;
        setInputs(inputs);
        setOutputs(outputs);
    }

    public TxData(TxInput[] inputs) {
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

    public TxData(long version, TxInput[] inputs, TxOutput[] outputs, long minTime, long maxTime) {
        this.version = version;
        setInputs(inputs);
        setOutputs(outputs);
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public TxData(long version, TxInput[] inputs, TxOutput[] outputs, long minTime, long maxTime, byte[] referenceData) {
        this.version = version;
        setInputs(inputs);
        setOutputs(outputs);
        this.minTime = minTime;
        this.maxTime = maxTime;
        setReferenceData(referenceData);
    }

    void unMarshalText(byte[] p) throws IOException {
        byte[] b = Hex.decode(p);
        readFrom(new ByteArrayInputStream(b));
    }

    void readFrom(InputStream r) throws IOException {
        readSerFlags(r);
        version = BlockChain.readVarInt63(r);
        setCommonFieldsSuffix(BlockChain.readExtensibleString(r, this::readCommonFields));
        setCommonWitnessSuffix(BlockChain.readExtensibleString(r, buf -> {}));
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
        BlockChain.writeExtensibleString(w, commonFieldsSuffix, buf -> {
            BlockChain.writeVarInt63(buf, minTime);
            BlockChain.writeVarInt63(buf, maxTime);
            return null;
        });

        // common witness
        BlockChain.writeExtensibleString(w, commonWitnessSuffix, buf -> null);

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

    Hash issuanceHash(int idx) {
        if (idx < 0 || idx >= inputs.length) {
            throw new IllegalArgumentException(String.format("no input %d", idx));
        }
        TxInput txInput = inputs[idx];
        if (!(txInput instanceof IssuanceInput)) {
            throw new IllegalArgumentException(String.format("input %d not an issuance input", idx));
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        BlockChain.writeVarStr31(buf, ((IssuanceInput) txInput).getNonce());
        txInput.assertID().write(buf);
        BlockChain.writeVarInt63(buf, minTime);
        BlockChain.writeVarInt63(buf, maxTime);
        return new Hash(Sha3.sum256(buf.toByteArray()));
    }

    private void readSerFlags(InputStream r) throws IOException {
        int serFlags = BCUtil.readSerFlags(r);
        if (serFlags != Transaction.SerRequired) {
            throw new IOException(String.format("unsupported serFlags %#x", serFlags));
        }
    }

    private void readCommonFields(InputStream r) throws IOException {
        minTime = BlockChain.readVarInt63(r);
        maxTime = BlockChain.readVarInt63(r);
    }

    private void readInputsFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r);
        setInputs(new TxInput[n]);
        for (int i = 0; i < n; i++) {
            inputs[i] = TxInput.readFrom(r);
        }
    }

    private void readOutputsFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r);
        setOutputs(new TxOutput[n]);
        for (int i = 0; i < n; i++) {
            outputs[i] = TxOutput.readFrom(r);
        }
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
