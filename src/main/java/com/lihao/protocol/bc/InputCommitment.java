package com.lihao.protocol.bc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 24/12/2016.
 */
public interface InputCommitment {

    int readFrom(InputStream r, long txVersion) throws IOException;

    void writeTo(OutputStream w) throws IOException;
}
