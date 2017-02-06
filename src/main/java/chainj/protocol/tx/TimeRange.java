package chainj.protocol.tx;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class TimeRange implements EntryInterface{

    private Body body = new Body();

    Body getBody() {
        return body;
    }

    class Body {
        public long minTimeMS, maxTimeMS;
        public ExtHash extHash = new ExtHash(); // 不要删除，在writeForHash的时候会写入到字节数组

        Body() {
        }

        Body(long minTimeMS, long maxTimeMS) {
            this.minTimeMS = minTimeMS;
            this.maxTimeMS = maxTimeMS;
        }
    }

    TimeRange(long minTimeMS, long maxTimeMS) {
        this.body = new Body(minTimeMS, maxTimeMS);
    }

    @Override
    public String type() {
        return "timerange1";
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
