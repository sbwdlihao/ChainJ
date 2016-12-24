package com.lihao.protocol.bc;

import com.lihao.crypto.Sha3;
import com.lihao.encoding.blockchain.BlockChain;
import com.lihao.protocol.bc.txinput.EmptyTxInput;
import com.lihao.protocol.bc.txinput.IssuanceInput;
import com.lihao.protocol.bc.txinput.SpendInput;

import java.io.*;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public abstract class TxInput {

    public long assetVersion;

    public InputCommitment inputCommitment;

    public byte[] referenceData;

    public InputWitness inputWitness;

    public static TxInput readFrom(InputStream r, long txVersion) throws IOException {
        TxInput txInput = new EmptyTxInput();
        long assetVersion = BlockChain.readVarInt63(r, null);
        byte[] inputCommitment = BlockChain.readVarStr31(r, null);
        if (assetVersion == 1) {
            ByteArrayInputStream icBuf = new ByteArrayInputStream(inputCommitment);
            int icType = icBuf.read();
            if (icType == -1) {
                throw new IOException("read ic type null");
            }
            int bytesRead = 1;
            txInput = createTxInput(icType);
            bytesRead += txInput.inputCommitment.readFrom(icBuf, txVersion);
            if (txVersion == 1 && bytesRead < inputCommitment.length) {
                throw new IOException("unrecognized extra data in input commitment for transaction version 1");
            }
        }
        txInput.assetVersion = assetVersion;
        txInput.referenceData = BlockChain.readVarStr31(r, null);
        byte[] inputWitness = BlockChain.readVarStr31(r, null);
        ByteArrayInputStream iwBuf = new ByteArrayInputStream(inputWitness);
        txInput.inputWitness.readFrom(iwBuf);
        return txInput;
    }

    public void writeTo(OutputStream w, byte serFlags) throws IOException {
        BlockChain.writeVarInt63(w, assetVersion);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        inputCommitment.writeTo(buf);
        BlockChain.writeVarStr31(w, buf.toByteArray());
        BlockChain.writeVarStr31(w, referenceData);
        if ((serFlags & Transaction.SerWitness) != 0) {
            buf.reset();
            inputWitness.writeTo(buf);
            BlockChain.writeVarStr31(w, buf.toByteArray());
        }
    }

    public Hash witnessHash() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        inputWitness.writeTo(buf);
        return new Hash(Sha3.Sum256(buf.toByteArray()));
    }

    private static TxInput createTxInput(int icType) throws IOException {
        switch (icType) {
            case 0:
                return new IssuanceInput();
            case 1:
                return new SpendInput();
            default:
                throw new IOException("unsupported input type " + icType);
        }
    }

    public boolean isIssuance() {
        return false;
    }
}
