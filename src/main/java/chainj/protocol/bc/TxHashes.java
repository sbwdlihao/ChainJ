package chainj.protocol.bc;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
public class TxHashes {
    private Hash id = new Hash();

    private Hash[] outputIDs = new Hash[0]; // each OutputID is also the corresponding UnspentID

    private Issuance[] issuances = new Issuance[0];

    private VMContext[] vmContexts; // one per old-style Input

    public Hash getId() {
        return id;
    }

    public void setId(Hash id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    public void setOutputIDs(Hash[] outputIDs) {
        Objects.requireNonNull(outputIDs);
        this.outputIDs = outputIDs;
    }

    public void setIssuances(Issuance[] issuances) {
        Objects.requireNonNull(issuances);
        this.issuances = issuances;
    }

    public VMContext[] getVmContexts() {
        return vmContexts;
    }

    public void setVmContexts(VMContext[] vmContexts) {
        this.vmContexts = vmContexts;
    }

    public TxHashes() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TxHashes txHashes = (TxHashes) o;

        if (id != null ? !id.equals(txHashes.id) : txHashes.id != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(outputIDs, txHashes.outputIDs)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(issuances, txHashes.issuances)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(vmContexts, txHashes.vmContexts);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(outputIDs);
        result = 31 * result + Arrays.hashCode(issuances);
        result = 31 * result + Arrays.hashCode(vmContexts);
        return result;
    }
}