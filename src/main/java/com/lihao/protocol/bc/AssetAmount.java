package com.lihao.protocol.bc;

import com.lihao.encoding.blockchain.BlockChain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 21/12/2016.
 */
public class AssetAmount {

    public AssetID assetID;

    public long amount;

    public AssetAmount(AssetID assetID, long amount) {
        this.assetID = assetID;
        this.amount = amount;
    }

    void readFrom(InputStream in, int[] nOut) throws IOException {
        nOut[0] = in.read(assetID.getValue());
        if (nOut[0] != assetID.getValue().length) {
            throw new IOException("cannot readFull full assert id");
        }
        amount = BlockChain.readVarInt63(in, nOut);
    }

    void writeTo(OutputStream w) throws IOException {
        w.write(assetID.getValue());
        BlockChain.writeVarInt63(w, amount);
    }
}
