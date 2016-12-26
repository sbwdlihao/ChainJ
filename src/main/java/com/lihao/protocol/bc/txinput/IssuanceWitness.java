package com.lihao.protocol.bc.txinput;

import com.lihao.encoding.blockchain.BlockChain;
import com.lihao.protocol.bc.AssetID;
import com.lihao.protocol.bc.Hash;
import com.lihao.protocol.bc.InputWitness;
import com.lihao.protocol.bc.exception.BadAssetIDException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class IssuanceWitness extends SpendWitness implements InputWitness {

    public Hash initialBlockHash = new Hash();

    public long vmVersion;

    public byte[] issuanceProgram = new byte[0];

    private IssuanceInputCommitment inputCommitment;

    public IssuanceWitness(IssuanceInputCommitment inputCommitment) {
        this.inputCommitment = inputCommitment;
    }

    @Override
    public void readFrom(InputStream r) throws IOException {
        // readFull IssuanceInput witness
        initialBlockHash.readFull(r);
        vmVersion = BlockChain.readVarInt63(r);
        issuanceProgram = BlockChain.readVarStr31(r);
        AssetID computedAssetID = AssetID.computeAssetID(issuanceProgram, initialBlockHash, vmVersion);
        if (computedAssetID != inputCommitment.assetID) {
            throw new BadAssetIDException("asset ID does not match other issuance parameters");
        }
        super.readFrom(r);
    }

    @Override
    public void writeTo(OutputStream w) throws IOException {
        w.write(initialBlockHash.getValue());
        BlockChain.writeVarInt63(w, vmVersion);
        BlockChain.writeVarStr31(w, issuanceProgram);
        super.writeTo(w);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IssuanceWitness)) return false;
        if (!super.equals(o)) return false;

        IssuanceWitness that = (IssuanceWitness) o;

        if (vmVersion != that.vmVersion) return false;
        if (initialBlockHash != null ? !initialBlockHash.equals(that.initialBlockHash) : that.initialBlockHash != null)
            return false;
        return Arrays.equals(issuanceProgram, that.issuanceProgram);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (initialBlockHash != null ? initialBlockHash.hashCode() : 0);
        result = 31 * result + (int) (vmVersion ^ (vmVersion >>> 32));
        result = 31 * result + Arrays.hashCode(issuanceProgram);
        return result;
    }
}