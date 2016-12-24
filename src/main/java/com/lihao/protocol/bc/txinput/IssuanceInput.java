package com.lihao.protocol.bc.txinput;

import com.lihao.protocol.bc.AssetID;
import com.lihao.protocol.bc.Hash;
import com.lihao.protocol.bc.TxInput;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class IssuanceInput extends TxInput {

    public IssuanceInput(byte[] nonce, AssetID assetID, long amount, byte[] referenceData, Hash initialBlockHash, byte[] issuanceProgram, byte[][] arguments) {
        this.assetVersion = 1;
        this.referenceData = referenceData;
        IssuanceInputCommitment inputCommitment = new IssuanceInputCommitment();
        IssuanceWitness inputWitness = new IssuanceWitness(inputCommitment);
        inputCommitment.amount = amount;
        inputCommitment.nonce = nonce;
        inputCommitment.assetID = assetID;
        inputWitness.vmVersion = 1;
        inputWitness.initialBlockHash = initialBlockHash;
        inputWitness.issuanceProgram = issuanceProgram;
        inputWitness.arguments = arguments;
        this.inputCommitment = inputCommitment;
        this.inputWitness = inputWitness;
    }

    public IssuanceInput(){
        inputCommitment = new IssuanceInputCommitment();
        inputWitness = new IssuanceWitness((IssuanceInputCommitment)inputCommitment);
    }

    @Override
    public boolean isIssuance() {
        return true;
    }
}
