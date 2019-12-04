package org.lds.exif;

import org.lds.ByteArrayUtil;
import org.lds.io.BinaryReader;
import org.lds.io.InputStreamListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.HashMap;

public class IFD {

    public static IFD getNextIFD(final EXIF exif, final long offset) throws IOException {
        if (offset == 0) return null;
        return exif.factory.openRead(new InputStreamListener<IFD>() {
            @Override
            public IFD onOpen(InputStream in) throws IOException {
                BinaryReader bin = new BinaryReader(in, exif.tiffHeader.getByteOrder());
                bin.skip(exif.tiffHeader.getOffset());
                bin.skip(offset);
                int fieldCount = bin.readUInt16();
                HashMap<Integer, Field> map = new HashMap<Integer, Field>();
                for (int i = 0; i < fieldCount; i++) {
                    Field field = Field.parse(in, exif.tiffHeader.getByteOrder());
                    if (field == null) continue;
                    map.put(field.getTag(), field);
                }
                long offsetOfNextIFD = bin.readUInt32();
                return new IFD(exif, fieldCount, map, offsetOfNextIFD);
            }
        });
    }

    final EXIF exif;
    private final int fieldCount;
    private final HashMap<Integer, Field> fieldMap;
    private final long offsetOfNextIFD;

    public IFD(EXIF exif, int fieldCount, HashMap<Integer, Field> fieldMap, long offsetOfNextIFD) {
        this.exif = exif;
        this.fieldCount = fieldCount;
        this.fieldMap = fieldMap;
        this.offsetOfNextIFD = offsetOfNextIFD;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public long getOffsetOfNextIFD() {
        return offsetOfNextIFD;
    }

    public Field getField(int tag) {
        return fieldMap.get(tag);
    }

    public Object getValue(int tag) {
        Object obj = getValues(tag);
        if (obj == null) return null;
        if (obj.getClass().isArray() && Array.getLength(obj) > 0) return Array.get(obj, 0);
        return obj;
    }

    public Object getValues(int tag) {
        final Field field = getField(tag);
        if (field == null) return null;
        int length = field.getType().getLength() * field.getCount();
        if (length <= 4) {
            BinaryReader bin = new BinaryReader(new ByteArrayInputStream(field.getValueOffset(), 0, length), exif.tiffHeader.getByteOrder());
            try {
                return getValues(bin, field);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            final long offset = ByteArrayUtil.readInt64(field.getValueOffset(), 0, 4, exif.tiffHeader.getByteOrder());
            return exif.factory.mustOpen(new InputStreamListener<Object>() {
                @Override
                public Object onOpen(InputStream in) throws IOException {
                    BinaryReader bin = new BinaryReader(in, exif.tiffHeader.getByteOrder());
                    bin.skip(exif.tiffHeader.getOffset());
                    bin.skip(offset);
                    return getValues(bin, field);
                }
            });
        }
    }

    public Long getValueOffset(int tag) {
        Field field = getField(tag);
        if (field == null) return null;
        return ByteArrayUtil.readInt64(field.getValueOffset(), 0, 4, exif.tiffHeader.getByteOrder());
    }

    private Object getValues(BinaryReader bin, Field field) throws IOException {
        switch (field.getType()) {
            case BYTE: {
                short[] array = new short[field.getCount()];
                for (int i = 0; i < array.length; i++) {
                    array[i] = bin.readUInt8();
                }
                return array;
            }
            case ASCII:
                return bin.readString(field.getCount() - 1);
            case SHORT: {
                int[] array = new int[field.getCount()];
                for (int i = 0; i < array.length; i++) {
                    array[i] = bin.readUInt16();
                }
                return array;
            }
            case LONG: {
                long[] array = new long[field.getCount()];
                for (int i = 0; i < array.length; i++) {
                    array[i] = bin.readUInt32();
                }
                return array;
            }
            case RATIONAL: {
                long[][] array = new long[field.getCount()][];
                for (int i = 0; i < array.length; i++) {
                    array[i] = new long[]{bin.readUInt32(), bin.readUInt32()};
                }
                return array;
            }
            case UNDEFINED:
                return bin.readByteArray(field.getCount());
            case SLONG: {
                int[] array = new int[field.getCount()];
                for (int i = 0; i < array.length; i++) {
                    array[i] = bin.readInt32();
                }
                return array;
            }
            case SRATIONAL: {
                int[][] array = new int[field.getCount()][];
                for (int i = 0; i < array.length; i++) {
                    array[i] = new int[]{bin.readInt32(), bin.readInt32()};
                }
                return array;
            }
            default:
                return null;
        }
    }
}
