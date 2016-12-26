package com.lihao.protocol.bc;

import com.lihao.encoding.blockchain.BlockChain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class Outpoint {

    public Hash hash = new Hash();

    public int index;

    public Outpoint() {}

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Outpoint outpoint = (Outpoint) o;

        if (index != outpoint.index) return false;
        return hash != null ? hash.equals(outpoint.hash) : outpoint.hash == null;
    }

    @Override
    public int hashCode() {
        int result = hash != null ? hash.hashCode() : 0;
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return hash.toString() + ":" + (index & 0xffffffffL);
    }
}
