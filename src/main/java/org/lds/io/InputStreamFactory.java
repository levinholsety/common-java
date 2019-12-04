package org.lds.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class InputStreamFactory {

    public abstract InputStream newInputStream() throws IOException;

    public <T> T openRead(InputStreamListener<T> listener) throws IOException {
        InputStream in = null;
        BufferedInputStream buf = null;
        try {
            in = newInputStream();
            if (in instanceof FileInputStream) {
                buf = new BufferedInputStream(in);
            }
            return listener.onOpen(buf == null ? in : buf);
        } finally {
            IOUtil.close(in);
        }
    }

    public <T> T mustOpen(InputStreamListener<T> listener) {
        try {
            return openRead(listener);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
