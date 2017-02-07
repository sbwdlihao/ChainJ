package chainj.protocol.bc;

import chainj.io.WriteTo;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;

/**
 * Created by sbwdlihao on 23/12/2016.
 */
public class BCTest {

    static byte[] serialize(WriteTo wo) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        wo.writeTo(buf);
        return buf.toByteArray();
    }

    public static Hash mustDecodeHash(String hex) {
        return new Hash(Hex.decode(hex));
    }
}
