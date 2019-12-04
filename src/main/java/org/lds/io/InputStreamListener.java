package org.lds.io;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamListener<T> {
    T onOpen(InputStream in) throws IOException;
}
