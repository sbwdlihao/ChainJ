package chainj.protocol.state;

import chainj.protocol.bc.Outpoint;
import chainj.protocol.bc.TxOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by sbwdlihao on 28/12/2016.
 *
 * Output represents a spent or unspent output for the validation process.
 */
public class Output {

    private Outpoint outpoint;
    private TxOutput txOutput;

    public Outpoint getOutpoint() {
        return outpoint;
    }

    public void setOutpoint(Outpoint outpoint) {
        Objects.requireNonNull(outpoint);
        this.outpoint = outpoint;
    }

    public TxOutput getTxOutput() {
        return txOutput;
    }

    public void setTxOutput(TxOutput txOutput) {
        Objects.requireNonNull(txOutput);
        this.txOutput = txOutput;
    }

    public Output(Outpoint outpoint, TxOutput txOutput) {
        setOutpoint(outpoint);
        setTxOutput(txOutput);
    }

    // OutputTreeItem returns the key of an output in the state tree,
    // as well as the output commitment (a second []byte) for Inserts
    // into the state tree.
    public void outputTreeItem(ByteArrayOutputStream outpointKey, ByteArrayOutputStream commitment) {
        outpoint.writeTo(outpointKey);
        txOutput.getOutputCommitment().writeTo(commitment, txOutput.getAssetVersion());
    }
}
