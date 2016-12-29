package chainj.protocol.bc.txinput;

import chainj.protocol.bc.BCUtil;
import chainj.protocol.bc.InputWitness;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class SpendWitness implements InputWitness {

    private byte[][] arguments = new byte[0][];

    public byte[][] getArguments() {
        return arguments;
    }

    public void setArguments(byte[][] arguments) {
        Objects.requireNonNull(arguments);
        this.arguments = arguments;
    }

    @Override
    public void readFrom(InputStream r) throws IOException {
        setArguments(BCUtil.readDyadicArray(r));
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
