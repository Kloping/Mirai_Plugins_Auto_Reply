package com.github.kloping;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author github-kloping
 */
public class MyUtils {
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
                        return (Entity) Resource.entityMap.get(s);
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
}
