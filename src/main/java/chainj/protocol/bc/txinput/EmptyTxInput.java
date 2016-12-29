package chainj.protocol.bc.txinput;

import chainj.protocol.bc.InputCommitment;
import chainj.protocol.bc.InputWitness;
import chainj.protocol.bc.TxInput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class EmptyTxInput extends TxInput {

    public EmptyTxInput() {
        setInputCommitment(new EmptyInputCommitment());
        setInputWitness(new EmptyInputWitness());
    }

    class EmptyInputCommitment implements InputCommitment {
        @Override
        public int readFrom(InputStream r, long txVersion) throws IOException {
            return 0;
        }

        @Override
        public void writeTo(OutputStream w) throws IOException {
        }
    }

    class EmptyInputWitness implements InputWitness {
        @Override
        public void readFrom(InputStream r) throws IOException {

        }

        @Override
        public void writeTo(OutputStream w) throws IOException {

        }
    }
}
