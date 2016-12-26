package com.lihao.protocol.bc.txinput;

import com.lihao.protocol.bc.InputCommitment;
import com.lihao.protocol.bc.Outpoint;
import com.lihao.protocol.bc.OutputCommitment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class SpendInputCommitment implements InputCommitment {

    public Outpoint outpoint = new Outpoint();

    public OutputCommitment outputCommitment = new OutputCommitment();

    private SpendInput spendInput;

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
        outputCommitment.writeTo(w, spendInput.assetVersion);
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
