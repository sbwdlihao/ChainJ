package chainj.protocol.bc;

import chainj.crypto.Sha3;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
public class VMContext {

    private Hash txRefDataHash = new Hash();

    private Hash refDataHash = new Hash();

    private Hash txSigHash = new Hash();

    private Hash outputID;

    private Hash entryID = new Hash();

    private Hash nonceID;

    Hash getTxRefDataHash() {
        return txRefDataHash;
    }

    Hash getRefDataHash() {
        return refDataHash;
    }

    public void setRefDataHash(Hash refDataHash) {
        Objects.requireNonNull(refDataHash);
        this.refDataHash = refDataHash;
    }

    Hash getTxSigHash() {
        return txSigHash;
    }

    Hash getOutputID() {
        return outputID;
    }

    public void setOutputID(Hash outputID) {
        this.outputID = outputID;
    }

    Hash getEntryID() {
        return entryID;
    }

    Hash getNonceID() {
        return nonceID;
    }

    public void setNonceID(Hash nonceID) {
        this.nonceID = nonceID;
    }

    public VMContext(Hash txRefDataHash, Hash entryID, Hash txID) {
        this.txRefDataHash = txRefDataHash;
        this.entryID = entryID;

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        entryID.write(buf);
        txID.write(buf);
        txSigHash = new Hash(Sha3.sum256(buf.toByteArray()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VMContext vmContext = (VMContext) o;

        if (txRefDataHash != null ? !txRefDataHash.equals(vmContext.txRefDataHash) : vmContext.txRefDataHash != null)
            return false;
        if (refDataHash != null ? !refDataHash.equals(vmContext.refDataHash) : vmContext.refDataHash != null)
            return false;
        if (txSigHash != null ? !txSigHash.equals(vmContext.txSigHash) : vmContext.txSigHash != null) return false;
        if (outputID != null ? !outputID.equals(vmContext.outputID) : vmContext.outputID != null) return false;
        if (entryID != null ? !entryID.equals(vmContext.entryID) : vmContext.entryID != null) return false;
        return nonceID != null ? nonceID.equals(vmContext.nonceID) : vmContext.nonceID == null;
    }

    @Override
    public int hashCode() {
        int result = txRefDataHash != null ? txRefDataHash.hashCode() : 0;
        result = 31 * result + (refDataHash != null ? refDataHash.hashCode() : 0);
        result = 31 * result + (txSigHash != null ? txSigHash.hashCode() : 0);
        result = 31 * result + (outputID != null ? outputID.hashCode() : 0);
        result = 31 * result + (entryID != null ? entryID.hashCode() : 0);
        result = 31 * result + (nonceID != null ? nonceID.hashCode() : 0);
        return result;
    }
}
