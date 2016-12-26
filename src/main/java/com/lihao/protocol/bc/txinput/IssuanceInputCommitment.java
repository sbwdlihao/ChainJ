package com.lihao.protocol.bc.txinput;

import com.lihao.encoding.blockchain.BlockChain;
import com.lihao.protocol.bc.AssetID;
import com.lihao.protocol.bc.InputCommitment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class IssuanceInputCommitment implements InputCommitment {

    public byte[] nonce = new byte[0];

    // Note: as long as we require serflags=0x7, we don't need to
    // explicitly store the asset ID here even though it's technically
    // part of the input commitment. We can compute it instead from
    // values in the witness (which, with serflags other than 0x7,
    // might not be present).
    public AssetID assetID = new AssetID();

    public long amount;

    public IssuanceInputCommitment(){}

    @Override
    public int readFrom(InputStream r, long txVersion) throws IOException {
        int[] n = new int[1];
        nonce = BlockChain.readVarStr31(r, n);
        assetID.readFull(r, n);
        amount = BlockChain.readVarInt63(r, n);
        return n[0];
    }

    @Override
    public void writeTo(OutputStream w) throws IOException {
        w.write(new byte[]{0}); // issuance type
        BlockChain.writeVarStr31(w, nonce);
        w.write(assetID.getValue());
        BlockChain.writeVarInt63(w, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IssuanceInputCommitment that = (IssuanceInputCommitment) o;

        if (amount != that.amount) return false;
        if (!Arrays.equals(nonce, that.nonce)) return false;
        return assetID != null ? assetID.equals(that.assetID) : that.assetID == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(nonce);
        result = 31 * result + (assetID != null ? assetID.hashCode() : 0);
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        return result;
    }
}
