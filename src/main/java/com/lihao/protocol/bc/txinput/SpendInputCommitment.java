package com.lihao.protocol.bc.txinput;

import com.lihao.protocol.bc.InputCommitment;
import com.lihao.protocol.bc.Outpoint;
import com.lihao.protocol.bc.OutputCommitment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class SpendInputCommitment implements InputCommitment {

    private Outpoint outpoint = new Outpoint();

    private OutputCommitment outputCommitment = new OutputCommitment();

    private SpendInput spendInput;

    public Outpoint getOutpoint() {
        return outpoint;
    }

    public void setOutpoint(Outpoint outpoint) {
        Objects.requireNonNull(outpoint);
        this.outpoint = outpoint;
    }

    public OutputCommitment getOutputCommitment() {
        return outputCommitment;
    }

    public void setOutputCommitment(OutputCommitment outputCommitment) {
        Objects.requireNonNull(outputCommitment);
        this.outputCommitment = outputCommitment;
    }

    public SpendInputCommitment(SpendInput spendInput) {
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
    public void writeTo(OutputStream w) throws IOException {
        w.write(new byte[]{1}); // spend type
        outpoint.writeTo(w);
        outputCommitment.writeTo(w, spendInput.getAssetVersion());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpendInputCommitment that = (SpendInputCommitment) o;

        if (outpoint != null ? !outpoint.equals(that.outpoint) : that.outpoint != null) return false;
        return outputCommitment != null ? outputCommitment.equals(that.outputCommitment) : that.outputCommitment == null;
    }

    @Override
    public int hashCode() {
        int result = outpoint != null ? outpoint.hashCode() : 0;
        result = 31 * result + (outputCommitment != null ? outputCommitment.hashCode() : 0);
        return result;
    }
}
