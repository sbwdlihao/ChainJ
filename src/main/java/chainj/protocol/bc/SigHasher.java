package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import chainj.protocol.bc.txinput.SpendInput;

import java.io.ByteArrayOutputStream;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public class SigHasher {

    private TxData txData;

    private Hash txHash;

    public SigHasher(TxData txData) {
        this.txData = txData;
    }

    public Hash hash(int idx) {
        if (txHash == null) {
            txHash = txData.hash();
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(txHash.getValue(), 0, txHash.getValue().length);
        BlockChain.writeVarInt31(buf, idx);

        Hash outHash = Hash.emptyHash;
        if (txData.getInputs() != null && txData.getInputs().length > idx) {
            TxInput txInput = txData.getInputs()[idx];
            if (txInput instanceof SpendInput) {
                ByteArrayOutputStream ocBuf = new ByteArrayOutputStream();
                SpendInput spendInput = (SpendInput)txInput;
                spendInput.getOutputCommitment().writeTo(ocBuf, txInput.getAssetVersion());
                outHash = new Hash(Sha3.sum256(ocBuf.toByteArray()));
            }
        }
        buf.write(outHash.getValue(), 0, outHash.getValue().length);
        return new Hash(Sha3.sum256(buf.toByteArray()));
    }
}
