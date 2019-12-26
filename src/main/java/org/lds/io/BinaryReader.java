package org.lds.io;

import org.lds.Encoding;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteOrder;

public interface BinaryReader extends Closeable {
    ByteOrder getOrder();

    Encoding getEncoding();

    void read(byte[] buf, int off, int len) throws IOException;

    byte read() throws IOException;

    void skip(long n) throws IOException;

}
