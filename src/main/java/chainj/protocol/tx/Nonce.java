package chainj.protocol.tx;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class Nonce implements EntryInterface{

    private Body body = new Body();

    Body getBody() {
        return body;
    }

    class Body {
        public Program program = new Program();
        public EntryRef timeRange = new EntryRef();
        public ExtHash extHash = new ExtHash(); // 不要删除，在writeForHash的时候会写入到字节数组

        Body() {
        }

        Body(Program program, EntryRef timeRange) {
            this.program = program;
            this.timeRange = timeRange;
        }
    }

    Nonce(Program program, EntryRef timeRange) {
        this.body = new Body(program, timeRange);
    }

    @Override
    public String type() {
        return "nonce1";
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
