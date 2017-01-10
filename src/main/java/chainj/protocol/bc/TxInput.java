package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import chainj.protocol.bc.txinput.EmptyTxInput;
import chainj.protocol.bc.txinput.IssuanceInput;
import chainj.protocol.bc.txinput.SpendInput;
import chainj.protocol.bc.txinput.SpendWitness;
import chainj.protocol.state.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public abstract class TxInput {

    protected long assetVersion;

    protected InputCommitment inputCommitment;

    private byte[] referenceData = new byte[0];

    protected InputWitness inputWitness;

    public long getAssetVersion() {
        return assetVersion;
    }

    protected void setInputCommitment(InputCommitment inputCommitment) {
        Objects.requireNonNull(inputCommitment);
        this.inputCommitment = inputCommitment;
    }

    public byte[] getReferenceData() {
        return referenceData;
    }

    protected void setReferenceData(byte[] referenceData) {
        Objects.requireNonNull(referenceData);
        this.referenceData = referenceData;
    }

    protected void setInputWitness(InputWitness inputWitness) {
        Objects.requireNonNull(inputWitness);
        this.inputWitness = inputWitness;
    }

    public static TxInput readFrom(InputStream r, long txVersion) throws IOException {
        TxInput txInput = new EmptyTxInput();
        long assetVersion = BlockChain.readVarInt63(r);
        byte[] inputCommitment = BlockChain.readVarStr31(r);
        if (assetVersion == 1) {
            ByteArrayInputStream icBuf = new ByteArrayInputStream(inputCommitment);
            int icType = icBuf.read();
            if (icType == -1) {
                throw new IOException("read ic type null");
            }
            int bytesRead = 1;
            txInput = createTxInput(icType);
            bytesRead += txInput.inputCommitment.readFrom(icBuf, txVersion);
            if (txVersion == 1 && bytesRead < inputCommitment.length) {
                throw new IOException("unrecognized extra data in input commitment for transaction version 1");
            }
        }
        txInput.assetVersion = assetVersion;
        txInput.setReferenceData(BlockChain.readVarStr31(r));
        byte[] inputWitness = BlockChain.readVarStr31(r);
        ByteArrayInputStream iwBuf = new ByteArrayInputStream(inputWitness);
        txInput.inputWitness.readFrom(iwBuf);
        return txInput;
    }

    public void writeTo(ByteArrayOutputStream w, int serFlags) {
        BlockChain.writeVarInt63(w, assetVersion);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        inputCommitment.writeTo(buf);
        BlockChain.writeVarStr31(w, buf.toByteArray());
        BlockChain.writeVarStr31(w, referenceData);
        if ((serFlags & Transaction.SerWitness) != 0) {
            buf.reset();
            inputWitness.writeTo(buf);
            BlockChain.writeVarStr31(w, buf.toByteArray());
        }
    }

    public Output prevOutput() {
        AssetAmount assetAmount = assetAmount();
        TxOutput txOutput = new TxOutput(assetAmount.getAssetID(), assetAmount.getAmount(), controlProgram(), new byte[0]);
        return new Output(outpoint(), txOutput);
    }

    Hash witnessHash() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        inputWitness.writeTo(buf);
        return new Hash(Sha3.sum256(buf.toByteArray()));
    }

    public AssetID assertID() {
        return assetAmount().getAssetID();
    }

    public long amount() {
        return assetAmount().getAmount();
    }

    protected AssetAmount assetAmount() {
        return new AssetAmount();
    }

    protected byte[] controlProgram() {
        return new byte[0];
    }

    public Outpoint outpoint() {
        return new Outpoint();
    }

    public long vmVersion() {
        return 0;
    }

    public byte[] vmProgram() {
        return new byte[0];
    }

    public byte[][] arguments() {
        if (inputWitness instanceof SpendWitness) {
            return ((SpendWitness)inputWitness).getArguments();
        }
        return new byte[0][];
    }

    private static TxInput createTxInput(int icType) throws IOException {
        switch (icType) {
            case 0:
                return new IssuanceInput();
            case 1:
                return new SpendInput();
            default:
                throw new IOException("unsupported input type " + icType);
        }
    }

    public boolean isIssuance() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TxInput txInput = (TxInput) o;

        return assetVersion == txInput.assetVersion &&
                (inputCommitment != null ? inputCommitment.equals(txInput.inputCommitment) : txInput.inputCommitment == null) &&
                Arrays.equals(referenceData, txInput.referenceData) &&
                (inputWitness != null ? inputWitness.equals(txInput.inputWitness) : txInput.inputWitness == null);
    }

    @Override
    public int hashCode() {
        int result = (int) (assetVersion ^ (assetVersion >>> 32));
        result = 31 * result + (inputCommitment != null ? inputCommitment.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(referenceData);
        result = 31 * result + (inputWitness != null ? inputWitness.hashCode() : 0);
        return result;
    }
}
