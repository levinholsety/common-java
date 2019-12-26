package org.lds.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.lds.Base64;
import org.lds.ByteArrayUtil;
import org.lds.Encoding;
import org.lds.Hex;
import org.lds.exif.Exif;
import org.lds.math.Fraction;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

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

    @Test
    public void testFraction() {
        System.out.println(new Fraction(6, -10).toString());
    }

    @Test
    public void testExif() throws IOException {
        File dir = new File("D:\\Temp");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                int index = name.lastIndexOf('.');
                if (index < 0) {
                    return false;
                }
                String ext = name.substring(index).toLowerCase();
                return ".jpg".equals(ext) || ".heic".equals(ext) || ".nef".equals(ext);
            }
        });
        if (files == null) {
            return;
        }
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
            Exif exif = Exif.parse(file);
            if (exif == null) {
                continue;
            }
            JSONObject obj = new JSONObject(true);
            obj.put("make", exif.getMake());
            obj.put("model", exif.getModel());
            obj.put("dateTime", exif.getDateTime());
            obj.put("exposureTime", exif.getExposureTime());
            obj.put("fNumber", exif.getFNumber());
            obj.put("gpsLatitude", exif.getGPSLatitude());
            obj.put("gpsLongitude", exif.getGPSLongitude());
            obj.put("lens", exif.getLens());
            System.out.println(JSON.toJSONString(obj, true));
        }
    }
}
