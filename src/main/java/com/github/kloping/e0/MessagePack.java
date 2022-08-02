package com.github.kloping.e0;

import io.github.kloping.extension.ThreeMap;
import io.github.kloping.extension.ThreeMapImpl;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * @author github.kloping
 */
public class MessagePack {
    public static final Pattern PATTERN = Pattern.compile("\\[NEXT\\d*]");
    public static final String SEND = "send";
    public static final String SLEEP = "sleep";

    public static Field F0;
    public static Field F1;

    static {
        try {
            F1 = ThreeMapImpl.class.getDeclaredField("map2");
            F1.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            F0 = ThreeMapImpl.class.getDeclaredField("map1");
            F0.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public ThreeMap<Integer, String, Object> data = new ThreeMapImpl<>();

    public ThreeMap<Integer, String, Object> getData() {
        return data;
    }

    public void setData(ThreeMap<Integer, String, Object> data) {
        this.data = data;
    }

    public Entry<String, Object> get(Integer integer) {
        try {
            Map<Integer, String> map1 =
                    (Map<Integer, String>) F0.get(data);
            Map<Integer, Object> map2 =
                    (Map<Integer, Object>) F1.get(data);
            return new AbstractMap.SimpleEntry<>(map1.get(integer.intValue()), map2.get(integer));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
