package com.lihao.protocol.bc;

import com.lihao.encoding.blockchain.BlockChain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 21/12/2016.
 *
 * TODO(bobg): Review serialization/deserialization logic for assetVersions other than 1.
 */
public class TxOutput {

    public long assetVersion;

    public OutputCommitment outputCommitment;

    public byte[] referenceData;

    public static TxOutput newTxOutput(AssetID assetID, long amount, byte[] controlProgram, byte[] referenceData) {
        return new TxOutput(1, new OutputCommitment(new AssetAmount(assetID, amount), 1, controlProgram), referenceData);
    }

    public TxOutput(){}

    public TxOutput(long assetVersion, OutputCommitment outputCommitment, byte[] referenceData) {
        this.assetVersion = assetVersion;
        this.outputCommitment = outputCommitment;
        this.referenceData = referenceData;
    }

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
        txOutput.assetVersion = BlockChain.readVarInt63(r, null);
        txOutput.outputCommitment.readFrom(r, txVersion, txOutput.assetVersion, null);
        txOutput.referenceData = BlockChain.readVarStr31(r, null);
        // readFull and ignore the (empty) output witness
        BlockChain.readVarStr31(r, null);
        return txOutput;
    }

    void writeTo(OutputStream w, byte serFlags) throws IOException {
        BlockChain.writeVarInt63(w, assetVersion);
        outputCommitment.writeTo(w, assetVersion);
        Transaction.writeRefData(w, referenceData, serFlags);
        BlockChain.writeVarStr31(w, null);
    }

    void writeCommitment(OutputStream w) throws IOException {
        outputCommitment.writeTo(w, assetVersion);
    }
}
