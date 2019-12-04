package org.lds.box;

import java.io.IOException;

public class ItemInfoBox extends FullBox {

    private final int entryCount;

    public ItemInfoBox(Box box) throws IOException {
        super(box);
        if (getVersion() == 0) {
            entryCount = bin.readUInt16();
        } else {
            entryCount = bin.readInt32();
        }
    }

    public int getEntryCount() {
        return entryCount;
    }
}
