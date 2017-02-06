package chainj.protocol.tx;

import chainj.crypto.Sha3;
import chainj.protocol.bc.Hash;

/**
 * Created by sbwdlihao on 05/02/2017.
 *
 * A "Data" entry represents some arbitrary data
 * the transaction author wants the current transaction to commit to,
 * either for use in programs in the current or future transactions,
 * or for reference by external systems.
 * This is done with a hash commitment:
 * the entry itself stores a 32-byte hash of the underlying data,
 * which may be of any length.
 * It is the responsibility of the transport layer
 * to provide the underlying data
 * alongside the actual transaction, if necessary.
 * The data need not be made available to all parties;
 * it is fine to keep it confidential.
 * Note that the body of this entry is a hash (of the underlying data)
 * when a Data entry is hashed, its body_hash is a hash of that hash.
 */

class Data implements EntryInterface {

    private Hash body;

    Hash getBody() {
        return body;
    }

    Data(byte[] data) {
        this.body = new Hash(Sha3.sum256(data));
    }

    @Override
    public String type() {
        return "data1";
    }

    @Override
    public Object body() {
        return body;
    }

    @Override
    public int ordinal() {
        return -1;
    }
}
