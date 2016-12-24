package com.lihao.protocol.bc;

import com.lihao.encoding.blockchain.BlockChain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class Outpoint {

    public Hash hash;

    public int index;

    public Outpoint() {
        hash = new Hash();
    }

    public Outpoint(Hash hash, int index) {
        this.hash = hash;
        this.index = index;
    }

    public void readFrom(InputStream r, int[] nOut) throws IOException {
        hash.readFull(r, nOut);
        index = BlockChain.readVarInt31(r, nOut);
    }

    public int writeTo(OutputStream w) throws IOException {
        w.write(hash.getValue());
        int n = BlockChain.writeVarInt31(w, index);
        return hash.getValue().length + n;
    }

    @Override
    public String toString() {
        return hash.toString() + ":" + (index & 0xffffffffL);
    }
}
