package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import chainj.protocol.bc.txinput.EmptyTxInput;
import chainj.protocol.bc.txinput.IssuanceInput;
import chainj.protocol.bc.txinput.SpendInput;
import chainj.protocol.bc.txinput.SpendWitness;
import chainj.protocol.state.Output;

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

    // Unconsumed suffixes of the commitment and witness extensible
    // strings.
    private byte[] commitmentSuffix = new byte[0];
    private byte[] witnessSuffix = new byte[0];

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

    private void setCommitmentSuffix(byte[] commitmentSuffix) {
        Objects.requireNonNull(commitmentSuffix);
        this.commitmentSuffix = commitmentSuffix;
    }

    private void setWitnessSuffix(byte[] witnessSuffix) {
        Objects.requireNonNull(witnessSuffix);
        this.witnessSuffix = witnessSuffix;
    }

    static TxInput readFrom(InputStream r) throws IOException {
        TxInput txInput = new EmptyTxInput();
        TxInput[] txInputs = new TxInput[1];
        txInputs[0] = txInput;
        long assetVersion = BlockChain.readVarInt63(r);
        byte[] commitmentSuffix = BlockChain.readExtensibleString(r, buf -> {
            if (assetVersion == 1) {
                int icType = buf.read();
                txInputs[0] = createTxInput(icType);
                txInputs[0].inputCommitment.readFrom(buf);
            }
        });
        txInput = txInputs[0];
        txInput.setCommitmentSuffix(commitmentSuffix);
        txInput.assetVersion = assetVersion;
        txInput.setReferenceData(BlockChain.readVarStr31(r));
        txInput.setWitnessSuffix(BlockChain.readExtensibleString(r, buf -> txInputs[0].inputWitness.readFrom(buf)));
        return txInput;
    }

    void writeTo(ByteArrayOutputStream w, int serFlags) {
        BlockChain.writeVarInt63(w, assetVersion);
        BlockChain.writeExtensibleString(w, commitmentSuffix, buf -> {
            writeInputCommitment(buf, serFlags);
            return null;
        });
        BlockChain.writeVarStr31(w, referenceData);
        if ((serFlags & Transaction.SerWitness) != 0) {
            BlockChain.writeExtensibleString(w, witnessSuffix, buf -> {
                if (assetVersion == 1) {
                    inputWitness.writeTo(buf);
                }
                return null;
            });
        }
    }

    public void writeInputCommitment(ByteArrayOutputStream w, int serFlags) {
        if (assetVersion == 1) {
            inputCommitment.writeTo(w, serFlags);
        }
    }

    public Output prevOutput() {
        AssetAmount assetAmount = assetAmount();
        TxOutput txOutput = new TxOutput(assetAmount.getAssetID(), assetAmount.getAmount(), controlProgram(), new byte[0]);
        return new Output(spentOutputID(), txOutput);
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

    public AssetAmount assetAmount() {
        return new AssetAmount();
    }

    protected byte[] controlProgram() {
        return new byte[0];
    }

    public OutputID spentOutputID() {
        return new OutputID();
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
