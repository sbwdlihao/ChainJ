package chainj.protocol.tx;

/**
 * Created by sbwdlihao on 05/02/2017.
 */
class Program {

    public long vmVersion;
    public byte[] code;

    Program() {
    }

    Program(long vmVersion, byte[] code) {
        this.vmVersion = vmVersion;
        this.code = code;
    }
}
