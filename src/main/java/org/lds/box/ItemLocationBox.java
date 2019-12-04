package org.lds.box;

import java.io.IOException;

public class ItemLocationBox extends FullBox {
    public static class Item {
        private int itemId;
        private int extentCount;
        private Extent[] extents;

        public Item(ItemLocationBox box) throws IOException {
            if (box.getVersion() < 2) {
                itemId = box.bin.readUInt16();
            } else if (box.getVersion() == 2) {
                itemId = box.bin.readInt32();
            }
            if (box.getVersion() == 1 || box.getVersion() == 2) {
                box.bin.skip(2);
            }
            box.bin.skip(2);
            box.bin.skip(box.baseOffsetSize);
            extentCount = box.bin.readUInt16();
            extents = new Extent[extentCount];
            for (int i = 0; i < extentCount; i++) {
                extents[i] = new Extent(box);
            }
        }

        public int getItemId() {
            return itemId;
        }

        public int getExtentCount() {
            return extentCount;
        }

        public Extent getExtent(int index) {
            return extents[index];
        }
    }

    public static class Extent {
        private long extentOffset;
        private long extentLength;

        public Extent(ItemLocationBox box) throws IOException {
            if ((box.getVersion() == 1 || box.getVersion() == 2) && box.indexSize > 0) {
                box.bin.skip(box.indexSize);
            }
            extentOffset = box.bin.readInt64(box.offsetSize);
            extentLength = box.bin.readInt64(box.lengthSize);
        }

        public long getExtentOffset() {
            return extentOffset;
        }

        public long getExtentLength() {
            return extentLength;
        }
    }


    private int offsetSize;
    private int lengthSize;
    private int baseOffsetSize;
    private int indexSize;
    private int itemCount;
    private Item[] items;

    public ItemLocationBox(Box box) throws IOException {
        super(box);
        int v = bin.readUInt16();
        offsetSize = v >> 12 & 0xf;
        lengthSize = v >> 8 & 0xf;
        baseOffsetSize = v >> 4 & 0xf;
        indexSize = v & 0xf;
        if (getVersion() < 2) {
            itemCount = bin.readUInt16();
        } else if (getVersion() == 2) {
            itemCount = bin.readInt32();
        }
        items = new Item[itemCount];
        for (int i = 0; i < itemCount; i++) {
            items[i] = new Item(this);
        }
    }

    public int getItemCount() {
        return itemCount;
    }

    public Item getItem(int index) {
        return items[index];
    }

    public Item getItemById(int itemId) {
        for (int i = 0; i < itemCount; i++) {
            Item item = getItem(i);
            if (item.getItemId() == itemId) return item;
        }
        return null;
    }
}
