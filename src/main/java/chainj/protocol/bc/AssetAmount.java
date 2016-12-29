package chainj.protocol.bc;

import chainj.encoding.blockchain.BlockChain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class AssetAmount {

    private AssetID assetID = new AssetID();

    private long amount;

    public AssetID getAssetID() {
        return assetID;
    }

    public void setAssetID(AssetID assetID) {
        Objects.requireNonNull(assetID);
        this.assetID = assetID;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public AssetAmount() {}

    public AssetAmount(AssetID assetID, long amount) {
        setAssetID(assetID);
        setAmount(amount);
    }

    void readFrom(InputStream in, int[] nOut) throws IOException {
        nOut[0] = in.read(assetID.getValue());
        if (nOut[0] != assetID.getValue().length) {
            throw new IOException("cannot readFull full assert id");
        }
        setAmount(BlockChain.readVarInt63(in, nOut));
    }

    void writeTo(OutputStream w) throws IOException {
        w.write(assetID.getValue());
        BlockChain.writeVarInt63(w, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssetAmount that = (AssetAmount) o;

        if (amount != that.amount) return false;
        return assetID != null ? assetID.equals(that.assetID) : that.assetID == null;
    }

    @Override
    public int hashCode() {
        int result = assetID != null ? assetID.hashCode() : 0;
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        return result;
    }
}
