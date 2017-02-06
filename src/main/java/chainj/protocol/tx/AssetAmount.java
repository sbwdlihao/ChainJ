package chainj.protocol.tx;

import chainj.protocol.bc.AssetID;

/**
 * Created by sbwdlihao on 06/02/2017.
 *
 * 增加这个类的目的是为writeForHash，由于getClass().getFields()只能得到public属性，而且是所有的，所以为了避免后续对
 * chainj.protocol.bc.AssetAmount的更改（比如添加了一个public属性）导致额外的信息写入，于是建立这个类
 */
public class AssetAmount {

    public AssetID assetID = new AssetID();

    public long amount;

    AssetAmount() {
    }

    AssetAmount(chainj.protocol.bc.AssetAmount assetAmount) {
        assetID = assetAmount.getAssetID();
        amount = assetAmount.getAmount();
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
