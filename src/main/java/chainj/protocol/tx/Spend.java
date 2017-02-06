package chainj.protocol.tx;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class Spend implements EntryInterface{

    private Body body = new Body();

    private int ordinal;

    Body getBody() {
        return body;
    }

    class Body {
        public EntryRef spentOutput = new EntryRef();
        public EntryRef data = new EntryRef();
        public ExtHash extHash = new ExtHash(); // 不要删除，在writeForHash的时候会写入到字节数组

        Body() {
        }

        Body(EntryRef spentOutput, EntryRef data) {
            this.spentOutput = spentOutput;
            this.data = data;
        }
    }

    Spend(EntryRef spentOutput, EntryRef data, int ordinal) {
        body = new Body(spentOutput, data);
        this.ordinal = ordinal;
    }

    @Override
    public String type() {
        return "spend1";
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
