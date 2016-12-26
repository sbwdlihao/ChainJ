package com.lihao.protocol.bc;

import com.lihao.encoding.blockchain.BlockChain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class TxOutput {

    public long assetVersion;

    public OutputCommitment outputCommitment = new OutputCommitment();

    public byte[] referenceData = new byte[0];

    public TxOutput(){}

    public TxOutput(AssetID assetID, long amount, byte[] controlProgram, byte[] referenceData) {
        this.assetVersion = 1;
        this.outputCommitment = new OutputCommitment(new AssetAmount(assetID, amount), 1, controlProgram);
        this.referenceData = referenceData;
    }

    Hash witnessHash() {
        return Hash.emptyHash;
    }

    public static TxOutput readFrom(InputStream r, long txVersion) throws IOException {
        TxOutput txOutput = new TxOutput();
        txOutput.assetVersion = BlockChain.readVarInt63(r);
        txOutput.outputCommitment.readFrom(r, txVersion, txOutput.assetVersion);
        txOutput.referenceData = BlockChain.readVarStr31(r);
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

    void writeCommitment(OutputStream w) throws IOException {
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
