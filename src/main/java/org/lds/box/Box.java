package org.lds.box;

import org.lds.io.BinaryReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class Box {
    public interface ParseListener {
        void onBox(Box box) throws IOException;
    }

    protected final BinaryReader bin;
    private final long size;
    private final String type;

    public Box(InputStream in) throws IOException {
        bin = new BinaryReader(in, ByteOrder.BIG_ENDIAN);
        long size = bin.readUInt32();
        String type = bin.readString(4);
        if (size == 1) {
            size = bin.readInt64();
        } else if (size == 0) {
            size = bin.getOffset() + bin.available();
        }
        if ("uuid".equals(type)) {
            bin.skip(16);
        }
        this.size = size;
        this.type = type;
    }

    protected Box(Box box) {
        bin = box.bin;
        size = box.size;
        type = box.type;
    }

    public void skip() throws IOException {
        bin.skip(size - bin.getOffset());
    }

    public long getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public void readBoxes(ParseListener listener) throws IOException {
        while (bin.getOffset() < size) {
            readBox(listener);
        }
    }

    public void readBox(ParseListener listener) throws IOException {
        Box box = new Box(bin.getInputStream());
        listener.onBox(box);
        box.skip();
        bin.addOffset(box.size);
    }
}
