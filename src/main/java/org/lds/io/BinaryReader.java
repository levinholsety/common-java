package org.lds.io;

import org.lds.ByteArrayUtil;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class BinaryReader {
    private final InputStream baseStream;
    private ByteOrder byteOrder;
    private long offset;

    public BinaryReader(InputStream baseStream, ByteOrder byteOrder) {
        this.baseStream = baseStream;
        this.byteOrder = byteOrder;
        offset = 0;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public InputStream getInputStream() {
        return baseStream;
    }

    public long available() throws IOException {
        return baseStream.available();
    }

    public long getOffset() {
        return offset;
    }

    public void addOffset(long off) {
        this.offset += off;
    }

    public void read(byte[] b, int off, int len) throws IOException {
        int length = baseStream.read(b, off, len);
        if (length < len) throw new EOFException();
        addOffset(length);
    }

    public void skip(long n) throws IOException {
        long length = baseStream.skip(n);
        if (length < n) throw new EOFException();
        addOffset(length);
    }

    public String readString(int len) throws IOException {
        byte[] buf = new byte[len];
        read(buf, 0, len);
        return new String(buf);
    }

    public byte[] readByteArray(int len) throws IOException {
        byte[] buf = new byte[len];
        read(buf, 0, len);
        return buf;
    }

    public byte readInt8() throws IOException {
        int b = baseStream.read();
        if (b == -1) throw new EOFException();
        addOffset(1);
        return (byte) b;
    }

    public short readUInt8() throws IOException {
        return (short) (readInt8() & 0xff);
    }

    public short readInt16() throws IOException {
        byte[] buf = new byte[2];
        read(buf, 0, 2);
        return (short) ByteArrayUtil.readInt64(buf, 0, 2, byteOrder);
    }

    public int readUInt16() throws IOException {
        byte[] buf = new byte[2];
        read(buf, 0, 2);
        return (int) ByteArrayUtil.readInt64(buf, 0, 2, byteOrder);
    }

    public int readInt32() throws IOException {
        byte[] buf = new byte[4];
        read(buf, 0, 4);
        return (int) ByteArrayUtil.readInt64(buf, 0, 4, byteOrder);
    }

    public long readUInt32() throws IOException {
        byte[] buf = new byte[4];
        read(buf, 0, 4);
        return ByteArrayUtil.readInt64(buf, 0, 4, byteOrder);
    }

    public long readInt64() throws IOException {
        byte[] buf = new byte[8];
        read(buf, 0, 8);
        return ByteArrayUtil.readInt64(buf, 0, 8, byteOrder);
    }

    public long readInt64(int len) throws IOException {
        if (len > 8) throw new IllegalArgumentException(String.format("len: %d", len));
        byte[] buf = new byte[len];
        read(buf, 0, len);
        return ByteArrayUtil.readInt64(buf, 0, len, byteOrder);
    }

}
