package com.lihao.protocol.bc;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public abstract class AbstractHash {

    protected byte[] value;

    public void readFull(InputStream in) throws IOException {
        int n = in.read(value);
        if (n != value.length) {
            throw new IOException("read not full");
        }
    }

    public void readFull(InputStream in, int[] nOut) throws IOException {
        int n = in.read(value);
        if (nOut != null && nOut.length > 0) {
            if (n != -1) {
                nOut[0] += n;
            }
        }
        if (n != value.length) {
            throw new IOException("read not full");
        }
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractHash that = (AbstractHash) o;

        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return Hex.encodeHexString(value);
    }
}
