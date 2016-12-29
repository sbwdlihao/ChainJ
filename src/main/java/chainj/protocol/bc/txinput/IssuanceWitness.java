package chainj.protocol.bc.txinput;

import chainj.protocol.bc.AssetID;
import chainj.protocol.bc.Hash;
import chainj.protocol.bc.InputWitness;
import chainj.protocol.bc.exception.BadAssetIDException;
import chainj.encoding.blockchain.BlockChain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class IssuanceWitness extends SpendWitness implements InputWitness {

    private Hash initialBlockHash = new Hash();

    private long vmVersion;

    private byte[] issuanceProgram = new byte[0];

    private IssuanceInputCommitment inputCommitment;

    public Hash getInitialBlockHash() {
        return initialBlockHash;
    }

    public void setInitialBlockHash(Hash initialBlockHash) {
        Objects.requireNonNull(initialBlockHash);
        this.initialBlockHash = initialBlockHash;
    }

    public long getVmVersion() {
        return vmVersion;
    }

    public void setVmVersion(long vmVersion) {
        this.vmVersion = vmVersion;
    }

    public byte[] getIssuanceProgram() {
        return issuanceProgram;
    }

    public void setIssuanceProgram(byte[] issuanceProgram) {
        Objects.requireNonNull(issuanceProgram);
        this.issuanceProgram = issuanceProgram;
    }

    public IssuanceWitness(IssuanceInputCommitment inputCommitment) {
        this.inputCommitment = inputCommitment;
    }

    @Override
    public void readFrom(InputStream r) throws IOException {
        // readFull IssuanceInput witness
        initialBlockHash.readFull(r);
        setVmVersion(BlockChain.readVarInt63(r));
        setIssuanceProgram(BlockChain.readVarStr31(r));
        AssetID computedAssetID = AssetID.computeAssetID(issuanceProgram, initialBlockHash, vmVersion);
        if (!computedAssetID.equals(inputCommitment.getAssetID())) {
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