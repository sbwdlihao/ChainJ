package chainj;

/**
 * Created by sbwdlihao on 03/01/2017.
 */
public class Case<D, W> {
    public D data;
    public W want;

    public Case(D data, W want) {
        this.data = data;
        this.want = want;
    }
}
