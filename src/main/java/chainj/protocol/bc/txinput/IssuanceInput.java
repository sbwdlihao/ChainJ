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
        return ((IssuanceInputCommitment)inputWitness).getNonce();
    }

    public IssuanceInput(byte[] nonce, AssetID assetID, long amount, byte[] referenceData, Hash initialBlockHash, byte[] issuanceProgram, byte[][] arguments) {
        assetVersion = 1;
        setReferenceData(referenceData);
        IssuanceInputCommitment inputCommitment = new IssuanceInputCommitment();
        IssuanceWitness inputWitness = new IssuanceWitness(inputCommitment);
        inputCommitment.setAmount(amount);
        inputCommitment.setNonce(nonce);
        inputCommitment.setAssetID(assetID);
        inputWitness.setVmVersion(1);
        inputWitness.setInitialBlockHash(initialBlockHash);
        inputWitness.setIssuanceProgram(issuanceProgram);
        inputWitness.setArguments(arguments);
        setInputCommitment(inputCommitment);
        setInputWitness(inputWitness);
    }

    public IssuanceInput() {
        IssuanceInputCommitment inputCommitment = new IssuanceInputCommitment();
        setInputCommitment(inputCommitment);
        setInputWitness(new IssuanceWitness(inputCommitment));
    }

    @Override
    protected AssetAmount assetAmount() {
        IssuanceInputCommitment inputCommitment = (IssuanceInputCommitment)this.inputCommitment;
        return new AssetAmount(inputCommitment.getAssetID(), inputCommitment.getAmount());
    }

    @Override
    public boolean isIssuance() {
        return true;
    }
}
