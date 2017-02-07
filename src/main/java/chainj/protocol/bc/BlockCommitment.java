package chainj.protocol.bc;

import chainj.encoding.blockchain.BlockChain;
import chainj.util.function.ExceptionFunction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by sbwdlihao on 04/02/2017.
 */
class BlockCommitment {
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

    byte[] getConsensusProgram() {
        return consensusProgram;
    }

    void setConsensusProgram(byte[] consensusProgram) {
        Objects.requireNonNull(consensusProgram);
        this.consensusProgram = consensusProgram;
    }

    void readFrom(InputStream r) throws IOException {
        transactionsMerkleRoot.readFull(r);
        assetsMerkleRoot.readFull(r);
        setConsensusProgram(BlockChain.readVarStr31(r));
    }

    void writeTo(ByteArrayOutputStream w) {
        transactionsMerkleRoot.write(w);
        assetsMerkleRoot.write(w);
        BlockChain.writeVarStr31(w, consensusProgram);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockCommitment that = (BlockCommitment) o;

        if (transactionsMerkleRoot != null ? !transactionsMerkleRoot.equals(that.transactionsMerkleRoot) : that.transactionsMerkleRoot != null)
            return false;
        if (assetsMerkleRoot != null ? !assetsMerkleRoot.equals(that.assetsMerkleRoot) : that.assetsMerkleRoot != null)
            return false;
        return Arrays.equals(consensusProgram, that.consensusProgram);
    }

    @Override
    public int hashCode() {
        int result = transactionsMerkleRoot != null ? transactionsMerkleRoot.hashCode() : 0;
        result = 31 * result + (assetsMerkleRoot != null ? assetsMerkleRoot.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(consensusProgram);
        return result;
    }
}
