package org.lds.exif;

import org.lds.box.*;
import org.lds.io.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class EXIF {
    private static final String IFD0 = "IFD0";
    private static final String IFD1 = "IFD1";
    private static final String ExifIFD = "ExifIFD";
    private static final String GPSInfoIFD = "GPSInfoIFD";

    public static EXIF parse(File file) throws IOException {
        return parse(new FileInputStreamFactory(file));
    }

    public static EXIF parse(String filename) throws IOException {
        return parse(new FileInputStreamFactory(filename));
    }

    public static EXIF parse(byte[] data) throws IOException {
        return parse(new ByteArrayInputStreamFactory(data));
    }

    public static EXIF parse(byte[] data, int offset, int length) throws IOException {
        return parse(new ByteArrayInputStreamFactory(data, offset, length));
    }

    private static EXIF parse(final InputStreamFactory factory) throws IOException {
        return factory.openRead(new InputStreamListener<EXIF>() {
            @Override
            public EXIF onOpen(InputStream in) throws IOException {
                BinaryReader bin = new BinaryReader(in, ByteOrder.BIG_ENDIAN);
                int value = bin.readUInt16();
                if (value == 0xffd8) {
                    while (true) {
                        if (0xff != bin.readUInt8()) return null;
                        if (0xe1 == bin.readUInt8()) {
                            int length = bin.readUInt16();
                            if ("Exif".equals(bin.readString(4)) && 0 == bin.readUInt16()) {
                                byte[] exifData = new byte[length - 8];
                                bin.read(exifData, 0, exifData.length);
                                ByteArrayInputStreamFactory factory2 = new ByteArrayInputStreamFactory(exifData);
                                TIFFHeader tiffHeader = parseTIFFHeader(factory2, 0);
                                if (tiffHeader == null) return null;
                                return new EXIF(factory2, tiffHeader);
                            }
                        } else {
                            int length = bin.readUInt16();
                            bin.skip(length - 2);
                        }
                    }
                } else if (value == 0x4949 || value == 0x4d4d) {
                    TIFFHeader tiffHeader = parseTIFFHeader(factory, 0);
                    if (tiffHeader == null) return null;
                    return new EXIF(factory, tiffHeader);
                } else {
                    bin.skip(2);
                    if ("ftypheic".equals(bin.readString(8))) {
                        return parseFromHEIC(factory);
                    }
                }
                return null;
            }
        });
    }

    private static TIFFHeader parseTIFFHeader(InputStreamFactory factory, final long offset) throws IOException {
        return factory.openRead(new InputStreamListener<TIFFHeader>() {
            @Override
            public TIFFHeader onOpen(InputStream in) throws IOException {
                return TIFFHeader.parse(in, offset);
            }
        });
    }

    private static EXIF parseFromHEIC(final InputStreamFactory factory) throws IOException {
        final ItemLocationBox.Extent[] extents = new ItemLocationBox.Extent[1];
        factory.openRead(new InputStreamListener<Object>() {
            @Override
            public Object onOpen(InputStream in) throws IOException {
                final int[] itemIds = new int[1];
                new Box(in).skip();
                new FullBox(new Box(in)).readBoxes(new Box.ParseListener() {
                    @Override
                    public void onBox(Box metaC) throws IOException {
                        if ("iinf".equals(metaC.getType())) {
                            ItemInfoBox iinf = new ItemInfoBox(metaC);
                            for (int i = 0; i < iinf.getEntryCount(); i++) {
                                iinf.readBox(new Box.ParseListener() {
                                    @Override
                                    public void onBox(Box iinfC) throws IOException {
                                        ItemInfoEntry infe = new ItemInfoEntry(iinfC);
                                        if ("Exif".equals(infe.getItemType())) {
                                            itemIds[0] = infe.getItemId();
                                        }
                                    }
                                });
                            }
                        } else if ("iloc".equals(metaC.getType())) {
                            ItemLocationBox iloc = new ItemLocationBox(metaC);
                            ItemLocationBox.Item item = iloc.getItemById(itemIds[0]);
                            if (item != null && item.getExtentCount() > 0) {
                                extents[0] = item.getExtent(0);
                            }
                        }
                    }
                });
                return null;
            }
        });
        return factory.openRead(new InputStreamListener<EXIF>() {
            @Override
            public EXIF onOpen(InputStream in) throws IOException {
                BinaryReader bin = new BinaryReader(in, ByteOrder.BIG_ENDIAN);
                ItemLocationBox.Extent extent = extents[0];
                bin.skip(extent.getExtentOffset() + 10);
                byte[] exifData = new byte[(int) extent.getExtentLength()];
                bin.read(exifData, 0, exifData.length);
                ByteArrayInputStreamFactory factory2 = new ByteArrayInputStreamFactory(exifData);
                TIFFHeader tiffHeader = parseTIFFHeader(factory2, 0);
                if (tiffHeader == null) return null;
                return new EXIF(factory2, tiffHeader);
            }
        });
    }

    final InputStreamFactory factory;
    final TIFFHeader tiffHeader;
    private final Map<String, IFD> ifdMap = new HashMap<String, IFD>();

    private EXIF(InputStreamFactory factory, TIFFHeader tiffHeader) {
        this.factory = factory;
        this.tiffHeader = tiffHeader;
    }

    public Integer getOrientation() {
        return (Integer) getValue(getIFD0(), Field.TagOrientation);
    }

    public String getMake() {
        return (String) getValue(getIFD0(), Field.TagMake);
    }

    public String getModel() {
        return (String) getValue(getIFD0(), Field.TagModel);
    }

    public String getSoftware() {
        return (String) getValue(getIFD0(), Field.TagSoftware);
    }

    public String getArtist() {
        return (String) getValue(getIFD0(), Field.TagArtist);
    }

    public String getDateTime() {
        return (String) getValue(getIFD0(), Field.TagDateTime);
    }

    public Long getExifIFDPointer() {
        return (Long) getValue(getIFD0(), Field.TagExifIFDPointer);
    }

    public Integer getCompression() {
        return (Integer) getValue(getIFD1(), Field.TagCompression);
    }

    public Long getJPEGInterchangeFormat() {
        return (Long) getValue(getIFD1(), Field.TagJPEGInterchangeFormat);
    }

    public Long getJPEGInterchangeFormatLength() {
        return (Long) getValue(getIFD1(), Field.TagJPEGInterchangeFormatLength);
    }

    public String getExposureTime() {
        Object value = getValue(getExifIFD(), Field.TagExposureTime);
        if (value == null) {
            return null;
        } else if (value.getClass().isArray()) {
            long[] values = (long[]) value;
            if (values[0] < values[1]) return String.format("1/%ds", values[1] / values[0]);
            return String.format("%.2fs", (double) values[0] / values[1]);
        } else {
            return String.format("%ss", value);
        }
    }

    public Double getFNumber() {
        long[] value = (long[]) getValue(getExifIFD(), Field.TagFNumber);
        if (value == null) return null;
        return (double) value[0] / value[1];
    }

    public int[] getISOSpeedRatings() {
        return (int[]) getValues(getExifIFD(), Field.TagISOSpeedRatings);
    }

    public Double getFocalLength() {
        long[] value = (long[]) getValue(getExifIFD(), Field.TagFocalLength);
        if (value == null) return null;
        return (double) value[0] / value[1];
    }

    public Integer getFocalLengthIn35mmFilm() {
        return (Integer) getValue(getExifIFD(), Field.TagFocalLengthIn35mmFilm);
    }

    public byte[] getMakerNote() {
        return (byte[]) getValues(getExifIFD(), Field.TagMakerNote);
    }

    public IFD getIFD(final long offset) {
        try {
            return IFD.getNextIFD(this, offset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public IFD getIFD0() {
        if (!ifdMap.containsKey(IFD0)) {
            synchronized (ifdMap) {
                if (!ifdMap.containsKey(IFD0)) {
                    ifdMap.put(IFD0, getIFD(tiffHeader.getOffsetOfIFD()));
                }
            }
        }
        return ifdMap.get(IFD0);
    }

    public IFD getIFD1() {
        if (!ifdMap.containsKey(IFD1)) {
            synchronized (ifdMap) {
                if (!ifdMap.containsKey(IFD1)) {
                    IFD ifd0 = getIFD0();
                    if (ifd0 == null) {
                        ifdMap.put(IFD1, null);
                    } else {
                        ifdMap.put(IFD1, getIFD(ifd0.getOffsetOfNextIFD()));
                    }
                }
            }
        }
        return ifdMap.get(IFD1);
    }

    public IFD getExifIFD() {
        if (!ifdMap.containsKey(ExifIFD)) {
            synchronized (ifdMap) {
                if (!ifdMap.containsKey(ExifIFD)) {
                    Long pointer = getExifIFDPointer();
                    if (pointer == null) {
                        ifdMap.put(ExifIFD, null);
                    } else {
                        ifdMap.put(ExifIFD, getIFD(pointer));
                    }
                }
            }
        }
        return ifdMap.get(ExifIFD);
    }

    public IFD getGPSInfoIFD() {
        if (!ifdMap.containsKey(GPSInfoIFD)) {
            synchronized (ifdMap) {
                if (!ifdMap.containsKey(GPSInfoIFD)) {
                    Long pointer = (Long) getValue(getIFD0(), Field.TagGPSInfoIFDPointer);
                    if (pointer == null) {
                        ifdMap.put(GPSInfoIFD, null);
                    } else {
                        ifdMap.put(GPSInfoIFD, getIFD(pointer));
                    }
                }
            }
        }
        return ifdMap.get(GPSInfoIFD);
    }

    private Object getValues(IFD ifd, int tag) {
        if (ifd == null) return null;
        return ifd.getValues(tag);
    }

    private Object getValue(IFD ifd, int tag) {
        if (ifd == null) return null;
        return ifd.getValue(tag);
    }

}
