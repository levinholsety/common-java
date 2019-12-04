package org.lds.exif;


import org.lds.io.BinaryReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class Field {

    public enum Type {
        BYTE(1, 1),
        ASCII(2, 1),
        SHORT(3, 2),
        LONG(4, 4),
        RATIONAL(5, 8),
        UNDEFINED(7, 1),
        SLONG(9, 4),
        SRATIONAL(10, 8);

        public static Type parse(short value) {
            for (Type el : Type.values()) {
                if (el.value == value) return el;
            }
            return null;
        }

        private short value;
        private int length;

        Type(int value, int length) {
            this.value = (short) value;
            this.length = length;
        }

        public int getLength() {
            return length;
        }
    }

    public static final int TagCompression = 0x103;
    public static final int TagMake = 0x10f;
    public static final int TagModel = 0x110;
    public static final int TagOrientation = 0x112;
    public static final int TagSoftware = 0x113;
    public static final int TagDateTime = 0x132;
    public static final int TagArtist = 0x13b;
    public static final int TagJPEGInterchangeFormat = 0x201;
    public static final int TagJPEGInterchangeFormatLength = 0x202;
    public static final int TagExifIFDPointer = 0x8769;
    public static final int TagGPSInfoIFDPointer = 0x8825;
    public static final int TagExposureTime = 0x829a;
    public static final int TagFNumber = 0x829d;
    public static final int TagISOSpeedRatings = 0x8827;
    public static final int TagFocalLength = 0x920a;
    public static final int TagFocalLengthIn35mmFilm = 0xa405;
    public static final int TagMakerNote = 0x927c;

    public static Field parse(InputStream in, ByteOrder byteOrder) throws IOException {
        BinaryReader bin = new BinaryReader(in, byteOrder);
        int tag = bin.readUInt16();
        short typeValue = bin.readInt16();
        int count = bin.readInt32();
        byte[] valueOffset = bin.readByteArray(4);
        Type type = Type.parse(typeValue);
        if (type == null) return null;
        if (count < 0) return null;
        return new Field(tag, type, count, valueOffset);
    }

    private final int tag;
    private final Type type;
    private final int count;
    private final byte[] valueOffset;

    private Field(int tag, Type type, int count, byte[] valueOffset) {
        this.tag = tag;
        this.type = type;
        this.count = count;
        this.valueOffset = valueOffset;
    }

    public int getTag() {
        return tag;
    }

    public Type getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public byte[] getValueOffset() {
        return valueOffset;
    }

}
