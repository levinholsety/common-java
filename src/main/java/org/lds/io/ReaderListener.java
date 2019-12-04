package org.lds.io;

import java.io.IOException;
import java.io.Reader;

public interface ReaderListener<T> {
    T onOpen(Reader r) throws IOException;
}
