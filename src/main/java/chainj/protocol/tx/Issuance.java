package chainj.protocol.tx;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class Issuance implements EntryInterface{

    private Body body = new Body();
    private int ordinal;

    Body getBody() {
        return body;
    }

    class Body {
        public EntryRef anchor = new EntryRef();
        public AssetAmount value = new AssetAmount();
        public EntryRef data = new EntryRef();
        public ExtHash extHash = new ExtHash(); // 不要删除，在writeForHash的时候会写入到字节数组

        Body() {
        }

        Body(EntryRef anchor, AssetAmount value, EntryRef data) {
            this.anchor = anchor;
            this.value = value;
            this.data = data;
        }
    }

    Issuance(EntryRef anchor, AssetAmount value, EntryRef data, int ordinal) {
        this.body = new Body(anchor, value, data);
        this.ordinal = ordinal;
    }

    @Override
    public String type() {
        return "issuance1";
    }

    @Override
    public Object body() {
        return body;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }
}
