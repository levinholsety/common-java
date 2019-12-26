package org.lds.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class InputStreamBinaryReader extends AbstractBinaryReader {

    private final InputStream baseStream;

    public InputStreamBinaryReader(InputStream baseStream, ByteOrder order) {
        this.baseStream = baseStream;
        setOrder(order);
    }

    public InputStream getInputStream() {
        return baseStream;
    }

    public long available() throws IOException {
        return baseStream.available();
    }

    @Override
    public void read(byte[] buf, int off, int len) throws IOException {
        int length = baseStream.read(buf, off, len);
        if (length < len) throw new EOFException();
    }

    @Override
    public byte read() throws IOException {
        int b = baseStream.read();
        if (b < 0) throw new EOFException();
        return (byte) b;
    }

    @Override
    public void skip(long n) throws IOException {
        long length = baseStream.skip(n);
        if (length < n) throw new EOFException();
    }

    @Override
    public void close() throws IOException {
        baseStream.close();
    }
}
