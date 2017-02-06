package chainj.protocol.tx;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class Retirement implements EntryInterface{

    private Body body = new Body();
    private int ordinal;

    class Body {
        public ValueSource valueSource = new ValueSource();
        public EntryRef data = new EntryRef();
        public ExtHash extHash = new ExtHash(); // 不要删除，在writeForHash的时候会写入到字节数组

        Body() {
        }

        Body(ValueSource valueSource, EntryRef data) {
            this.valueSource = valueSource;
            this.data = data;
        }
    }

    Retirement(ValueSource valueSource, EntryRef data, int ordinal) {
        this.body = new Body(valueSource, data);
        this.ordinal = ordinal;
    }

    @Override
    public String type() {
        return "retirement1";
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
