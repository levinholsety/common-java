package org.lds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Base64类。
 *
 * @author Li
 */
public final class Base64 {

    private static final String BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final String BASE64_URL_SAFE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
    private static final char PADDING = '=';
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final int B00000011 = Integer.parseInt("11", 2);
    private static final int B00001111 = Integer.parseInt("1111", 2);
    private static final int B00111111 = Integer.parseInt("111111", 2);

    public static final Base64 STANDARD = new Base64(BASE64, 0);
    public static final Base64 URL_SAFE = new Base64(BASE64_URL_SAFE, 0);
    public static final Base64 MIME76 = new Base64(BASE64, 76);
    public static final Base64 MIME64 = new Base64(BASE64, 64);

    /**
     * 解码Base64字符串。
     *
     * @param base64String Base64字符串。
     * @return 解码后的数据字节数组。
     */
    public static byte[] decode(String base64String) {
        if (base64String == null) {
            return null;
        }
        if (base64String.length() == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(base64String.length() / 4 * 3);
        StringBuilder sb = new StringBuilder(4);
        byte[] buf = new byte[3];
        for (int i = 0; i < base64String.length(); i++) {
            char c = base64String.charAt(i);
            if (c == '\n' || c == '\r') {
                continue;
            }
            sb.append(c);
            if (sb.length() == 4) {
                int len = decode(sb, buf);
                out.write(buf, 0, len);
                if (len < 3) {
                    break;
                }
                sb.delete(0, 4);
            }
        }
        return out.toByteArray();
    }

    private static int decode(StringBuilder sb, byte[] buf) {
        int v0 = indexOf(sb.charAt(0));
        int v1 = indexOf(sb.charAt(1));
        buf[0] = (byte) (v0 << 2 | v1 >> 4);
        int v2 = indexOf(sb.charAt(2));
        if (v2 == 64) {
            return 1;
        }
        buf[1] = (byte) ((v1 & B00001111) << 4 | v2 >> 2);
        int v3 = indexOf(sb.charAt(3));
        if (v3 == 64) {
            return 2;
        }
        buf[2] = (byte) ((v2 & B00000011) << 6 | v3);
        return 3;
    }

    private static int indexOf(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 65;
        } else if (c >= 'a' && c <= 'z') {
            return c - 71;
        } else if (c >= '0' && c <= '9') {
            return c + 4;
        } else if (c == '+' || c == '-') {
            return 62;
        } else if (c == '/' || c == '_') {
            return 63;
        } else if (c == '=') {
            return 64;
        } else {
            throw new IllegalArgumentException(String.format("invalid base64 char: '%c'", c));
        }
    }

    private final String chars;
    private final int maxLineLength;

    private Base64(String chars, int maxLineLength) {
        this.chars = chars;
        this.maxLineLength = maxLineLength;
    }

    /**
     * 编码Base64字符串。
     *
     * @return Base64字符串。
     */
    public String encode(byte[] data, int off, int len) {
        if (data == null) return null;
        ByteArrayInputStream in = new ByteArrayInputStream(data, off, len);
        StringBuilder sb = new StringBuilder(computeEncodedSize(in.available()));
        char[] cbuf = new char[4];
        byte[] buf = new byte[3];
        while ((len = in.read(buf, 0, buf.length)) != -1) {
            if (changeLine(sb.length())) {
                sb.append(LINE_SEPARATOR);
            }
            encode(buf, len, cbuf);
            sb.append(cbuf);
        }
        return sb.toString();
    }

    public String encode(byte[] data) {
        if (data == null) return null;
        return encode(data, 0, data.length);
    }

    private void encode(byte[] buf, int len, char[] cbuf) {
        int b0 = buf[0] & 0xff;
        cbuf[0] = chars.charAt(b0 >> 2);
        if (len == 1) {
            cbuf[1] = chars.charAt((b0 & B00000011) << 4);
            cbuf[2] = PADDING;
            cbuf[3] = PADDING;
            return;
        }
        int b1 = buf[1] & 0xff;
        cbuf[1] = chars.charAt((b0 & B00000011) << 4 | b1 >> 4);
        if (len == 2) {
            cbuf[2] = chars.charAt((b1 & B00001111) << 2);
            cbuf[3] = PADDING;
            return;
        }
        int b2 = buf[2] & 0xff;
        cbuf[2] = chars.charAt((b1 & B00001111) << 2 | b2 >> 6);
        cbuf[3] = chars.charAt(b2 & B00111111);
    }

    private int computeEncodedSize(int size) {
        size = size % 3 == 0 ? size / 3 * 4 : (size / 3 + 1) * 4;
        if (maxLineLength <= 0) return size;
        int lineSeparatorCount = size % maxLineLength == 0 ? size / maxLineLength - 1 : size / maxLineLength;
        return size + LINE_SEPARATOR.length() * lineSeparatorCount;
    }

    private boolean changeLine(int len) {
        if (maxLineLength <= 0) return false;
        return (len + LINE_SEPARATOR.length()) % (maxLineLength + LINE_SEPARATOR.length()) == 0;
    }
}
