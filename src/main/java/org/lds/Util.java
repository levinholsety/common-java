package org.lds;

import java.lang.reflect.Array;

public class Util {
    private static final IdGenerator defaultIdGenerator = new IdGenerator(0);

    public static void log(Object msg) {
        Logger.getLogger().log(msg);
    }

    public static long generateId() {
        return defaultIdGenerator.generateId();
    }

    public static boolean isNullOrWhiteSpace(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean isNullOrEmpty(Object array) {
        if (array == null) return true;
        if (!array.getClass().isArray()) throw new IllegalArgumentException("array");
        return Array.getLength(array) == 0;
    }

    public static boolean isNullOrEmpty(Iterable<?> iterable) {
        if (iterable == null) return true;
        return !iterable.iterator().hasNext();
    }
}
