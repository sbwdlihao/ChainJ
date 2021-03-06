package chainj.protocol.bc.txinput;

import chainj.encoding.blockchain.BlockChain;
import chainj.protocol.bc.InputWitness;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    void setArguments(byte[][] arguments) {
        Objects.requireNonNull(arguments);
        this.arguments = arguments;
    }

    @Override
    public void readFrom(InputStream r) throws IOException {
        setArguments(BlockChain.readVarStrList(r));
    }

    @Override
    public void writeTo(ByteArrayOutputStream w) {
        BlockChain.writeVarStrList(w, arguments);
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
