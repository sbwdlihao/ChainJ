package chainj.protocol.vm;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 03/01/2017.
 */
public class Instruction {

    private byte op;

    private int len;

    private byte[] data = new byte[0];

    public byte getOp() {
        return op;
    }

    void setOp(byte op) {
        this.op = op;
    }

    int getLen() {
        return len;
    }

    void setLen(int len) {
        this.len = len;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        Objects.requireNonNull(data);
        this.data = data;
    }

    Instruction() {
    }

    Instruction(byte op, int len) {
        this.op = op;
        this.len = len;
    }

    Instruction(byte op, int len, byte[] data) {
        this.op = op;
        this.len = len;
        setData(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instruction that = (Instruction) o;

        if (op != that.op) return false;
        if (len != that.len) return false;
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = (int) op;
        result = 31 * result + len;
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
