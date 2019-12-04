package org.lds.test;

import org.junit.Assert;
import org.junit.Test;
import org.lds.Base64;
import org.lds.ByteArrayUtil;
import org.lds.Encoding;
import org.lds.Hex;

public class UtilTest {

    @Test
    public void testHex() {
        byte[] data = ByteArrayUtil.random(101);
        String hexString = Hex.encode(data);
        byte[] decodedData = Hex.decode(hexString);
        Assert.assertArrayEquals(data, decodedData);
    }

    @Test
    public void testBase64() {
        byte[] data = ByteArrayUtil.random(101);
        String base64String = Base64.STANDARD.encode(data);
        byte[] decodedData = Base64.decode(base64String);
        Assert.assertArrayEquals(data, decodedData);
    }

    @Test
    public void testEncoding() {
        Assert.assertEquals("http%3A%2F%2Fwww.w3school.com.cn", Encoding.UTF_8.encodeURIComponent("http://www.w3school.com.cn"));
        Assert.assertEquals("http%3A%2F%2Fwww.w3school.com.cn%2Fp%201%2F", Encoding.UTF_8.encodeURIComponent("http://www.w3school.com.cn/p 1/"));
        Assert.assertEquals("%2C%2F%3F%3A%40%26%3D%2B%24%23", Encoding.UTF_8.encodeURIComponent(",/?:@&=+$#"));
        Assert.assertEquals("http://www.w3school.com.cn/My first/", Encoding.UTF_8.decodeURIComponent("http%3A%2F%2Fwww.w3school.com.cn%2FMy%20first%2F"));
    }

}
