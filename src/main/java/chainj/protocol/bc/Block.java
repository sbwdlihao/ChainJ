package chainj.protocol.bc;

import chainj.encoding.blockchain.BlockChain;
import chainj.io.WriteTo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by sbwdlihao on 26/12/2016.
 */
public class Block implements WriteTo{

    static final int SerBlockWitness = 1;
    private static final int SerBlockTransactions = 2;
    static final int SerBlockSigHash = 0;
    static final int SerBlockHeader = SerBlockWitness;
    static final int SerBlockFull = SerBlockWitness | SerBlockTransactions;

    // NewBlockVersion is the version to use when creating new blocks.
    static final int NewBlockVersion = 1;

    private BlockHeader blockHeader = new BlockHeader();

    private Transaction[] transactions = new Transaction[0];

    BlockHeader getBlockHeader() {
        return blockHeader;
    }

    private void setBlockHeader(BlockHeader blockHeader) {
        Objects.requireNonNull(blockHeader);
        this.blockHeader = blockHeader;
    }

    private void setTransactions(Transaction[] transactions) {
        Objects.requireNonNull(blockHeader);
        this.transactions = transactions;
    }

    public long getHeight() {
        return blockHeader.getHeight();
    }

    public Hash getHash() {
        return blockHeader.hash();
    }

    public byte[] getConsensusProgram() {
        return blockHeader.getConsensusProgram();
    }

    public long getTimestampMS() {
        return blockHeader.getTimestampMS();
    }

    public Hash hashForSig() {
        return blockHeader.hashForSig();
    }

    public Block() {}

    Block(BlockHeader blockHeader) {
        setBlockHeader(blockHeader);
    }

    Block(BlockHeader blockHeader, Transaction[] transactions) {
        setBlockHeader(blockHeader);
        setTransactions(transactions);
    }

    void readFrom(InputStream r) throws IOException {
        int serFlags = blockHeader.readFrom(r);
        if ((serFlags & SerBlockTransactions) == SerBlockTransactions) {
            int n = BlockChain.readVarInt31(r);
            setTransactions(new Transaction[n]);
            for (int i = 0; i < n; i++) {
                TxData txData = new TxData();
                txData.readFrom(r);
                transactions[i] = new Transaction(txData);
            }
        }
    }

    public void writeTo(ByteArrayOutputStream w) {
        writeTo(w, SerBlockFull);
    }

    void writeTo(ByteArrayOutputStream w, int serFlags) {
        blockHeader.writeTo(w, serFlags);
        if ((serFlags & SerBlockTransactions) == SerBlockTransactions) {
            BlockChain.writeVarInt31(w, transactions.length);
            for (Transaction transaction : transactions) {
                transaction.writeTo(w);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;

        return (blockHeader != null ? blockHeader.equals(block.blockHeader) : block.blockHeader == null) &&
                Arrays.equals(transactions, block.transactions);
    }

    @Override
    public int hashCode() {
        int result = blockHeader != null ? blockHeader.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(transactions);
        return result;
    }
}
