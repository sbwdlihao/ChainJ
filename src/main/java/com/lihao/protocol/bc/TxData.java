package com.lihao.protocol.bc;

import com.lihao.crypto.Sha3;
import com.lihao.encoding.blockchain.BlockChain;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 23/12/2016.
 *
 * TxData encodes a transaction in the blockchain.
 * Most users will want to use Tx instead;
 * it includes the hash.
 */
public class TxData {

    public long version;

    public TxInput[] inputs = new TxInput[0];

    public TxOutput[] outPuts = new TxOutput[0];

    public long minTime;

    public long maxTime;

    public byte[] referenceData = new byte[0];

    public TxData() {
    }

    public TxData(TxInput[] inputs) {
        this.inputs = inputs;
    }

    public TxData(long version, TxInput[] inputs, TxOutput[] outPuts, byte[] referenceData) {
        this.version = version;
        this.inputs = inputs;
        this.outPuts = outPuts;
        this.referenceData = referenceData;
    }

    public TxData(long version, TxInput[] inputs, TxOutput[] outPuts, long minTime, long maxTime, byte[] referenceData) {
        this.version = version;
        this.inputs = inputs;
        this.outPuts = outPuts;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.referenceData = referenceData;
    }

    public TxData(long version, TxOutput[] outPuts) {
        this.version = version;
        this.outPuts = outPuts;
    }

    public TxData(long version) {
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
        referenceData = BlockChain.readVarStr31(r);
    }

    Hash hash() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writeTo(buf, (byte) 0);
        return new Hash(Sha3.Sum256(buf.toByteArray()));
    }

    // assumes w has sticky errors
    void writeTo(OutputStream w, int serFlags) throws IOException {
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
        if (inputs != null) {
            for (TxInput input : inputs) {
                if (input.isIssuance()) {
                    return true;
                }
            }
        }
        return false;
    }

    Hash hashForSig(int idx) throws IOException {
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
        inputs = new TxInput[n];
        for (int i = 0; i < n; i++) {
            inputs[i] = TxInput.readFrom(r, version);
        }
    }

    private void readOutputsFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r);
        outPuts = new TxOutput[n];
        for (int i = 0; i < n; i++) {
            outPuts[i] = TxOutput.readFrom(r, version);
        }
    }

    private void writeCommonFields(OutputStream w) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        BlockChain.writeVarInt63(buf, minTime);
        BlockChain.writeVarInt63(buf, maxTime);
        BlockChain.writeVarStr31(w, buf.toByteArray());
    }

    private void writeInputsTo(OutputStream w, int serFlags) throws IOException {
        if (inputs != null) {
            BlockChain.writeVarInt31(w, inputs.length);
            for (TxInput input : inputs) {
                input.writeTo(w, serFlags);
            }
        } else {
            BlockChain.writeVarInt31(w, 0);
        }
    }

    private void writeOutputsTo(OutputStream w, int serFlags) throws IOException {
        if (outPuts != null) {
            BlockChain.writeVarInt31(w, outPuts.length);
            for (TxOutput outPut : outPuts) {
                outPut.writeTo(w, serFlags);
            }
        } else {
            BlockChain.writeVarInt31(w, 0);
        }
    }

    void writeInputsWitnessTo(OutputStream w) throws IOException {
        if (inputs != null) {
            BlockChain.writeVarInt31(w, inputs.length);
            for (TxInput input : inputs) {
                w.write(input.witnessHash().getValue());
            }
        } else {
            BlockChain.writeVarInt31(w, 0);
        }
    }

    void writeOutputsWitnessTo(OutputStream w) throws IOException {
        if (outPuts != null) {
            BlockChain.writeVarInt31(w, outPuts.length);
            for (TxOutput outPut : outPuts) {
                w.write(outPut.witnessHash().getValue());
            }
        } else {
            BlockChain.writeVarInt31(w, 0);
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
