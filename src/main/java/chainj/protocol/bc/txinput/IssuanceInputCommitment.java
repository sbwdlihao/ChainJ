package chainj.protocol.bc.txinput;

import chainj.encoding.blockchain.BlockChain;
import chainj.protocol.bc.AssetID;
import chainj.protocol.bc.InputCommitment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class IssuanceInputCommitment implements InputCommitment {

    private byte[] nonce = new byte[0];

    // Note: as long as we require serFlags=0x7, we don't need to
    // explicitly store the asset ID here even though it's technically
    // part of the input commitment. We can compute it instead from
    // values in the witness (which, with serFlags other than 0x7,
    // might not be present).
    private AssetID assetID = new AssetID();

    private long amount;

    byte[] getNonce() {
        return nonce;
    }

    void setNonce(byte[] nonce) {
        Objects.requireNonNull(nonce);
        this.nonce = nonce;
    }

    AssetID getAssetID() {
        return assetID;
    }

    void setAssetID(AssetID assetID) {
        Objects.requireNonNull(assetID);
        this.assetID = assetID;
    }

    long getAmount() {
        return amount;
    }

    void setAmount(long amount) {
        this.amount = amount;
    }

    IssuanceInputCommitment(){}

    @Override
    public int readFrom(InputStream r, long txVersion) throws IOException {
        int[] n = new int[1];
        setNonce(BlockChain.readVarStr31(r, n));
        assetID.readFull(r, n);
        amount = BlockChain.readVarInt63(r, n);
        return n[0];
    }

    @Override
    public void writeTo(ByteArrayOutputStream w) {
        w.write(0); // issuance type
        BlockChain.writeVarStr31(w, nonce);
        w.write(assetID.getValue(), 0, assetID.getValue().length);
        BlockChain.writeVarInt63(w, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IssuanceInputCommitment that = (IssuanceInputCommitment) o;

        return amount == that.amount &&
                Arrays.equals(nonce, that.nonce) &&
                (assetID != null ? assetID.equals(that.assetID) : that.assetID == null);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(nonce);
        result = 31 * result + (assetID != null ? assetID.hashCode() : 0);
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        return result;
    }
}
