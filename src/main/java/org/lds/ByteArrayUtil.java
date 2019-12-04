package org.lds;

import java.math.BigInteger;
import java.nio.ByteOrder;
import java.security.SecureRandom;

public class ByteArrayUtil {

    private static final SecureRandom rand = new SecureRandom(BigInteger.valueOf(System.nanoTime()).toByteArray());

    public static boolean startsWith(byte[] array, byte[] prefix) {
        if (array.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (array[i] != prefix[i]) return false;
        }
        return true;
    }

    public static long readInt64(byte[] buf, int off, int len, ByteOrder byteOrder) {
        if (len > 8) throw new IllegalArgumentException("len");
        long value = 0;
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int i = 0; i < len; i++) {
                long b = buf[off + i];
                b &= 0xff;
                b <<= 8 * i;
                value += b;
            }
        } else {
            for (int i = 0; i < len; i++) {
                long b = buf[off + i];
                b &= 0xff;
                b <<= 8 * (len - i - 1);
                value += b;
            }
        }
        return value;
    }

    public static byte[] random(int len) {
        byte[] byteArray = new byte[len];
        rand.nextBytes(byteArray);
        return byteArray;
    }
}
