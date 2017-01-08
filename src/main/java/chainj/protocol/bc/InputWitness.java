package chainj.protocol.bc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public interface InputWitness {

    void readFrom(InputStream r) throws IOException;

    void writeTo(ByteArrayOutputStream w);
}
