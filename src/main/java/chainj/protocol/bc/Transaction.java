package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import chainj.io.WriteTo;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class Transaction implements WriteTo {
    // CurrentTransactionVersion is the current latest
    // supported transaction version.
    static final int CurrentTransactionVersion = 1;

    // These flags are part of the wire protocol;
    // they must not change.
    static final int SerWitness = 1;
    static final int SerPrevout = 2;
    static final int SerMetadata = 4;
    // Bit mask for accepted serialization flags.
    // All other flag bits must be 0.
    static final int SerValid = 0x07;
    static final int SerRequired = 0x07; // we support only this combination of flags

    static void writeRefData(ByteArrayOutputStream w, byte[] data, int serFlags) {
        if ((serFlags & SerMetadata) != 0) {
            BlockChain.writeVarStr31(w, data);
        } else {
            Hash.writeFastHash(w, data);
        }
    }

    private TxData txData = new TxData();

    private Hash hash = new Hash();

    public TxData getTxData() {
        return txData;
    }

    public void setTxData(TxData txData) {
        Objects.requireNonNull(txData);
        this.txData = txData;
    }

    public Hash getHash() {
        return hash;
    }

    public void setHash(Hash hash) {
        Objects.requireNonNull(hash);
        this.hash = hash;
    }

    public TxInput[] getInputs() {
        return txData.getInputs();
    }

    public Transaction() {}

    public Transaction(TxData txData) {
        setTxData(txData);
        setHash(txData.hash());
    }

    public void writeTo(ByteArrayOutputStream w) {
        txData.writeTo(w, SerRequired);
    }

    public Hash witnessHash() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(hash.getValue(), 0, hash.getValue().length);

        txData.writeInputsWitnessTo(buf);
        txData.writeOutputsWitnessTo(buf);
        return new Hash(Sha3.Sum256(buf.toByteArray()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (txData != null ? !txData.equals(that.txData) : that.txData != null) return false;
        return hash != null ? hash.equals(that.hash) : that.hash == null;
    }

    @Override
    public int hashCode() {
        int result = txData != null ? txData.hashCode() : 0;
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        return result;
    }
}
