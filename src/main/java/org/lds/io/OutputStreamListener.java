package org.lds.io;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamListener {
    void onOpen(OutputStream out) throws IOException;
}
