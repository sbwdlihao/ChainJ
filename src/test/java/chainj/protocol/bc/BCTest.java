package chainj.protocol.bc;

import chainj.io.WriteTo;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;

/**
 * Created by sbwdlihao on 23/12/2016.
 */
class BCTest {

    static byte[] serialize(WriteTo wo) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        wo.writeTo(buf);
        return buf.toByteArray();
    }

    static Hash mustDecodeHash(String hex) {
        return new Hash(Hex.decode(hex));
    }
}
