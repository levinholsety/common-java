package org.lds.box;

import java.io.IOException;

public class ItemInfoEntry extends FullBox {
    private int itemId;
    private String itemType;

    public ItemInfoEntry(Box box) throws IOException {
        super(box);
        if (getVersion() >= 2) {
            if (getVersion() == 2) {
                itemId = bin.readUInt16();
            } else if (getVersion() == 3) {
                itemId = bin.readInt32();
            }
            bin.skip(2);
            itemType = bin.readString(4);
        }
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemType() {
        return itemType;
    }
}
