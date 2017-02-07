package chainj.protocol.bc;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sbwdlihao on 25/12/2016.
 */
class BCUtil {

    static int readSerFlags(InputStream r) throws IOException {
        int serFlags = r.read();
        if (serFlags == -1) {
            throw new IOException("read serFlags null");
        }
        return serFlags;
    }
}
