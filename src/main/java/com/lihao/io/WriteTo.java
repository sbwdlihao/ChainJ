package com.lihao.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by sbwdlihao on 23/12/2016.
 */
public interface WriteTo {

    void writeTo(OutputStream io) throws IOException;
}
