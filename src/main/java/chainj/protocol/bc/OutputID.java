package chainj.protocol.bc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
public class OutputID {

    private Hash hash = new Hash();

    public Hash getHash() {
        return hash;
    }

    public OutputID() {
    }

    public OutputID(Hash hash) {
        this.hash = hash;
    }

    public void writeTo(ByteArrayOutputStream w) {
        w.write(hash.getValue(), 0, hash.getValue().length);
    }

    public void readFrom(InputStream r) throws IOException {
        readFrom(r, null);
    }

    public void readFrom(InputStream r, int[] nOut) throws IOException {
        hash.readFull(r, nOut);
    }
}
