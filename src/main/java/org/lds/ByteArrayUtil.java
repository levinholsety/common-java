package org.lds;

import java.math.BigInteger;
import java.nio.ByteOrder;
import java.security.SecureRandom;

public class ByteArrayUtil {

    private static final SecureRandom rand = new SecureRandom(BigInteger.valueOf(System.nanoTime()).toByteArray());
    private static final int BITS_8 = 8;
    private static final int BITS_16 = 16;
    private static final int BITS_24 = 24;
    private static final int BITS_32 = 32;
    private static final int BITS_40 = 40;
    private static final int BITS_48 = 48;
    private static final int BITS_56 = 56;
    private static final int UNSIGNED_BYTE_MASK = 0xff;
    private static final long UNSIGNED_BYTE_MASK_LONG = 0xff;
    private static final int UNSIGNED_SHORT_MASK = 0xffff;
    private static final long UNSIGNED_INT_MASK = 0xffffffffL;

    public static boolean startsWith(byte[] array, byte[] prefix) {
        if (array.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (array[i] != prefix[i]) return false;
        }
        return true;
    }

    public static short toShortValue(byte[] buf, int off, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            return (short) ((buf[off] & UNSIGNED_BYTE_MASK) << BITS_8 |
                    (buf[++off] & UNSIGNED_BYTE_MASK));
        }
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return (short) ((buf[off] & UNSIGNED_BYTE_MASK) |
                    (buf[++off] & UNSIGNED_BYTE_MASK) << BITS_8);
        }
        throw new IllegalArgumentException("invalid byte order");
    }

    public static short toShortValue(byte[] buf, ByteOrder order) {
        return toShortValue(buf, 0, order);
    }

    public static int toUnsignedShortValue(byte[] buf, int off, ByteOrder order) {
        return toShortValue(buf, off, order) & UNSIGNED_SHORT_MASK;
    }

    public static int toUnsignedShortValue(byte[] buf, ByteOrder order) {
        return toUnsignedShortValue(buf, 0, order);
    }

    public static int toIntValue(byte[] buf, int off, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            return (buf[off] & UNSIGNED_BYTE_MASK) << BITS_24 |
                    (buf[++off] & UNSIGNED_BYTE_MASK) << BITS_16 |
                    (buf[++off] & UNSIGNED_BYTE_MASK) << BITS_8 |
                    (buf[++off] & UNSIGNED_BYTE_MASK);
        }
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return (buf[off] & UNSIGNED_BYTE_MASK) |
                    (buf[++off] & UNSIGNED_BYTE_MASK) << BITS_8 |
                    (buf[++off] & UNSIGNED_BYTE_MASK) << BITS_16 |
                    (buf[++off] & UNSIGNED_BYTE_MASK) << BITS_24;
        }
        throw new IllegalArgumentException("invalid byte order");
    }

    public static int toIntValue(byte[] buf, ByteOrder order) {
        return toIntValue(buf, 0, order);
    }

    public static long toUnsignedIntValue(byte[] buf, int off, ByteOrder order) {
        return toIntValue(buf, off, order) & UNSIGNED_INT_MASK;
    }

    public static long toUnsignedIntValue(byte[] buf, ByteOrder order) {
        return toUnsignedIntValue(buf, 0, order);
    }

    public static long toLongValue(byte[] buf, int off, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            return (buf[off] & UNSIGNED_BYTE_MASK_LONG) << BITS_56 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_48 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_40 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_32 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_24 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_16 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_8 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG);
        }
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return (buf[off] & UNSIGNED_BYTE_MASK_LONG) |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_8 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_16 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_24 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_32 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_40 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_48 |
                    (buf[++off] & UNSIGNED_BYTE_MASK_LONG) << BITS_56;
        }
        throw new IllegalArgumentException("invalid byte order");
    }

    public static long toLongValue(byte[] buf, ByteOrder order) {
        return toLongValue(buf, 0, order);
    }

    public static byte[] random(int len) {
        byte[] byteArray = new byte[len];
        rand.nextBytes(byteArray);
        return byteArray;
    }

}
