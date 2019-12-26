package org.lds.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileBinaryReader extends AbstractSeekableBinaryReader {

    private static final String MODE = "r";
    private final RandomAccessFile raf;

    public FileBinaryReader(String name) throws IOException {
        raf = new RandomAccessFile(name, MODE);
    }

    public FileBinaryReader(File file) throws IOException {
        raf = new RandomAccessFile(file, MODE);
    }

    @Override
    public void read(byte[] buf, int off, int len) throws IOException {
        raf.readFully(buf, off, len);
    }

    @Override
    public byte read() throws IOException {
        return raf.readByte();
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

    @Override
    public void seek(long pos) throws IOException {
        raf.seek(pos);
    }

    @Override
    public long getPosition() throws IOException {
        return raf.getFilePointer();
    }

    @Override
    public long getLength() throws IOException {
        return raf.length();
    }
}
