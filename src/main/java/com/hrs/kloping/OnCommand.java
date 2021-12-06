package com.hrs.kloping;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hrs.kloping.MyUtils.getMessageByWord;
import static com.hrs.kloping.MyUtils.getPlantText;
import static com.hrs.kloping.Resource.*;

public class OnCommand {
    public static void onEvent(MessageEvent event) {
        threads.execute(() -> work(event));
    }

    private static Map<Long, String> adding = new ConcurrentHashMap<>();
    private static Map<Long, Entity> deleting = new ConcurrentHashMap<>();
    private static Map<Long, Long> cds = new ConcurrentHashMap<>();

    private static void work(MessageEvent event) {
        if (maybe(event.getSender().getId()))
            if (filter(event)) return;

        long gid = event.getSubject().getId();
        if (cds.containsKey(gid) && cds.get(gid) > System.currentTimeMillis()) return;
        String codeKey = event.getMessage().serializeToMiraiCode();
        Message message = MyUtils.getMessageByKey(codeKey);
        if (message != null)
            event.getSubject().sendMessage(message);
        if (conf.getCd() > 0f)
            cds.put(gid, (long) (System.currentTimeMillis() + conf.getCd() * 1000L));
    }

    private static boolean filter(MessageEvent event) {
        try {
            long q = event.getSender().getId();
            if (adding.containsKey(q)) {
                onAdding(event);
                return true;
            } else if (deleting.containsKey(q)) {
                onDeleting(event);
                return true;
            }
            String str = getPlantText(event);
            if (str == null) return false;
            if (conf.getInsertKey().equals(str)) {
                if (cantInsert(q)) return false;
                adding.put(q, "");
                event.getSubject().sendMessage("已添加至队列,请发送触发词");
                return true;
            } else if (str.startsWith(conf.getOneComInsert())) {
                if (cantInsert(q)) return false;
                String[] sss = str.split(conf.getOneComSplit());
                if (sss.length == 3) {
                    if (isIllegal(sss[1]) || isIllegal(sss[2])) {
                        event.getSubject().sendMessage("敏感词汇 ");
                    } else ss(sss[1], sss[2], event.getSubject());
                    return true;
                }
                return false;
            } else if (str.startsWith(conf.getDeleteKey())) {
                if (cantDelete(q)) return false;
                String word = str.substring(conf.getDeleteKey().length());
                Entity entity = getMessageByWord(word);
                if (entity.getVs().size() == 1) {
                    entity.setState(1);
                    event.getSubject().sendMessage(entity.toString("已删除:\n", 1));
                } else {
                    deleting.put(q, entity);
                    event.getSubject().sendMessage(entity.toString("回复数字\n删除对应的回复词\n-1表示全部:\n", 99));
                }
                return true;
            } else if (str.startsWith(conf.getSelectKey())) {
                if (cantInsert(q)) return false;
                String word = str.substring(conf.getDeleteKey().length());
                Entity entity = getMessageByWord(word);
                event.getSubject().sendMessage(entity.toString(99));
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static void onDeleting(MessageEvent event) {
        long q = event.getSender().getId();
        Entity entity = deleting.get(q);
        String str = getPlantText(event);
        try {
            int i = Integer.parseInt(str.trim());
            if (i == -1) {
                for (Entity.Response v : entity.getVs()) {
                    v.setState(1);
                }
            } else {
                Object[] os = entity.getVs().toArray();
                Entity.Response response = entity.getVs(i);
                response.setState(1);
            }
            entity.apply();
            sourceMap();
            event.getSubject().sendMessage(entity.toString("删除词:\n", 99));
        } catch (Exception e) {
            event.getSubject().sendMessage("取消删除");
        }
        deleting.remove(q);
    }

    private static void onAdding(MessageEvent event) {
        long q = event.getSender().getId();
        String v = adding.get(q);
        if (isIllegal(v) || isIllegal(event.getMessage())) {
            event.getSubject().sendMessage("敏感词汇 ");
            return;
        }
        if (v.trim().isEmpty()) {
            adding.put(q, event.getMessage().serializeToMiraiCode());
            event.getSubject().sendMessage("触发词设置完成");
        } else {
            ss(v, event.getMessage(), event.getSubject());
            adding.remove(q);
        }
    }


    private static String ss(String v, MessageChain message, Contact contact) {
        Entity.Response response = new Entity.Response();
        response.setData(MiraiCode.deserializeMiraiCode(message.serializeToMiraiCode()));
        response.setWeight(1);
        response.setState(0);
        Entity entity = (Entity) entityMap.get(s(v));
        if (entity == null) entity = new Entity(contact.getId());
        entity.setK_(v);
        entity.getVs().add(response);
        entityMap.put(entity.getTouchKey(), entity.apply());
        sourceMap();
        contact.sendMessage("添加完成");
        return "添加完成";
    }

    private static String s(String k) {
        return k
                .replaceAll("%", "%")
                .replaceAll("？", "?")
                .replaceAll("%\\?", ".{0,}")
                .replaceAll("%\\+", ".+")
                .replaceAll("%", ".{1,1}");
    }

    private static String ss(String v, String message, Contact contact) {
        return ss(v, new MessageChainBuilder().append(message).build(), contact);
    }

    //=====================================

    private static boolean maybe(long id) {
        if (conf.getFollowers().contains(-1L)) return true;
        if (conf.getDeletes().contains(-1L)) return true;
        if (conf.getHost() == id) return true;
        if (conf.getFollowers().contains(id)) return true;
        if (conf.getDeletes().contains(id)) return true;
        return false;
    }

    private static boolean cantDelete(long q) {
        if (!conf.getDeletes().contains(-1L)
                && !conf.getDeletes().contains(q)
                && conf.getHost() != q)
            return true;
        return false;
    }

    private static boolean cantInsert(long q) {
        if (!conf.getFollowers().contains(-1L)
                && !conf.getFollowers().contains(q)
                && conf.getHost() != q)
            return true;
        return false;
    }
}
