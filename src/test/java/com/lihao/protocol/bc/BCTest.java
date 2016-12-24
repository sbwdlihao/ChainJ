package com.lihao.protocol.bc;

import com.lihao.io.WriteTo;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by sbwdlihao on 23/12/2016.
 */
public class BCTest {

    static byte[] serialize(WriteTo wo) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        wo.writeTo(buf);
        return buf.toByteArray();
    }

    static Hash mustDecodeHash(String hex) {
        return new Hash(Hex.decode(hex));
    }
}
