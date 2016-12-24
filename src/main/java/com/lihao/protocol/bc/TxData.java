package com.lihao.protocol.bc;

import com.lihao.crypto.Sha3;
import com.lihao.encoding.blockchain.BlockChain;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;

/**
 * Created by sbwdlihao on 23/12/2016.
 *
 * TxData encodes a transaction in the blockchain.
 * Most users will want to use Tx instead;
 * it includes the hash.
 */
public class TxData {

    public long version;

    public TxInput[] inputs;

    public TxOutput[] outPuts;

    public long minTime;

    public long maxTime;

    public byte[] referenceData;

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

    void unMarshalText(byte[] p) throws IOException {
        byte[] b = Hex.decode(p);
        readFrom(new ByteArrayInputStream(b));
    }

    private void readFrom(InputStream r) throws IOException {
        readSerFlags(r);
        version = BlockChain.readVarInt63(r, null);
        readCommonFields(r);
        // Common witness, empty in v1
        BlockChain.readVarStr31(r, null);
        readInputsFrom(r);
        readOutputsFrom(r);
        referenceData = BlockChain.readVarStr31(r, null);
    }

    Hash hash() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writeTo(buf, (byte) 0);
        return new Hash(Sha3.Sum256(buf.toByteArray()));
    }

    // assumes w has sticky errors
    void writeTo(OutputStream w, byte serFlags) throws IOException {
        w.write(new byte[]{serFlags});
        BlockChain.writeVarInt63(w, version); // TODO(bobg): check and return error

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
        int serFlags = r.read();
        if (serFlags == -1) {
            throw new IOException("readFull serFlags null");
        }
        if (serFlags != Transaction.SerRequired) {
            throw new IOException(String.format("unsupported serFlags %#x", serFlags));
        }
    }

    private void readCommonFields(InputStream r) throws IOException {
        byte[] commonFields = BlockChain.readVarStr31(r, null);
        ByteArrayInputStream buf = new ByteArrayInputStream(commonFields);
        int[] n = new int[1];
        minTime = BlockChain.readVarInt63(buf, n);
        maxTime = BlockChain.readVarInt63(buf, n);
        if (version == 1 && n[0] < commonFields.length) {
            throw new IOException("unrecognized extra data in common fields for transaction version 1");
        }
    }

    private void readInputsFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r, null);
        inputs = new TxInput[n];
        for (int i = 0; i < n; i++) {
            inputs[i] = TxInput.readFrom(r, version);
        }
    }

    private void readOutputsFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r, null);
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

    private void writeInputsTo(OutputStream w, byte serFlags) throws IOException {
        if (inputs != null) {
            BlockChain.writeVarInt31(w, inputs.length);
            for (TxInput input : inputs) {
                input.writeTo(w, serFlags);
            }
        } else {
            BlockChain.writeVarInt31(w, 0);
        }
    }

    private void writeOutputsTo(OutputStream w, byte serFlags) throws IOException {
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
}
