package org.lds;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DateUtil {

    private static class Parser extends ThreadLocal<DateFormat> {
        private final String pattern;
        private final int zoneType;

        private Parser(String pattern, int zoneType) {
            this.pattern = pattern;
            this.zoneType = zoneType;
        }

        private Parser(String pattern) {
            this(pattern, 0);
        }

        public Date parse(String value) throws ParseException {
            switch (zoneType) {
                case 1:
                    value = value.substring(0, value.length() - 1) + "+0000";
                    break;
                case 3:
                    value = value + "00";
                    break;
                case 6:
                    value = new StringBuilder(value).deleteCharAt(value.length() - 3).toString();
                    break;
            }
            return get().parse(value);
        }

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(pattern);
        }
    }

    private static final Map<Pattern, Parser> parserMap;

    static {
        parserMap = Collections.synchronizedMap(new HashMap<Pattern, Parser>());
        parserMap.put(Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$"), new Parser("yyyy-M-d H:m:s"));
        parserMap.put(Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$"), new Parser("yyyy-M-d"));
        parserMap.put(Pattern.compile("^\\d{1,2}:\\d{1,2}:\\d{1,2}$"), new Parser("H:m:s"));
        parserMap.put(Pattern.compile("^\\d{14}$"), new Parser("yyyyMMddHHmmss"));
        parserMap.put(Pattern.compile("^\\d{8}$"), new Parser("yyyyMMdd"));
        parserMap.put(Pattern.compile("^\\d{6}$"), new Parser("HHmmss"));
        parserMap.put(Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$"), new Parser("yyyy-MM-dd'T'HH:mm:ssZ", 1));
        parserMap.put(Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}$"), new Parser("yyyy-MM-dd'T'HH:mm:ssZ", 6));
        parserMap.put(Pattern.compile("^\\d{8}T\\d{6}Z$"), new Parser("yyyyMMdd'T'HHmmssZ", 1));
        parserMap.put(Pattern.compile("^\\d{8}T\\d{6}[+-]\\d{4}$"), new Parser("yyyyMMdd'T'HHmmssZ", 5));
        parserMap.put(Pattern.compile("^\\d{8}T\\d{6}[+-]\\d{2}$"), new Parser("yyyyMMdd'T'HHmmssZ", 3));
    }

    public static Date parseDate(String value) throws ParseException {
        if (Util.isNullOrWhiteSpace(value)) return null;
        for (Pattern pattern : parserMap.keySet()) {
            if (pattern.matcher(value).find()) {
                return parserMap.get(pattern).parse(value);
            }
        }
        throw new ParseException(value, 0);
    }

    public static long currentUnixTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static long toUnixTimestamp(Date date) {
        if (date == null) throw new NullPointerException("date");
        return date.getTime() / 1000;
    }

    public static Date fromUnixTimestamp(long timestamp) {
        return new Date(timestamp * 1000);
    }
}
