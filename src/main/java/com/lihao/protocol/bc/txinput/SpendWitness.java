package com.lihao.protocol.bc.txinput;

import com.lihao.encoding.blockchain.BlockChain;
import com.lihao.protocol.bc.InputWitness;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class SpendWitness implements InputWitness {

    public byte[][] arguments;

    @Override
    public void readFrom(InputStream r) throws IOException {
        int n = BlockChain.readVarInt31(r, null);
        arguments = new byte[n][];
        for(int i = 0; i < n; i++) {
            arguments[i] = BlockChain.readVarStr31(r, null);
        }
    }

    @Override
    public void writeTo(OutputStream w) throws IOException {
        if (arguments != null) {
            BlockChain.writeVarInt31(w, arguments.length); // TODO(bobg): check and return error
            for (byte[] argument : arguments) {
                BlockChain.writeVarStr31(w, argument); // TODO(bobg): check and return error
            }
        } else {
            BlockChain.writeVarInt31(w, 0);
        }
    }
}
