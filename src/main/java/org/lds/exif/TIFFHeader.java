package org.lds.exif;

import org.lds.io.BinaryReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class TIFFHeader {

    public static TIFFHeader parse(InputStream in, long offset) throws IOException {
        BinaryReader bin = new BinaryReader(in, ByteOrder.BIG_ENDIAN);
        bin.skip(offset);
        switch (bin.readUInt16()) {
            case 0x4949:
                bin.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                break;
            case 0x4d4d:
                bin.setByteOrder(ByteOrder.BIG_ENDIAN);
                break;
            default:
                return null;
        }
        if (0x2a != bin.readInt16()) return null;
        long offsetOfIFD = bin.readUInt32();
        return new TIFFHeader(offset, bin.getByteOrder(), offsetOfIFD);
    }

    private final long offset;
    private final ByteOrder byteOrder;
    private final long offsetOfIFD;

    private TIFFHeader(long offset, ByteOrder byteOrder, long offsetOfIFD) {
        this.offset = offset;
        this.byteOrder = byteOrder;
        this.offsetOfIFD = offsetOfIFD;
    }

    public long getOffset() {
        return offset;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public long getOffsetOfIFD() {
        return offsetOfIFD;
    }

}
