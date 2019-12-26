package org.lds.io;

import java.io.IOException;

public interface Seekable {

    void seek(long pos) throws IOException;

    long getPosition() throws IOException;

    long getLength() throws IOException;
}
