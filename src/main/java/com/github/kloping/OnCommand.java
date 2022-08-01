package com.github.kloping;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.kloping.MyUtils.filterMatcher;

/**
 * @author github-kloping
 */
public class OnCommand {
    static {
        if (Resource.conf.getHost() == -1) {
            System.err.println("请在/conf/auto_reply/conf.json设置您的QQ以控制你的机器人");
        }
    }

    //所有消息都会执行到这里
    public static void onEvent(MessageEvent event) {
        Resource.EXECUTOR_SERVICE.execute(() -> work(event));
    }

    private static Map<Long, String> adding = new ConcurrentHashMap<>();
    private static Map<Long, Entity> deleting = new ConcurrentHashMap<>();
    private static Map<Long, Long> cds = new ConcurrentHashMap<>();

    private synchronized static void work(MessageEvent event) {
        if (maybe(event.getSender().getId()))
            if (filter(event)) return;
        long gid = event.getSubject().getId();
        if (sche(event, gid)) return;
        if (Resource.conf.getCd() > 0f)
            cds.put(gid, (long) (System.currentTimeMillis() + Resource.conf.getCd() * 1000L));
    }

    private static boolean sche(MessageEvent event, long gid) {
        long cd0 = cds.getOrDefault(gid, 0L);
        long cd1 = System.currentTimeMillis();
        boolean k1 = cds.containsKey(gid) && cd0 > cd1;
        boolean k2 = isOpen(gid);
        if (!k2) return true;
        if (k1) return true;
        String codeKey = event.getMessage().serializeToMiraiCode();
        Message message = MyUtils.getMessageByKey(codeKey);
        if (message != null) event.getSubject().sendMessage(message);
        return false;
    }

    private static boolean isOpen(long gid) {
        if (Resource.conf.getMap().containsKey(-1L)) {
            return Resource.conf.getMap().get(-1L);
        }
        return Resource.conf.getMap().getOrDefault(gid, true);
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
            String str = event.getMessage().serializeToMiraiCode();
            if (str == null) return false;
            if (Resource.conf.getInsertKey().equals(str)) {
                if (cantInsert(q)) return false;
                adding.put(q, "");
                event.getSubject().sendMessage("已添加至队列,请发送触发词");
                return true;
            } else if (str.startsWith(Resource.conf.getOneComInsert())) {
                if (cantInsert(q)) return false;
                String[] sss = str.split(Resource.conf.getOneComSplit());
                if (sss.length == 3) {
                    if (Resource.isIllegal(sss[1]) || Resource.isIllegal(sss[2])) {
                        event.getSubject().sendMessage("敏感词汇 ");
                    } else {
                        ss(sss[1], sss[2], event.getSubject());
                    }
                    return true;
                }
                return false;
            } else if (str.startsWith(Resource.conf.getDeleteKey())) {
                if (cantDelete(q)) return false;
                String word = str.substring(Resource.conf.getDeleteKey().length());
                Entity entity = MyUtils.getMessageByWord(word);
                if (entity.getVSize() <= 0) return false;
                if (entity.getVSize() == 1) {
                    event.getSubject().sendMessage(entity.toString("已删除:\n", 1));
                    entity.getVs(1).setState(1);
                    entity.apply();
                    Resource.sourceMap();
                } else {
                    deleting.put(q, entity);
                    event.getSubject().sendMessage(entity.toString("回复数字\n删除对应的回复词\n-1表示全部:\n", 99));
                }
                return true;
            } else if (str.startsWith(Resource.conf.getSelectKey())) {
                if (cantInsert(q)) return false;
                String word = str.substring(Resource.conf.getDeleteKey().length());
                Entity entity = MyUtils.getMessageByWord(word);
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
        String str = MyUtils.getPlantText(event);
        try {
            int i = Integer.parseInt(str.trim());
            if (i == -1) {
                for (Entity.Response v : entity.getVs()) {
                    v.setState(1);
                }
            } else {
                Object[] os = entity.getVs().toArray();
                entity.getVs(i).setState(1);
            }
            entity.apply();
            Resource.sourceMap();
            event.getSubject().sendMessage(entity.toString("剩余词:\n", 99));
        } catch (Exception e) {
            event.getSubject().sendMessage("取消删除");
        }
        deleting.remove(q);
    }

    private static void onAdding(MessageEvent event) {
        long q = event.getSender().getId();
        String v = adding.get(q);
        if (Resource.isIllegal(v) || Resource.isIllegal(event.getMessage())) {
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
        response.setData(message);
        response.setWeight(1);
        response.setState(0);
        v = filterMatcher(v);
        Entity entity = (Entity) Resource.entityMap.get(s(v));
        if (entity == null) entity = new Entity(contact == null ? 0 : contact.getId());
        entity.setK_(v);
        entity.getVs().add(response);
        Resource.entityMap.put(entity.getTouchKey(), entity.apply());
        Resource.sourceMap();
        if (contact != null) {
            contact.sendMessage("添加完成");
        }
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

    public static String ss(String v, String message, Contact contact) {
        return ss(v, new MessageChainBuilder().append(MiraiCode.deserializeMiraiCode(message)).build(), contact);
    }

    //=====================================

    private static boolean maybe(long id) {
        if (Resource.conf.getFollowers().contains(-1L)) return true;
        if (Resource.conf.getDeletes().contains(-1L)) return true;
        if (Resource.conf.getHost() == id) return true;
        if (Resource.conf.getFollowers().contains(id)) return true;
        if (Resource.conf.getDeletes().contains(id)) return true;
        return false;
    }

    private static boolean cantDelete(long q) {
        if (!Resource.conf.getDeletes().contains(-1L)
                && !Resource.conf.getDeletes().contains(q)
                && Resource.conf.getHost() != q)
            return true;
        return false;
    }

    private static boolean cantInsert(long q) {
        if (!Resource.conf.getFollowers().contains(-1L)
                && !Resource.conf.getFollowers().contains(q)
                && Resource.conf.getHost() != q)
            return true;
        return false;
    }
}
