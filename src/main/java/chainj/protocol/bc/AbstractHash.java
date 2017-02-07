package chainj.protocol.bc;

import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public abstract class AbstractHash {

    protected byte[] value = new byte[32];

    AbstractHash(byte... bytes) {
        if (bytes.length > 32) {
            throw new IllegalArgumentException("bytes length must be 32 byte array");
        }
        value = new byte[32];
        System.arraycopy(bytes, 0, value, 0, bytes.length);
        if (bytes.length < 32) {
            System.arraycopy(new byte[32 - bytes.length], 0, value, bytes.length, 32 - bytes.length);
        }
    }

    public byte[] getValue() {
        return value;
    }

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

    public void write(ByteArrayOutputStream w) {
        w.write(value, 0, value.length);
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
