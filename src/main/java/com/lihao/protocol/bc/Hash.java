package com.lihao.protocol.bc;

import com.lihao.crypto.Sha3;
import com.lihao.encoding.blockchain.BlockChain;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sbwdlihao on 21/12/2016.
 *
 * Hash represents a 256-bit hash.  By convention, Hash objects are typically passed as values, not as pointers.
 */
public class Hash extends AbstractHash{
    static final Hash emptyHash;

    static {
        emptyHash = new Hash(Sha3.Sum256(null));
    }

    public Hash(){
        value = new byte[32];
    }

    public Hash(byte[] value) {
        if (value == null || value.length != 32) {
            throw new IllegalArgumentException("hash must be 32 byte array");
        }
        this.value = value;
    }

    public byte[] marshalText() {
        return Hex.encode(value);
    }

    public void unMarshalText(byte[] hex) {
        value = Hex.decode(hex);
    }

    public String toString() {
        return new String(marshalText());
    }

    public static Hash parseHash(String s) {
        return new Hash(Hex.decode(s));
    }

    static void writeFastHash(OutputStream w, byte[] d) throws IOException {
        if (d == null || d.length == 0) {
            BlockChain.writeVarStr31(w, null);
            return;
        }
        byte[] h = Sha3.Sum256(d);
        BlockChain.writeVarStr31(w, h);
    }
}
