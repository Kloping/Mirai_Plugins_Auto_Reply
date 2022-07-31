package com.github.kloping;

import com.alibaba.fastjson.JSONObject;
import io.github.kloping.object.ObjectUtils;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author github-kloping
 */
public class MyUtils {

    public static <T> void objs2list(List list, Class<T> cla) {
        List<T> ls = new ArrayList<>();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();
            if (o instanceof JSONObject) {
                ls.add(((JSONObject) o).toJavaObject(cla));
            } else if (ObjectUtils.isSuperOrInterface(o.getClass(), cla)) {
                ls.add((T) o);
            }
        }
        list.clear();
        list.addAll(ls);
    }
    public static String filterMatcher(String str) {
        str = str.replaceAll("\\[mirai:", "\\\\[mirai:");
        return str;
    }

    public static final Random RAND = new Random();

    public static Message getMessageByKey(String key) {
        Entity entity = getEntity(key);
        if (entity != null && entity.getState() == 0) return get(entity.getVs());
        return null;
    }

    private static Entity getEntity(String key) {
        Map<String, Entity> tempMap = new HashMap<>();
        Entity entity = (Entity) Resource.entityMap.get(key);
        if (entity == null) {
            for (String s : Resource.entityMap.keySet()) {
                try {
                    if (key.matches(s)) {
                        tempMap.put(key, entity);
                        return (Entity)  Resource.entityMap.get(s);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return entity;
    }

    public static Entity getMessageByWord(String key) {
        Entity entity = getEntity(key);
        if (entity != null && entity.getState() == 0) return entity;
        return null;
    }

    private static Message get(Set<Entity.Response> vs) {
        try {
            Map<Integer, Entity.Response> m = new HashMap<>();
            int n = 0;
            for (Entity.Response v : vs) {
                if (v.getState() != 0) continue;
                for (int i = 0; i < v.getWeight(); i++)
                    m.put(n++, v);
            }
            if (n <= 0) return null;
            int r = RAND.nextInt(n);
            return m.get(r).getData();
        } finally {
            System.gc();
        }
    }

    public static String getPlantText(MessageEvent event) {
        PlainText plainText = null;
        for (SingleMessage singleMessage : event.getMessage()) {
            if (singleMessage instanceof PlainText) {
                plainText = (PlainText) singleMessage;
                break;
            }
        }
        if (plainText == null) return null;
        return plainText.getContent().toString().trim();
    }


    /**
     * 将指定数字转为指定位数字符
     * (2,9)  => 09
     *
     * @param i
     * @param value
     * @return
     */
    public static String toStr(int i, int value) {
        String s0 = Integer.toString(value);
        while (s0.length() < i) {
            s0 = "0" + s0;
        }
        return s0;
    }

    public static final String[] WEEK_DAYS = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 获取当前日期是星期几
     *
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return WEEK_DAYS[w];
    }

    /**
     * 获取当前日期是星期几
     *
     * @return
     */
    public static Integer getWeekOfDateSt() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return w;
    }

    public static final SimpleDateFormat F0 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static int getYear() {
        String s = F0.format(new Date());
        return Integer.parseInt(s.substring(0, 4));
    }

    public static int getMon() {
        String s = F0.format(new Date());
        return Integer.parseInt(s.substring(5, 7));
    }

    public static int getDay() {
        String s = F0.format(new Date());
        return Integer.parseInt(s.substring(8, 10));
    }

    public static int getHour() {
        String s = F0.format(new Date());
        return Integer.parseInt(s.substring(11, 13));
    }

    public static int getMinutes() {
        String s = F0.format(new Date());
        return Integer.parseInt(s.substring(14, 16));
    }
}
