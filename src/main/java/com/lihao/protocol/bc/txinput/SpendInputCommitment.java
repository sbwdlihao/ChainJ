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

    public Outpoint outpoint;

    public OutputCommitment outputCommitment;

    private SpendInput spendInput;

    public SpendInputCommitment(SpendInput spendInput) {
        outpoint = new Outpoint();
        outputCommitment = new OutputCommitment();
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
}
