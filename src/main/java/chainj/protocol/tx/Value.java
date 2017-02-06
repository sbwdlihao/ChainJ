package chainj.protocol.tx;


/**
 * Created by sbwdlihao on 05/02/2017.
 */
class ValueSource {
    public EntryRef ref = new EntryRef();
    public AssetAmount value = new AssetAmount();
    public long position;

    ValueSource() {
    }

    ValueSource(EntryRef ref, AssetAmount value) {
        this.ref = ref;
        this.value = value;
    }

    ValueSource(EntryRef ref, AssetAmount value, long position) {
        this.ref = ref;
        this.value = value;
        this.position = position;
    }
}

class ValueDestination {
    public EntryRef ref = new EntryRef();
    public long position;
}
