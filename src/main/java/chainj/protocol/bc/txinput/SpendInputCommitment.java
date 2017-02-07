package chainj.protocol.bc.txinput;

import chainj.protocol.bc.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class SpendInputCommitment implements InputCommitment {

    private OutputID outputID = new OutputID();

    private OutputCommitment outputCommitment = new OutputCommitment();

    // The unconsumed suffix of the output commitment
    private byte[] outputCommitmentSuffix = new byte[0];

    private SpendInput spendInput;

    OutputID getOutputID() {
        return outputID;
    }

    void setOutputID(OutputID outputID) {
        Objects.requireNonNull(outputID);
        this.outputID = outputID;
    }

    OutputCommitment getOutputCommitment() {
        return outputCommitment;
    }

    void setOutputCommitment(OutputCommitment outputCommitment) {
        Objects.requireNonNull(outputCommitment);
        this.outputCommitment = outputCommitment;
    }

    private void setOutputCommitmentSuffix(byte[] outputCommitmentSuffix) {
        Objects.requireNonNull(outputCommitmentSuffix);
        this.outputCommitmentSuffix = outputCommitmentSuffix;
    }

    SpendInputCommitment(SpendInput spendInput) {
        this.spendInput = spendInput;
    }

    @Override
    public int readFrom(InputStream r) throws IOException {
        int[] n = new int[1];
        outputID.readFrom(r, n);
        setOutputCommitmentSuffix(outputCommitment.readFrom(r, 1, n));
        return n[0];
    }

    @Override
    public void writeTo(ByteArrayOutputStream w, int serFlags) {
        w.write(1); // spend type
        outputID.writeTo(w);
        if ((serFlags & Transaction.SerPrevOut) != 0) {
            outputCommitment.writeExtensibleString(w, outputCommitmentSuffix, spendInput.getAssetVersion());
        } else {
            Hash prevOutHash = outputCommitment.hash(outputCommitmentSuffix, spendInput.getAssetVersion());
            prevOutHash.write(w);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpendInputCommitment that = (SpendInputCommitment) o;

        if (outputID != null ? !outputID.equals(that.outputID) : that.outputID != null) return false;
        if (outputCommitment != null ? !outputCommitment.equals(that.outputCommitment) : that.outputCommitment != null)
            return false;
        return Arrays.equals(outputCommitmentSuffix, that.outputCommitmentSuffix);
    }

    @Override
    public int hashCode() {
        int result = outputID != null ? outputID.hashCode() : 0;
        result = 31 * result + (outputCommitment != null ? outputCommitment.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(outputCommitmentSuffix);
        return result;
    }
}
