package chainj.protocol.tx;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class Mux implements EntryInterface{

    private Body body = new Body();

    class Body {
        public ValueSource[] sources = new ValueSource[0];

        Body() {
        }

        Body(ValueSource[] sources) {
            this.sources = sources;
        }
    }

    Mux(ValueSource[] sources) {
        this.body = new Body(sources);
    }

    @Override
    public String type() {
        return "mux1";
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
