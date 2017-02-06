package chainj.protocol.tx;

import chainj.crypto.Sha3;
import chainj.encoding.blockchain.BlockChain;
import chainj.protocol.bc.AbstractHash;
import chainj.protocol.bc.Hash;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * Created by sbwdlihao on 05/02/2017.
 */

interface EntryInterface {

    String type();

    Object body();

    // When an entry is created from a bc.TxInput or a bc.TxOutput, this
    // reports the position of that antecedent object within its
    // transaction. Both inputs (spends and issuances) and outputs
    // (including retirements) are numbered beginning at zero. Entries
    // not originating in this way report -1.
    int ordinal();
}

class EntryRef  extends Hash {
    EntryRef() {}

    EntryRef(byte... bytes) {
        super(bytes);
    }

    EntryRef(Hash hash) {
        value = hash.getValue();
    }

    Hash hash() {
        return new Hash(value);
    }
}

class ExtHash extends Hash {}

class Entry {

    private static final String errInvalidValue = "invalid value";

    static EntryRef entryID(EntryInterface e) {
        ByteArrayOutputStream h = new ByteArrayOutputStream();
        byte[] id = "entryid:".getBytes();
        byte[] type = e.type().getBytes();
        byte[] colon = ":".getBytes();
        h.write(id, 0, id.length);
        h.write(type, 0, type.length);
        h.write(colon, 0, colon.length);
        ByteArrayOutputStream bh = new ByteArrayOutputStream();
        writeForHash(bh, e.body());
        byte[] hash = Sha3.sum256(bh.toByteArray());
        h.write(hash, 0, hash.length);
        return new EntryRef(Sha3.sum256(h.toByteArray()));
    }

    static void writeForHash(ByteArrayOutputStream w, Object c) {
        if (c instanceof Byte) {
            w.write(((Byte) c).intValue());
        } else if (c instanceof Long) {
            BlockChain.writeVarInt63(w, (Long) c);
        } else if (c.getClass() == byte[].class) {
            BlockChain.writeVarStr31(w, (byte[]) c);
        } else if (c instanceof String) {
            BlockChain.writeVarStr31(w, ((String) c).getBytes());
        } else if (c instanceof AbstractHash) {
            ((AbstractHash) c).write(w);
        } else if (c.getClass().isArray()) {
            int l = Array.getLength(c);
            BlockChain.writeVarInt31(w, l);
            for (int i = 0; i < l; i++) {
                writeForHash(w, Array.get(c, i));
            }
        } else {
            for (Field field : c.getClass().getFields()) {
                try {
                    writeForHash(w, field.get(c));
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(errInvalidValue);
                }
            }
        }
    }
}


