package chainj.protocol.tx;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class Header implements EntryInterface{

    public Body body = new Body();

    Body getBody() {
        return body;
    }

    Header(long version, EntryRef[] results, EntryRef data, long minTimeMS, long maxTimeMS) {
        this.body = new Body(version, results, data, minTimeMS, maxTimeMS);
    }

    class Body {
        public long version;
        public EntryRef[] results = new EntryRef[0];
        public EntryRef data = new EntryRef();
        public long minTimeMS, maxTimeMS;
        public ExtHash extHash = new ExtHash(); // 不要删除，在writeForHash的时候会写入到字节数组

        Body() {
        }

        Body(long version, EntryRef[] results, EntryRef data, long minTimeMS, long maxTimeMS) {
            this.version = version;
            this.results = results;
            this.data = data;
            this.minTimeMS = minTimeMS;
            this.maxTimeMS = maxTimeMS;
        }
    }

    @Override
    public String type() {
        return "txheader";
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
