package com.lihao.protocol.bc;

import com.lihao.crypto.Sha3;
import com.lihao.encoding.blockchain.BlockChain;
import com.lihao.protocol.bc.txinput.EmptyTxInput;
import com.lihao.protocol.bc.txinput.IssuanceInput;
import com.lihao.protocol.bc.txinput.SpendInput;

import java.io.*;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public abstract class TxInput {

    public long assetVersion;

    public InputCommitment inputCommitment;

    public byte[] referenceData = new byte[0];

    public InputWitness inputWitness;

    public static TxInput readFrom(InputStream r, long txVersion) throws IOException {
        TxInput txInput = new EmptyTxInput();
        long assetVersion = BlockChain.readVarInt63(r);
        byte[] inputCommitment = BlockChain.readVarStr31(r);
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
        txInput.referenceData = BlockChain.readVarStr31(r);
        byte[] inputWitness = BlockChain.readVarStr31(r);
        ByteArrayInputStream iwBuf = new ByteArrayInputStream(inputWitness);
        txInput.inputWitness.readFrom(iwBuf);
        return txInput;
    }

    public void writeTo(OutputStream w, int serFlags) throws IOException {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TxInput txInput = (TxInput) o;

        if (assetVersion != txInput.assetVersion) return false;
        if (inputCommitment != null ? !inputCommitment.equals(txInput.inputCommitment) : txInput.inputCommitment != null)
            return false;
        if (!Arrays.equals(referenceData, txInput.referenceData)) return false;
        return inputWitness != null ? inputWitness.equals(txInput.inputWitness) : txInput.inputWitness == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (assetVersion ^ (assetVersion >>> 32));
        result = 31 * result + (inputCommitment != null ? inputCommitment.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(referenceData);
        result = 31 * result + (inputWitness != null ? inputWitness.hashCode() : 0);
        return result;
    }
}
