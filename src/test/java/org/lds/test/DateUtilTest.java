package org.lds.test;

import org.junit.Assert;
import org.junit.Test;
import org.lds.DateUtil;

import java.text.SimpleDateFormat;

public class DateUtilTest {

    @Test
    public void testParseDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals("2019-11-01 13:02:03", sdf.format(DateUtil.parseDate("2019-11-1 13:2:3")));
        Assert.assertEquals("2019-11-01 13:02:03", sdf.format(DateUtil.parseDate("2019-11-01 13:02:03")));
        Assert.assertEquals("2019-11-01 00:00:00", sdf.format(DateUtil.parseDate("2019-11-01")));
        Assert.assertEquals("1970-01-01 13:02:03", sdf.format(DateUtil.parseDate("13:02:03")));
        Assert.assertEquals("2019-11-01 13:02:03", sdf.format(DateUtil.parseDate("20191101130203")));
        Assert.assertEquals("2019-11-01 00:00:00", sdf.format(DateUtil.parseDate("20191101")));
        Assert.assertEquals("1970-01-01 13:02:03", sdf.format(DateUtil.parseDate("130203")));
        Assert.assertEquals("2019-11-01 21:02:03", sdf.format(DateUtil.parseDate("2019-11-01T13:02:03Z")));
        Assert.assertEquals("2019-11-01 20:02:03", sdf.format(DateUtil.parseDate("2019-11-01T13:02:03+01:00")));
        Assert.assertEquals("2019-11-01 21:02:03", sdf.format(DateUtil.parseDate("20191101T130203Z")));
        Assert.assertEquals("2019-11-01 20:02:03", sdf.format(DateUtil.parseDate("20191101T130203+0100")));
        Assert.assertEquals("2019-11-01 20:02:03", sdf.format(DateUtil.parseDate("20191101T130203+01")));
    }
}
