package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * Created by sbwdlihao on 21/12/2016.
 *
 * AssetID is the Hash256 of the issuance script for the asset and the
 * initial block of the chain where it appears.
 */
public class AssetID extends AbstractHash {

    public AssetID(){}

    public AssetID(byte... bytes) {
        super(bytes);
    }

    // ComputeAssetID computes the asset ID of the asset defined by
    // the given issuance program and initial block hash.
    public static AssetID computeAssetID(byte[] issuanceProgram, Hash initialBlockHash, long vmVersion, Hash assetDefinitionHash) {
        Objects.requireNonNull(issuanceProgram);
        Objects.requireNonNull(initialBlockHash);

        ByteArrayOutputStream io = new ByteArrayOutputStream();
        initialBlockHash.write(io);
        BlockChain.writeVarInt63(io, vmVersion);
        BlockChain.writeVarStr31(io, issuanceProgram);
        assetDefinitionHash.write(io);
        return new AssetID(Sha3.sum256(io.toByteArray()));
    }

    public static AssetID computeAssetID(byte[] issuanceProgram, Hash initialBlockHash, long vmVersion) {
        return computeAssetID(issuanceProgram, initialBlockHash, vmVersion, Hash.emptyStringHash);
    }
}
