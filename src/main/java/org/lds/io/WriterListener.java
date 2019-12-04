package org.lds.io;

import java.io.IOException;
import java.io.Writer;

public interface WriterListener {
    void onOpen(Writer w) throws IOException;
}
