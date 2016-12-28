package com.lihao.protocol.bc.txinput;

import com.lihao.encoding.blockchain.BlockChain;
import com.lihao.protocol.bc.AssetID;
import com.lihao.protocol.bc.InputCommitment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public byte[] getNonce() {
        return nonce;
    }

    public void setNonce(byte[] nonce) {
        Objects.requireNonNull(nonce);
        this.nonce = nonce;
    }

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

    public IssuanceInputCommitment(){}

    @Override
    public int readFrom(InputStream r, long txVersion) throws IOException {
        int[] n = new int[1];
        setNonce(BlockChain.readVarStr31(r, n));
        assetID.readFull(r, n);
        setAmount(BlockChain.readVarInt63(r, n));
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
