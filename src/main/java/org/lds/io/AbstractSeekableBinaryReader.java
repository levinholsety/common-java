package org.lds.io;

import java.io.IOException;

public abstract class AbstractSeekableBinaryReader extends AbstractBinaryReader implements Seekable {
    @Override
    public void skip(long n) throws IOException {
        seek(getPosition() + n);
    }
}
