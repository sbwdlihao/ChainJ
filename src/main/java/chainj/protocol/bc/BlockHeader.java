package chainj.protocol.bc;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import chainj.io.WriteTo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

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

    private BlockCommitment blockCommitment = new BlockCommitment();
    private byte[] commitmentSuffix = new byte[0];

    private BlockWitness blockWitness = new BlockWitness();
    private byte[] witnessSuffix = new byte[0];

    long getVersion() {
        return version;
    }

    long getHeight() {
        return height;
    }

    long getTimestampMS() {
        return timestampMS;
    }

    public byte[] getConsensusProgram() {
        return blockCommitment.getConsensusProgram();
    }

    byte[][] getWitness() {
        return blockWitness.getWitness();
    }

    BlockHeader() {}

    public BlockHeader(byte[] consensusProgram) {
        blockCommitment.setConsensusProgram(consensusProgram);
    }

    public BlockHeader(long timestampMS) {
        this.timestampMS = timestampMS;
    }

    public BlockHeader(byte[][] witness) {
        blockWitness.setWitness(witness);
    }

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

        commitmentSuffix = BlockChain.readExtensibleString(r, buf -> blockCommitment.readFrom(buf));
        if ((serFlags & Block.SerBlockWitness) == Block.SerBlockWitness) {
            witnessSuffix = BlockChain.readExtensibleString(r, buf -> blockWitness.readFrom(buf));
        }
        return serFlags;
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
        previousBlockHash.write(w);
        BlockChain.writeVarInt63(w, timestampMS);
        BlockChain.writeExtensibleString(w, commitmentSuffix, buf -> {
            blockCommitment.writeTo(buf);
            return null;
        });
        if ((serFlags & Block.SerBlockWitness) == Block.SerBlockWitness) {
            BlockChain.writeExtensibleString(w, witnessSuffix, buf -> {
                blockWitness.writeTo(buf);
                return null;
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockHeader that = (BlockHeader) o;

        if (version != that.version) return false;
        if (height != that.height) return false;
        if (timestampMS != that.timestampMS) return false;
        if (previousBlockHash != null ? !previousBlockHash.equals(that.previousBlockHash) : that.previousBlockHash != null)
            return false;
        if (blockCommitment != null ? !blockCommitment.equals(that.blockCommitment) : that.blockCommitment != null)
            return false;
        if (!Arrays.equals(commitmentSuffix, that.commitmentSuffix)) return false;
        if (blockWitness != null ? !blockWitness.equals(that.blockWitness) : that.blockWitness != null) return false;
        return Arrays.equals(witnessSuffix, that.witnessSuffix);
    }

    @Override
    public int hashCode() {
        int result = (int) (version ^ (version >>> 32));
        result = 31 * result + (int) (height ^ (height >>> 32));
        result = 31 * result + (previousBlockHash != null ? previousBlockHash.hashCode() : 0);
        result = 31 * result + (int) (timestampMS ^ (timestampMS >>> 32));
        result = 31 * result + (blockCommitment != null ? blockCommitment.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(commitmentSuffix);
        result = 31 * result + (blockWitness != null ? blockWitness.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(witnessSuffix);
        return result;
    }
}
