package org.lds.io;

import org.lds.Encoding;
import org.lds.Util;

import java.io.*;
import java.nio.channels.FileChannel;

public class IOUtil {
    private static final int defaultBufferSize = 0x10000;

    public static void copy(File srcFile, File dstFile) throws IOException {
        if (srcFile == null || !srcFile.isFile() || dstFile == null || dstFile.isDirectory()) return;
        long length = srcFile.length();
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;
        try {
            srcChannel = new FileInputStream(srcFile).getChannel();
            dstChannel = new FileOutputStream(dstFile).getChannel();
            long offset = 0;
            while (offset < length) {
                offset += srcChannel.transferTo(offset, length - offset, dstChannel);
            }
        } finally {
            close(dstChannel, srcChannel);
        }
    }

    public static void transfer(InputStream in, OutputStream out) throws IOException {
        if (in == null || out == null) return;
        byte[] buf = new byte[defaultBufferSize];
        int len;
        while ((len = in.read(buf, 0, buf.length)) != -1) {
            if (len > 0) {
                out.write(buf, 0, len);
            }
        }
    }

    public static void transfer(Reader r, Writer w) throws IOException {
        if (r == null || w == null) return;
        char[] cbuf = new char[defaultBufferSize];
        int len;
        while ((len = r.read(cbuf, 0, cbuf.length)) != -1) {
            if (len > 0) {
                w.write(cbuf, 0, len);
            }
        }
    }

    public static <T extends Closeable> void close(T... objects) {
        if (objects == null) return;
        for (T obj : objects) {
            if (obj == null) continue;
            try {
                obj.close();
            } catch (IOException e) {
                Util.log(e);
            }
        }
    }

    public static byte[] readAll(File file) throws IOException {
        if (file == null || !file.isFile()) return null;
        byte[] data = new byte[(int) file.length()];
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            if (in.read(data) == data.length) return data;
            throw new EOFException();
        } finally {
            close(in);
        }
    }

    public static byte[] readAll(String filename) throws IOException {
        if (Util.isNullOrWhiteSpace(filename)) return null;
        return readAll(new File(filename));
    }

    public static String readAllText(final File file, Encoding encoding) throws IOException {
        if (file == null || !file.isFile()) return null;
        if (encoding == null) {
            encoding = Encoding.Default;
        }
        char[] cbuf = new char[(int) file.length()];
        Reader r = null;
        try {
            r = new InputStreamReader(
                    new BufferedInputStream(
                            new FileInputStream(file)),
                    encoding.getCharset());
            return new String(cbuf, 0, r.read(cbuf));
        } finally {
            close(r);
        }
    }

    public static String readAllText(String filename, Encoding encoding) throws IOException {
        if (Util.isNullOrWhiteSpace(filename)) return null;
        return readAllText(new File(filename), encoding);
    }

    public static void writeAll(File file, final byte[] data) throws IOException {
        if (data == null) return;
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(data);
            out.flush();
        } finally {
            close(out);
        }
    }

    public static void writeAll(String filename, byte[] data) throws IOException {
        writeAll(new File(filename), data);
    }

    public static void writeAllText(File file, final String text, Encoding encoding) throws IOException {
        if (text == null) return;
        if (encoding == null) {
            encoding = Encoding.Default;
        }
        Writer w = null;
        try {
            w = new OutputStreamWriter(
                    new BufferedOutputStream(
                            new FileOutputStream(file)),
                    encoding.getCharset());
            w.write(text);
            w.flush();
        } finally {
            close(w);
        }
    }

    public static void writeAllText(String filename, String text, Encoding encoding) throws IOException {
        writeAllText(new File(filename), text, encoding);
    }

    public static void appendText(File file, String text, Encoding encoding) throws IOException {
        if (text == null) return;
        if (encoding == null) {
            encoding = Encoding.Default;
        }
        Writer w = null;
        try {
            w = new OutputStreamWriter(
                    new BufferedOutputStream(
                            new FileOutputStream(file, true)),
                    encoding.getCharset());
            w.write(text);
            w.flush();
        } finally {
            IOUtil.close(w);
        }
    }

    public static void appendText(String filename, String text, Encoding encoding) throws IOException {
        appendText(new File(filename), text, encoding);
    }
}
