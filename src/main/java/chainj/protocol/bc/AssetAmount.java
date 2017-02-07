package chainj.protocol.bc;

import chainj.encoding.blockchain.BlockChain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private void setAssetID(AssetID assetID) {
        Objects.requireNonNull(assetID);
        this.assetID = assetID;
    }

    public long getAmount() {
        return amount;
    }

    public AssetAmount() {}

    public AssetAmount(AssetID assetID, long amount) {
        setAssetID(assetID);
        this.amount = amount;
    }

    void readFrom(InputStream in) throws IOException {
        readFrom(in, null);
    }

    void readFrom(InputStream in, int[] nOut) throws IOException {
        assetID.readFull(in, nOut);
        this.amount = BlockChain.readVarInt63(in, nOut);
    }

    void writeTo(ByteArrayOutputStream w) {
        assetID.write(w);
        BlockChain.writeVarInt63(w, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssetAmount that = (AssetAmount) o;

        return amount == that.amount && (assetID != null ? assetID.equals(that.assetID) : that.assetID == null);
    }

    @Override
    public int hashCode() {
        int result = assetID != null ? assetID.hashCode() : 0;
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        return result;
    }
}
