package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;

/**
 * Created by sbwdlihao on 21/12/2016.
 *
 * Hash represents a 256-bit hash.  By convention, Hash objects are typically passed as values, not as pointers.
 */
public class Hash extends AbstractHash{
    static final Hash emptyHash;

    static {
        emptyHash = new Hash(Sha3.sum256(null));
    }

    public Hash(){}

    public Hash(byte... bytes) {
        super(bytes);
    }

    public String toString() {
        return new String(Hex.encode(value));
    }

    static void writeFastHash(ByteArrayOutputStream w, byte[] d) {
        if (d == null || d.length == 0) {
            BlockChain.writeVarStr31(w, null);
            return;
        }
        byte[] h = Sha3.sum256(d);
        BlockChain.writeVarStr31(w, h);
    }
}
