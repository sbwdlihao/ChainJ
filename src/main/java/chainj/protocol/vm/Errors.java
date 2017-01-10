package chainj.protocol.vm;

/**
 * Created by sbwdlihao on 02/01/2017.
 */
public class Errors {

    public static final String ErrAltStackUnderflow = "alt stack underflow";
    public static final String ErrBadValue = "bad value";
    public static final String ErrContext = "wrong context";
    public static final String ErrDataStackUnderflow = "data stack underflow";
    public static final String ErrDisallowedOpcode = "disallowed opcode";
    public static final String ErrDivZero = "division by zero";
    public static final String ErrLongProgram = "program size exceeds maxint32";
    public static final String ErrRange = "range error";
    public static final String ErrReturn = "RETURN executed";
    public static final String ErrRunLimitExceeded = "run limit exceeded";
    public static final String ErrShortProgram = "unexpected end of program";
    public static final String ErrToken = "unrecognized token";
    public static final String ErrUnexpected = "unexpected error";
    public static final String ErrUnsupportedTx = "unsupported transaction type";
    public static final String ErrUnsupportedVM = "unsupported VM";
    public static final String ErrVerifyFailed = "VERIFY failed";
    public static final String ErrMultiSigFormat = "bad multi sig program format";
}
