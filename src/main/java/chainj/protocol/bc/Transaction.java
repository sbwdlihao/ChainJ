package chainj.protocol.bc;

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
    public static final int SerPrevOut = 2;
    static final int SerMetadata = 4;
    // Bit mask for accepted serialization flags.
    // All other flag bits must be 0.
    public static final int SerTxHash = 0x00; // this is used only for computing transaction hash - prevout and refdata are replaced with their hashes
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

    private TxHashes txHashes = new TxHashes();

    public TxData getTxData() {
        return txData;
    }

    public void setTxData(TxData txData) {
        Objects.requireNonNull(txData);
        this.txData = txData;
    }

    private void setTxHashes(TxHashes txHashes) {
        Objects.requireNonNull(txHashes);
        this.txHashes = txHashes;
    }

    public Hash ID() {
        return txHashes.getId();
    }

    public long getVersion() {
        return txData.getVersion();
    }

    public byte[] getReferenceData() {
        return txData.getReferenceData();
    }

    public TxInput[] getInputs() {
        return txData.getInputs();
    }

    public TxOutput[] getOutputs() {
        return txData.getOutputs();
    }

    public long getMinTime() {
        return txData.getMinTime();
    }

    public long getMaxTime() {
        return txData.getMaxTime();
    }

    public Transaction() {}

    public Transaction(TxData txData) {
        setTxData(txData);
        setTxHashes(chainj.protocol.tx.Transaction.txHashes(txData));
    }

    public void writeTo(ByteArrayOutputStream w) {
        txData.writeTo(w, SerRequired);
    }

//    Hash witnessHash() {
//        ByteArrayOutputStream buf = new ByteArrayOutputStream();
//        buf.write(hash.getValue(), 0, hash.getValue().length);
//
//        txData.writeInputsWitnessTo(buf);
//        txData.writeOutputsWitnessTo(buf);
//        return new Hash(Sha3.sum256(buf.toByteArray()));
//    }

    public Hash issuanceHash(int idx) {
        return txData.issuanceHash(idx);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (txData != null ? !txData.equals(that.txData) : that.txData != null) return false;
        return txHashes != null ? txHashes.equals(that.txHashes) : that.txHashes == null;
    }

    @Override
    public int hashCode() {
        int result = txData != null ? txData.hashCode() : 0;
        result = 31 * result + (txHashes != null ? txHashes.hashCode() : 0);
        return result;
    }
}
