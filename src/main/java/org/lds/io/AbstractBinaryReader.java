package org.lds.io;

import org.lds.ByteArrayUtil;
import org.lds.Encoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

public abstract class AbstractBinaryReader implements BinaryReader {

    public static final byte NULL = 0;
    private ByteOrder order = ByteOrder.nativeOrder();
    private Encoding encoding = Encoding.Default;

    @Override
    public ByteOrder getOrder() {
        return order;
    }

    @Override
    public Encoding getEncoding() {
        return encoding;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public void setOrder(ByteOrder order) {
        this.order = order;
    }

    public void read(byte[] buf) throws IOException {
        read(buf, 0, buf.length);
    }

    public byte[] readByteArray(int len) throws IOException {
        byte[] result = new byte[len];
        read(result);
        return result;
    }

    public byte[] readByteArrayUntil(byte delim) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte b = read(); b != delim; b = read()) {
            out.write(b);
        }
        return out.toByteArray();
    }

    public String readString(int len) throws IOException {
        byte[] buf = readByteArray(len);
        return encoding.decode(buf);
    }

    public String readString() throws IOException {
        byte[] buf = readByteArrayUntil(NULL);
        return encoding.decode(buf);
    }

    public short readShortValue() throws IOException {
        byte[] buf = readByteArray(2);
        return ByteArrayUtil.toShortValue(buf, order);
    }

    public int readUnsignedShortValue() throws IOException {
        byte[] buf = readByteArray(2);
        return ByteArrayUtil.toUnsignedShortValue(buf, order);
    }

    public int readIntValue() throws IOException {
        byte[] buf = readByteArray(4);
        return ByteArrayUtil.toIntValue(buf, order);
    }

    public long readUnsignedIntValue() throws IOException {
        byte[] buf = readByteArray(4);
        return ByteArrayUtil.toUnsignedIntValue(buf, order);
    }

    public long readLongValue() throws IOException {
        byte[] buf = readByteArray(8);
        return ByteArrayUtil.toLongValue(buf, order);
    }

}
