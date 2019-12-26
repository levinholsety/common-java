package org.lds.tiff;

import org.lds.io.AbstractSeekableBinaryReader;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class TIFF {

    public static TIFF parse(AbstractSeekableBinaryReader reader, long offset) throws IOException {
        reader.seek(offset);
        ByteOrder order;
        switch (reader.readUnsignedShortValue()) {
            case 0x4949:
                order = ByteOrder.LITTLE_ENDIAN;
                break;
            case 0x4d4d:
                order = ByteOrder.BIG_ENDIAN;
                break;
            default:
                return null;
        }
        reader.setOrder(order);
        if (reader.readUnsignedShortValue() != 0x2a) {
            return null;
        }
        long offsetOfIFD = reader.readUnsignedIntValue();
        return new TIFF(reader, offset, order, offsetOfIFD);
    }

    private final AbstractSeekableBinaryReader reader;
    private final long offset;
    private final ByteOrder order;
    private final long offsetOfIFD;

    private TIFF(AbstractSeekableBinaryReader reader, long offset, ByteOrder order, long offsetOfIFD) {
        this.reader = reader;
        this.offset = offset;
        this.order = order;
        this.offsetOfIFD = offsetOfIFD;
    }

    public AbstractSeekableBinaryReader getReader() {
        return reader;
    }

    public long getOffset() {
        return offset;
    }

    public ByteOrder getOrder() {
        return order;
    }

    public long getOffsetOfIFD() {
        return offsetOfIFD;
    }

    public IFD readIFD(long offset) throws IOException {
        reader.seek(this.offset + offset);
        int entryCount = reader.readUnsignedShortValue();
        Map<Integer, DE> entries = new HashMap<Integer, DE>(entryCount);
        for (int i = 0; i < entryCount; i++) {
            int tag = reader.readUnsignedShortValue();
            int type = reader.readUnsignedShortValue();
            int count = reader.readIntValue();
            if (count < 0) {
                continue;
            }
            byte[] valueOffset = reader.readByteArray(4);
            entries.put(tag, new DE(tag, type, count, valueOffset));
        }
        long offsetOfNextIFD = reader.readUnsignedIntValue();
        return new IFD(this, entryCount, entries, offsetOfNextIFD);
    }

}
