package org.lds;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public enum Encoding {
    Default(Charset.defaultCharset()),
    GBK(Charset.forName("GBK")),
    ISO_8859_1(Charset.forName("ISO-8859-1")),
    UTF_8(Charset.forName("UTF-8"));

    private final Charset charset;

    Encoding(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public byte[] encode(String text) {
        if (text == null) return null;
        return text.getBytes(charset);
    }

    public String decode(byte[] byteArray) {
        if (byteArray == null) return null;
        return new String(byteArray, charset);
    }

    public String encodeURIComponent(String uriComponent) {
        if (uriComponent == null) return null;
        String encoded;
        try {
            encoded = URLEncoder.encode(uriComponent, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        encoded = encoded.replaceAll("\\+", "%20");
        return encoded;
    }

    public String decodeURIComponent(String uriComponent) {
        if (Util.isNullOrWhiteSpace(uriComponent)) return null;
        try {
            return URLDecoder.decode(uriComponent, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
