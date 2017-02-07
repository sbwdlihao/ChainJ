package chainj.protocol.bc.txinput;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import chainj.protocol.bc.AssetID;
import chainj.protocol.bc.Hash;
import chainj.protocol.bc.InputWitness;
import chainj.protocol.bc.TxInput;
import chainj.protocol.bc.exception.BadAssetIDException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class IssuanceWitness extends SpendWitness implements InputWitness {

    private Hash initialBlockHash = new Hash();

    private byte[] assetDefinition = new byte[0];

    private long vmVersion;

    private byte[] issuanceProgram = new byte[0];

    private IssuanceInput txInput;

    Hash getInitialBlockHash() {
        return initialBlockHash;
    }

    void setInitialBlockHash(Hash initialBlockHash) {
        Objects.requireNonNull(initialBlockHash);
        this.initialBlockHash = initialBlockHash;
    }

    void setAssetDefinition(byte[] assetDefinition) {
        Objects.requireNonNull(assetDefinition);
        this.assetDefinition = assetDefinition;
    }

    long getVmVersion() {
        return vmVersion;
    }

    void setVmVersion(long vmVersion) {
        this.vmVersion = vmVersion;
    }

    byte[] getIssuanceProgram() {
        return issuanceProgram;
    }

    void setIssuanceProgram(byte[] issuanceProgram) {
        Objects.requireNonNull(issuanceProgram);
        this.issuanceProgram = issuanceProgram;
    }

    IssuanceWitness(IssuanceInput txInput) {
        this.txInput = txInput;
    }

    Hash assetDefinitionHash() {
        return new Hash(Sha3.sum256(assetDefinition));
    }

    @Override
    public void readFrom(InputStream r) throws IOException {
        // readFull IssuanceInput witness
        initialBlockHash.readFull(r);
        assetDefinition = BlockChain.readVarStr31(r);
        vmVersion = BlockChain.readVarInt63(r);
        setIssuanceProgram(BlockChain.readVarStr31(r));
        AssetID computedAssetID = AssetID.computeAssetID(issuanceProgram, initialBlockHash, vmVersion, assetDefinitionHash());
        if (!computedAssetID.equals(txInput.assertID())) {
            throw new BadAssetIDException("asset ID does not match other issuance parameters");
        }
        super.readFrom(r);
    }

    @Override
    public void writeTo(ByteArrayOutputStream w) {
        initialBlockHash.write(w);
        BlockChain.writeVarStr31(w, assetDefinition);
        BlockChain.writeVarInt63(w, vmVersion);
        BlockChain.writeVarStr31(w, issuanceProgram);
        super.writeTo(w);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        IssuanceWitness that = (IssuanceWitness) o;

        if (vmVersion != that.vmVersion) return false;
        if (initialBlockHash != null ? !initialBlockHash.equals(that.initialBlockHash) : that.initialBlockHash != null)
            return false;
        if (!Arrays.equals(assetDefinition, that.assetDefinition)) return false;
        return Arrays.equals(issuanceProgram, that.issuanceProgram);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (initialBlockHash != null ? initialBlockHash.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(assetDefinition);
        result = 31 * result + (int) (vmVersion ^ (vmVersion >>> 32));
        result = 31 * result + Arrays.hashCode(issuanceProgram);
        return result;
    }
}