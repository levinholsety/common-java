package org.lds.io;

import java.io.IOException;

public class ByteArrayBinaryReader extends AbstractSeekableBinaryReader {
    private final byte[] byteArray;
    private int position = 0;

    public ByteArrayBinaryReader(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    @Override
    public void read(byte[] buf, int off, int len) throws IOException {
        System.arraycopy(byteArray, position, buf, off, len);
        position += len;
    }

    @Override
    public byte read() throws IOException {
        byte b = byteArray[position];
        position++;
        return b;
    }

    @Override
    public void close() {
    }

    @Override
    public void seek(long pos) throws IOException {
        this.position = (int) pos;
    }

    @Override
    public long getPosition() throws IOException {
        return position;
    }

    @Override
    public long getLength() throws IOException {
        return byteArray.length;
    }
}
