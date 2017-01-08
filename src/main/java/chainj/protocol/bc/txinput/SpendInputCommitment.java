package chainj.protocol.bc.txinput;

import chainj.protocol.bc.InputCommitment;
import chainj.protocol.bc.Outpoint;
import chainj.protocol.bc.OutputCommitment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class SpendInputCommitment implements InputCommitment {

    private Outpoint outpoint = new Outpoint();

    private OutputCommitment outputCommitment = new OutputCommitment();

    private SpendInput spendInput;

    Outpoint getOutpoint() {
        return outpoint;
    }

    void setOutpoint(Outpoint outpoint) {
        Objects.requireNonNull(outpoint);
        this.outpoint = outpoint;
    }

    OutputCommitment getOutputCommitment() {
        return outputCommitment;
    }

    void setOutputCommitment(OutputCommitment outputCommitment) {
        Objects.requireNonNull(outputCommitment);
        this.outputCommitment = outputCommitment;
    }

    SpendInputCommitment(SpendInput spendInput) {
        this.spendInput = spendInput;
    }

    @Override
    public int readFrom(InputStream r, long txVersion) throws IOException {
        int[] n = new int[1];
        outpoint.readFrom(r, n);
        outputCommitment.readFrom(r, txVersion, 1, n);
        return n[0];
    }

    @Override
    public void writeTo(ByteArrayOutputStream w) {
        w.write(1); // spend type
        outpoint.writeTo(w);
        outputCommitment.writeTo(w, spendInput.getAssetVersion());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpendInputCommitment that = (SpendInputCommitment) o;

        return (outpoint != null ? outpoint.equals(that.outpoint) : that.outpoint == null) &&
                (outputCommitment != null ? outputCommitment.equals(that.outputCommitment) : that.outputCommitment == null);
    }

    @Override
    public int hashCode() {
        int result = outpoint != null ? outpoint.hashCode() : 0;
        result = 31 * result + (outputCommitment != null ? outputCommitment.hashCode() : 0);
        return result;
    }
}
