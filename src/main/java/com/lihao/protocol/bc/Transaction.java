package com.lihao.protocol.bc;

import com.lihao.crypto.Sha3;
import com.lihao.encoding.blockchain.BlockChain;
import com.lihao.io.WriteTo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class Transaction implements WriteTo{
    // These flags are part of the wire protocol;
    // they must not change.
    static final byte SerWitness = 1;
    static final byte SerPrevout = 2;
    static final byte SerMetadata = 4;
    // Bit mask for accepted serialization flags.
    // All other flag bits must be 0.
    static final byte SerValid = 0x07;
    static final byte SerRequired = 0x07; // we support only this combination of flags

    static void writeRefData(OutputStream w, byte[] data, byte serFlags) throws IOException {
        if ((serFlags & SerMetadata) != 0) {
            BlockChain.writeVarStr31(w, data); // TODO(bobg): check and return error
        } else {
            Hash.writeFastHash(w, data);
        }
    }

    public TxData txData;

    public Hash hash;

    public Transaction() {}

    public Transaction(TxData txData) throws IOException {
        this.txData = txData;
        this.hash = txData.hash();
    }

    public void writeTo(OutputStream io) throws IOException {
        txData.writeTo(io, SerRequired);
    }

    public Hash witnessHash() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(hash.getValue());

        txData.writeInputsWitnessTo(buf);
        txData.writeOutputsWitnessTo(buf);
        return new Hash(Sha3.Sum256(buf.toByteArray()));
    }

}
