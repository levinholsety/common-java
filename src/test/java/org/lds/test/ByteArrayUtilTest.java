package org.lds.test;

import org.junit.Assert;
import org.junit.Test;
import org.lds.ByteArrayUtil;

import java.nio.ByteOrder;

public class ByteArrayUtilTest {
    @Test
    public void testByteArrayToNumber() {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        Assert.assertEquals(258, ByteArrayUtil.toShortValue(data, ByteOrder.BIG_ENDIAN));
        Assert.assertEquals(16909060, ByteArrayUtil.toIntValue(data, ByteOrder.BIG_ENDIAN));
        Assert.assertEquals(72623859790382856L, ByteArrayUtil.toLongValue(data, ByteOrder.BIG_ENDIAN));
        Assert.assertEquals(513, ByteArrayUtil.toShortValue(data, ByteOrder.LITTLE_ENDIAN));
        Assert.assertEquals(67305985, ByteArrayUtil.toIntValue(data, ByteOrder.LITTLE_ENDIAN));
        Assert.assertEquals(578437695752307201L, ByteArrayUtil.toLongValue(data, ByteOrder.LITTLE_ENDIAN));
        data = new byte[]{-1, -2, -3, -4, -5, -6, -7, -8};
        Assert.assertEquals(65534, ByteArrayUtil.toUnsignedShortValue(data, ByteOrder.BIG_ENDIAN));
        Assert.assertEquals(4294901244L, ByteArrayUtil.toUnsignedIntValue(data, ByteOrder.BIG_ENDIAN));
        Assert.assertEquals(-283686952306184L, ByteArrayUtil.toLongValue(data, ByteOrder.BIG_ENDIAN));
        Assert.assertEquals(65279, ByteArrayUtil.toUnsignedShortValue(data, ByteOrder.LITTLE_ENDIAN));
        Assert.assertEquals(4244504319L, ByteArrayUtil.toUnsignedIntValue(data, ByteOrder.LITTLE_ENDIAN));
        Assert.assertEquals(-506097522914230529L, ByteArrayUtil.toLongValue(data, ByteOrder.LITTLE_ENDIAN));
    }
}
