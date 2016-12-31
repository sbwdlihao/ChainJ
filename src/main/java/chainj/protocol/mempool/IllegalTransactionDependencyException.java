package chainj.protocol.mempool;

/**
 * Created by sbwdlihao on 31/12/2016.
 */
public class IllegalTransactionDependencyException extends RuntimeException{

    public IllegalTransactionDependencyException(String message) {
        super(message);
    }
}
