package chainj.protocol.vm;

/**
 * Created by sbwdlihao on 04/01/2017.
 */
public class VMRunTimeException extends RuntimeException{

    public VMRunTimeException(String message) {
        super(message);
    }

    public VMRunTimeException(Throwable cause) {
        super(cause);
    }
}
