package chainj.protocol.bc;

import chainj.encoding.blockchain.BlockChain;
import chainj.util.function.ExceptionFunction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 04/02/2017.
 */
class BlockWitness {

    // Witness is a vector of arguments to the previous block's
    // ConsensusProgram for validating this block.
    private byte[][] witness = new byte[0][];

    byte[][] getWitness() {
        return witness;
    }

    void setWitness(byte[][] witness) {
        Objects.requireNonNull(witness);
        this.witness = witness;
    }

    void readFrom(InputStream r) throws IOException {
        setWitness(BlockChain.readVarStrList(r));
    }

    void writeTo(ByteArrayOutputStream w) {
        BlockChain.writeVarStrList(w, witness);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockWitness that = (BlockWitness) o;

        return Arrays.deepEquals(witness, that.witness);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(witness);
    }
}
