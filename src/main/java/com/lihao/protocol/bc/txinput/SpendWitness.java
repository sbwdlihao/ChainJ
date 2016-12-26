package com.lihao.protocol.bc.txinput;

import com.lihao.protocol.bc.BCUtil;
import com.lihao.protocol.bc.InputWitness;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class SpendWitness implements InputWitness {

    public byte[][] arguments = new byte[0][];

    @Override
    public void readFrom(InputStream r) throws IOException {
        arguments = BCUtil.readDyadicArray(r);
    }

    @Override
    public void writeTo(OutputStream w) throws IOException {
        BCUtil.writeDyadicArray(w, arguments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpendWitness witness = (SpendWitness) o;

        return Arrays.deepEquals(arguments, witness.arguments);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(arguments);
    }
}
