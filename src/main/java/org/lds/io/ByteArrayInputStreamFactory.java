package org.lds.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayInputStreamFactory extends InputStreamFactory {
    private final byte[] data;
    private final int offset;
    private final int length;

    public ByteArrayInputStreamFactory(byte[] data) {
        this(data, 0, data.length);
    }

    public ByteArrayInputStreamFactory(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public InputStream newInputStream() throws IOException {
        return new ByteArrayInputStream(data, offset, length);
    }
}
