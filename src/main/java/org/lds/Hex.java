package org.lds;

/**
 * Hex类。
 *
 * @author Li
 */
public final class Hex {

    /**
     * 编码十六进制字符串。
     *
     * @param byteArray 需要编码的字节数组。
     * @return 十六进制字符串。
     */
    public static String encode(byte[] byteArray) {
        if (byteArray == null) return null;
        char[] charArray = new char[byteArray.length * 2];
        for (int i = 0; i < byteArray.length; i++) {
            int b = byteArray[i] & 0xff;
            charArray[i * 2] = toChar(b >> 4);
            charArray[i * 2 + 1] = toChar(b & 0xf);
        }
        return new String(charArray);
    }

    /**
     * 解码十六进制字符串。
     *
     * @param hexString 需要解码的十六进制字符串。
     * @return 解码后的字节数组。
     */
    public static byte[] decode(String hexString) {
        if (hexString == null) return null;
        if (hexString.length() % 2 > 0) throw new IllegalArgumentException("invalid length");
        byte[] byteArray = new byte[hexString.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) (fromChar(hexString.charAt(i * 2)) << 4 | fromChar(hexString.charAt(i * 2 + 1)));
        }
        return byteArray;
    }

    private static int fromChar(char c) throws NumberFormatException {
        if (c >= 48 && c <= 57) return c - 48;// 数字字符
        if (c >= 65 && c <= 90) return c - 55;// 大写字母字符
        if (c >= 97 && c <= 122) return c - 87;// 小写字母字符
        throw new NumberFormatException(String.valueOf(c));
    }

    private static char toChar(int n) {
        if (n >= 0 && n <= 9) return (char) (n + 48);// 数字字符
        if (n >= 10 && n <= 35) return (char) (n + 87);// 小写字母字符
        throw new IllegalArgumentException(String.format("invalid number: %d", n));
    }
}
