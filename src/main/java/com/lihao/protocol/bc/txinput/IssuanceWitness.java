package com.lihao.protocol.bc.txinput;

import com.lihao.encoding.blockchain.BlockChain;
import com.lihao.protocol.bc.AssetID;
import com.lihao.protocol.bc.Hash;
import com.lihao.protocol.bc.InputWitness;
import com.lihao.protocol.bc.exception.BadAssetIDException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class IssuanceWitness extends SpendWitness implements InputWitness {

    public Hash initialBlockHash;

    public long vmVersion;

    public byte[] issuanceProgram;

    private IssuanceInputCommitment inputCommitment;

    public IssuanceWitness(IssuanceInputCommitment inputCommitment) {
        initialBlockHash = new Hash();
        this.inputCommitment = inputCommitment;
    }

    @Override
    public void readFrom(InputStream r) throws IOException {
        // readFull IssuanceInput witness
        initialBlockHash.readFull(r);
        vmVersion = BlockChain.readVarInt63(r, null);
        issuanceProgram = BlockChain.readVarStr31(r, null);
        AssetID computedAssetID = AssetID.computeAssetID(issuanceProgram, initialBlockHash, vmVersion);
        if (computedAssetID != inputCommitment.assetID) {
            throw new BadAssetIDException("asset ID does not match other issuance parameters");
        }
        super.readFrom(r);
    }

    @Override
    public void writeTo(OutputStream w) throws IOException {
        w.write(initialBlockHash.getValue());
        BlockChain.writeVarInt63(w, vmVersion); // TODO(bobg): check and return error
        BlockChain.writeVarStr31(w, issuanceProgram); // TODO(bobg): check and return error
        super.writeTo(w);
    }
}