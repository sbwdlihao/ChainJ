package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import chainj.io.WriteTo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * Created by sbwdlihao on 25/12/2016.
 */
public class BlockHeader implements WriteTo {

    // Version of the block.
    private long version;

    // Height of the block in the block chain.
    // Initial block has height 1.
    private long height;

    // Hash of the previous block in the block chain.
    private Hash previousBlockHash = new Hash();

    // Time of the block in milliseconds.
    // Must grow monotonically and can be equal
    // to the time in the previous block.
    private long timestampMS;

    // The next three fields constitute the block's "commitment."

    // TransactionsMerkleRoot is the root hash of the Merkle binary hash
    // tree formed by the transaction witness hashes of all transactions
    // included in the block.
    private Hash transactionsMerkleRoot = new Hash();

    // AssetsMerkleRoot is the root hash of the Merkle Patricia Tree of
    // the set of unspent outputs with asset version 1 after applying
    // the block.
    private Hash assetsMerkleRoot = new Hash();

    // ConsensusProgram is the predicate for validating the next block.
    private byte[] consensusProgram = new byte[0];

    // Witness is a vector of arguments to the previous block's
    // ConsensusProgram for validating this block.
    private byte[][] witness = new byte[0][];

    long getHeight() {
        return height;
    }

    long getTimestampMS() {
        return timestampMS;
    }

    byte[] getConsensusProgram() {
        return consensusProgram;
    }

    private void setConsensusProgram(byte[] consensusProgram) {
        Objects.requireNonNull(consensusProgram);
        this.consensusProgram = consensusProgram;
    }

    private void setWitness(byte[][] witness) {
        Objects.requireNonNull(witness);
        this.witness = witness;
    }

    BlockHeader() {}

    BlockHeader(long version, long height) {
        this.version = version;
        this.height = height;
    }

    Hash hash() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writeTo(buf);
        return new Hash(Sha3.sum256(buf.toByteArray()));
    }

    Hash hashForSig() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writeForSigTo(buf);
        return new Hash(Sha3.sum256(buf.toByteArray()));
    }

    Date time() {
        return new Date(timestampMS);
    }

    int readFrom(InputStream r) throws IOException {
        int serFlags = BCUtil.readSerFlags(r);
        switch (serFlags) {
            case Block.SerBlockSigHash:
            case Block.SerBlockHeader:
            case Block.SerBlockFull:
                break;
            default:
                throw new IOException(String.format("unsupported serialization flags %#x", serFlags));
        }
        version = BlockChain.readVarInt63(r);
        height = BlockChain.readVarInt63(r);
        previousBlockHash.readFull(r);
        timestampMS = BlockChain.readVarInt63(r);

        readCommitment(r);
        readWitness(r, serFlags);
        return serFlags;
    }

    private void readCommitment(InputStream r) throws IOException {
        byte[] commitment = BlockChain.readVarStr31(r);
        if (commitment.length < 64) {
            throw new IOException("block commitment string too short");
        }
        System.arraycopy(commitment, 0, transactionsMerkleRoot.getValue(), 0, 32);
        System.arraycopy(commitment, 32, assetsMerkleRoot.getValue(), 0, 32);

        ByteArrayInputStream programReader = new ByteArrayInputStream(commitment, 64, commitment.length);
        setConsensusProgram(BlockChain.readVarStr31(programReader));
    }

    private void readWitness(InputStream r, int serFlags) throws IOException {
        if ((serFlags & Block.SerBlockWitness) == Block.SerBlockWitness) {
            byte[] witnesses = BlockChain.readVarStr31(r);
            ByteArrayInputStream witnessReader = new ByteArrayInputStream(witnesses);
            setWitness(BCUtil.readDyadicArray(witnessReader));
        }
    }

    public void writeTo(ByteArrayOutputStream w) {
        writeTo(w, Block.SerBlockHeader);
    }

    private void writeForSigTo(ByteArrayOutputStream w) {
        writeTo(w, Block.SerBlockSigHash);
    }

    void writeTo(ByteArrayOutputStream w, int serFlags) {
        w.write(serFlags);
        BlockChain.writeVarInt63(w, version);
        BlockChain.writeVarInt63(w, height);
        w.write(previousBlockHash.getValue(), 0, previousBlockHash.getValue().length);
        BlockChain.writeVarInt63(w, timestampMS);
        writeCommitment(w);
        writeWitness(w, serFlags);
    }

    private void writeCommitment(ByteArrayOutputStream w) {
        ByteArrayOutputStream commitment = new ByteArrayOutputStream();
        commitment.write(transactionsMerkleRoot.getValue(), 0, transactionsMerkleRoot.getValue().length);
        commitment.write(assetsMerkleRoot.getValue(), 0, assetsMerkleRoot.getValue().length);
        BlockChain.writeVarStr31(commitment, consensusProgram);
        BlockChain.writeVarStr31(w, commitment.toByteArray());
    }

    private void writeWitness(ByteArrayOutputStream w, int serFlags) {
        if ((serFlags & Block.SerBlockWitness) == Block.SerBlockWitness) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            BCUtil.writeDyadicArray(buf, witness);
            BlockChain.writeVarStr31(w, buf.toByteArray());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockHeader that = (BlockHeader) o;

        return version == that.version &&
                height == that.height &&
                timestampMS == that.timestampMS &&
                (previousBlockHash != null ? previousBlockHash.equals(that.previousBlockHash) : that.previousBlockHash == null) &&
                (transactionsMerkleRoot != null ? transactionsMerkleRoot.equals(that.transactionsMerkleRoot) : that.transactionsMerkleRoot == null) &&
                (assetsMerkleRoot != null ? assetsMerkleRoot.equals(that.assetsMerkleRoot) : that.assetsMerkleRoot == null) &&
                Arrays.equals(consensusProgram, that.consensusProgram) &&
                Arrays.deepEquals(witness, that.witness);
    }

    @Override
    public int hashCode() {
        int result = (int) (version ^ (version >>> 32));
        result = 31 * result + (int) (height ^ (height >>> 32));
        result = 31 * result + (previousBlockHash != null ? previousBlockHash.hashCode() : 0);
        result = 31 * result + (int) (timestampMS ^ (timestampMS >>> 32));
        result = 31 * result + (transactionsMerkleRoot != null ? transactionsMerkleRoot.hashCode() : 0);
        result = 31 * result + (assetsMerkleRoot != null ? assetsMerkleRoot.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(consensusProgram);
        result = 31 * result + Arrays.deepHashCode(witness);
        return result;
    }
}
