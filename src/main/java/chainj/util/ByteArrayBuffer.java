package chainj.util;

/**
 * Created by sbwdlihao on 06/01/2017.
 */
public class ByteArrayBuffer {

    private byte[] rawData;
    private int size;

    private ByteArrayBuffer(byte[] raw, int size) {
        this.rawData = raw;
        this.size = size;
    }

    static public ByteArrayBuffer make() {
        return new ByteArrayBuffer(new byte[0], 0);
    }

    public ByteArrayBuffer append(byte... bytes) {
        if (bytes.length == 0) {
            return this;
        }
        if (bytes.length <= rawData.length - size) {
            System.arraycopy(bytes, 0, rawData, size, bytes.length);
        } else {
            int newSize = Math.max(rawData.length << 1, size + bytes.length);
            byte[] newRawData = new byte[newSize];
            System.arraycopy(rawData, 0, newRawData, 0, size);
            System.arraycopy(bytes, 0, newRawData, size, bytes.length);
            rawData = newRawData;
        }
        size += bytes.length;
        return this;
    }

    public ByteArrayBuffer append(byte[] bytes, int length) {
        if (bytes.length == 0 || length <= 0) {
            return this;
        }
        if (length > bytes.length) {
            throw new IllegalArgumentException("length overflow");
        }
        if (length <= rawData.length - size) {
            System.arraycopy(bytes, 0, rawData, size, length);
        } else {
            int newSize = Math.max(rawData.length << 1, size + length);
            byte[] newRawData = new byte[newSize];
            System.arraycopy(rawData, 0, newRawData, 0, size);
            System.arraycopy(bytes, 0, newRawData, size, length);
            rawData = newRawData;
        }
        size += length;
        return this;
    }

    public void copyOfRange(int start, byte[] src) {
        System.arraycopy(src, 0, rawData, start, src.length);
    }

    public int length() {
        return size;
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[size];
        System.arraycopy(rawData, 0, bytes, 0, size);
        return bytes;
    }
}
