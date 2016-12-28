package com.lihao.protocol.bc;

import com.lihao.crypto.Sha3;
import com.lihao.encoding.blockchain.BlockChain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by sbwdlihao on 21/12/2016.
 *
 * AssetID is the Hash256 of the issuance script for the asset and the
 * initial block of the chain where it appears.
 */
public class AssetID extends AbstractHash {

    private static final long assetVersion = 1;

    public AssetID(){
        value = new byte[32];
    }

    public AssetID(byte[] value) {
        if (value == null || value.length != 32) {
            throw new IllegalArgumentException("assert id must be 32 byte array");
        }
        this.value = value;
    }

    // ComputeAssetID computes the asset ID of the asset defined by
    // the given issuance program and initial block hash.
    public static AssetID computeAssetID(byte[] issuanceProgram, Hash initialBlockHash, long vmVersion) throws IOException {
        Objects.requireNonNull(issuanceProgram);
        Objects.requireNonNull(initialBlockHash);

        ByteArrayOutputStream io = new ByteArrayOutputStream();
        io.write(initialBlockHash.getValue());
        BlockChain.writeVarInt63(io, assetVersion);
        BlockChain.writeVarInt63(io, vmVersion);
        BlockChain.writeVarStr31(io, issuanceProgram);
        return new AssetID(Sha3.Sum256(io.toByteArray()));
    }
}
