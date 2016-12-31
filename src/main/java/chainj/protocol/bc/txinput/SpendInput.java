package chainj.protocol.bc.txinput;

import chainj.protocol.bc.*;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class SpendInput extends TxInput {

    public SpendInput(Hash txHash, int index, byte[][] arguments, AssetID assetID, long amount, byte[] controlProgram, byte[] referenceData) {
        setAssetVersion(1);
        setReferenceData(referenceData);
        SpendInputCommitment inputCommitment = new SpendInputCommitment(this);
        SpendWitness inputWitness = new SpendWitness();
        inputCommitment.setOutpoint(new Outpoint(txHash, index));
        inputCommitment.setOutputCommitment(new OutputCommitment(new AssetAmount(assetID, amount), 1, controlProgram));
        inputWitness.setArguments(arguments);
        setInputCommitment(inputCommitment);
        setInputWitness(inputWitness);
    }

    public SpendInput(){
        setInputCommitment(new SpendInputCommitment(this));
        setInputWitness(new SpendWitness());
    }

    @Override
    protected AssetAmount assetAmount() {
        return ((SpendInputCommitment)this.inputCommitment).getOutputCommitment().getAssetAmount();
    }

    @Override
    protected byte[] controlProgram() {
        return ((SpendInputCommitment)this.inputCommitment).getOutputCommitment().getControlProgram();
    }

    @Override
    public Outpoint outpoint() {
        return ((SpendInputCommitment)this.inputCommitment).getOutpoint();
    }
}
