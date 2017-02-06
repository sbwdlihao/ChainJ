package chainj.protocol.tx;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class Output implements EntryInterface{

    private Body body = new Body();
    private int ordinal;

    Body getBody() {
        return body;
    }

    class Body {
        public ValueSource valueSource = new ValueSource();
        public Program controlProgram = new Program();
        public EntryRef data = new EntryRef();
        public ExtHash extHash = new ExtHash(); // 不要删除，在writeForHash的时候会写入到字节数组

        Body() {
        }

        Body(ValueSource valueSource, Program controlProgram, EntryRef data) {
            this.valueSource = valueSource;
            this.controlProgram = controlProgram;
            this.data = data;
        }
    }

    Output(ValueSource valueSource, Program controlProgram, EntryRef data, int ordinal) {
        this.body = new Body(valueSource, controlProgram, data);
        this.ordinal = ordinal;
    }

    @Override
    public String type() {
        return "output1";
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
