package com.lihao.protocol.bc;

import com.lihao.crypto.Sha3;
import com.lihao.encoding.blockchain.BlockChain;
import com.lihao.protocol.bc.txinput.SpendInput;
import com.lihao.protocol.bc.txinput.SpendInputCommitment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class SigHasher {

    private TxData txData;

    private Hash txHash;

    public SigHasher(TxData txData) {
        this.txData = txData;
    }

    Hash hash(int idx) throws IOException {
        if (txHash == null) {
            txHash = txData.hash();
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(txHash.getValue());
        BlockChain.writeVarInt31(buf, idx); // TODO(bobg): check and return error

        Hash outHash = Hash.emptyHash;
        if (txData.inputs != null && txData.inputs.length > idx) {
            TxInput txInput = txData.inputs[idx];
            if (txInput instanceof SpendInput) {
                ByteArrayOutputStream ocBuf = new ByteArrayOutputStream();
                SpendInputCommitment inputCommitment = (SpendInputCommitment)txInput.inputCommitment;
                if (inputCommitment != null && inputCommitment.outputCommitment != null) {
                    inputCommitment.outputCommitment.writeTo(ocBuf, txInput.assetVersion);
                    outHash = new Hash(Sha3.Sum256(ocBuf.toByteArray()));
                }
            }
        }
        buf.write(outHash.getValue());
        return new Hash(Sha3.Sum256(buf.toByteArray()));
    }
}
