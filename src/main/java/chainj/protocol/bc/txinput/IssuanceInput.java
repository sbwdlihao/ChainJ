package chainj.protocol.bc.txinput;

import chainj.protocol.bc.AssetAmount;
import chainj.protocol.bc.AssetID;
import chainj.protocol.bc.Hash;
import chainj.protocol.bc.TxInput;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class IssuanceInput extends TxInput {

    public byte[] getNonce() {
        return ((IssuanceInputCommitment)inputCommitment).getNonce();
    }

    public IssuanceInput(byte[] nonce, long amount, byte[] referenceData, Hash initialBlockHash, byte[] issuanceProgram, byte[][] arguments) {}

    public IssuanceInput(byte[] nonce, long amount, byte[] referenceData, Hash initialBlockHash, byte[] issuanceProgram, byte[][] arguments, byte[] assetDefinition) {
        assetVersion = 1;
        long vmVersion = 1;
        setReferenceData(referenceData);
        IssuanceInputCommitment inputCommitment = new IssuanceInputCommitment();
        IssuanceWitness inputWitness = new IssuanceWitness(this);
        inputWitness.setVmVersion(vmVersion);
        inputWitness.setInitialBlockHash(initialBlockHash);
        inputWitness.setAssetDefinition(assetDefinition);
        inputWitness.setIssuanceProgram(issuanceProgram);
        inputWitness.setArguments(arguments);
        inputCommitment.setAmount(amount);
        inputCommitment.setNonce(nonce);
        inputCommitment.setAssetID(AssetID.computeAssetID(issuanceProgram, initialBlockHash, vmVersion, inputWitness.assetDefinitionHash()));
        setInputCommitment(inputCommitment);
        setInputWitness(inputWitness);
    }

    public IssuanceInput (long vmVersion) {
        IssuanceInputCommitment inputCommitment = new IssuanceInputCommitment();
        IssuanceWitness inputWitness = new IssuanceWitness(this);
        inputWitness.setVmVersion(vmVersion);
        setInputCommitment(inputCommitment);
        setInputWitness(inputWitness);
    }

    public IssuanceInput() {
        IssuanceInputCommitment inputCommitment = new IssuanceInputCommitment();
        setInputCommitment(inputCommitment);
        setInputWitness(new IssuanceWitness(this));
    }

    public Hash getInitialBlockHash() {
        return ((IssuanceWitness)inputWitness).getInitialBlockHash();
    }

    @Override
    public AssetAmount assetAmount() {
        IssuanceInputCommitment inputCommitment = (IssuanceInputCommitment)this.inputCommitment;
        return new AssetAmount(inputCommitment.getAssetID(), inputCommitment.getAmount());
    }

    @Override
    public long vmVersion() {
        return ((IssuanceWitness)inputWitness).getVmVersion();
    }

    @Override
    public byte[] vmProgram() {
        return ((IssuanceWitness)inputWitness).getIssuanceProgram();
    }

    @Override
    public boolean isIssuance() {
        return true;
    }
}
