package org.lds.box;

import java.io.IOException;

public class FullBox extends Box {
    private final int version;
    private final int flags;

    public FullBox(Box box) throws IOException {
        super(box);
        version = bin.readUInt8();
        flags = (int) bin.readInt64(3);
    }

    public int getVersion() {
        return version;
    }

    public int getFlags() {
        return flags;
    }

}
