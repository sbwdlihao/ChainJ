package com.lihao.protocol.bc.txinput;

import com.lihao.protocol.bc.*;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class SpendInput extends TxInput {

    public SpendInput(Hash txHash, int index, byte[][] arguments, AssetID assetID, long amount, byte[] controlProgram, byte[] referenceData) {
        this.assetVersion = 1;
        this.referenceData = referenceData;
        SpendInputCommitment inputCommitment = new SpendInputCommitment(this);
        SpendWitness inputWitness = new SpendWitness();
        inputCommitment.outpoint = new Outpoint(txHash, index);
        inputCommitment.outputCommitment = new OutputCommitment(new AssetAmount(assetID, amount), 1, controlProgram);
        inputWitness.arguments = arguments;
        this.inputCommitment = inputCommitment;
        this.inputWitness = inputWitness;
    }

    public SpendInput(){
        inputCommitment = new SpendInputCommitment(this);
        inputWitness = new SpendWitness();
    }
}
