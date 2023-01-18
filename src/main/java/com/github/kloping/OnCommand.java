package com.github.kloping;

import com.github.kloping.e0.MessagePack;
import io.github.kloping.common.Public;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.kloping.MyUtils.filterMatcher;
import static com.github.kloping.e0.MessagePack.SEND;
import static com.github.kloping.e0.MessagePack.SLEEP;

/**
 * @author github-kloping
 */
public class OnCommand {
    static {
        if (Resource.conf.getHost() == -1) {
            System.err.println("请在/conf/auto_reply/conf.json设置您的QQ以控制你的机器人");
        }
    }

    /**
     * 所有消息都会执行到这里
     *
     * @param event
     */
    public static void onEvent(MessageEvent event) {
        Resource.EXECUTOR_SERVICE.execute(() -> work(event));
    }

    private static Map<Long, String> adding = new ConcurrentHashMap<>();
    private static Map<Long, Entity> deleting = new ConcurrentHashMap<>();
    private static Map<Long, Long> cds = new ConcurrentHashMap<>();

    private synchronized static void work(MessageEvent event) {
        long gid = event.getSubject().getId();
        if (!isOpen(event)) return;
        if (maybe(event.getSender().getId())) {
            if (filter(event)) return;
        }
        if (event.getSender().getId() == event.getBot().getId())
            return;
        schedule(event, gid);
    }

    private static boolean schedule(MessageEvent event, long gid) {
        long cd0 = cds.getOrDefault(gid, 0L);
        long cd1 = System.currentTimeMillis();
        long cd2 = (long) (Resource.conf.getCd() * 1000L);
        boolean k1 = (cd1 - cd0) > (cd2);
        if (!k1) return true;
        String codeKey = event.getMessage().serializeToMiraiCode();
        MessagePack pack = MyUtils.getMessageByKey(codeKey);
        if (pack != null) {
            cds.put(gid, System.currentTimeMillis());
            Public.EXECUTOR_SERVICE.submit(() -> {
                int i = 1;
                boolean a = true;
                while (a) {
                    if (pack.getData().containKey(i)) {
                        Entry<String, Object> entry = pack.get(i);
                        String step = entry.getKey();
                        Object o = entry.getValue();
                        switch (step) {
                            case SEND:
                                event.getSubject().sendMessage((Message) o);
                                break;
                            case SLEEP:
                                Long l = Long.parseLong(o.toString());
                                try {
                                    Thread.sleep(l);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                break;
                        }
                    } else {
                        a = false;
                    }
                    i++;
                }
            });
        }
        return false;
    }

    private static boolean isOpen(MessageEvent event) {
        if (event instanceof GroupMessageEvent || event instanceof GroupTempMessageEvent) {
            String id = "g" + event.getSubject().getId();
            if (Resource.conf.getKv().containsKey(id)) {
                return Resource.conf.getKv().get(id);
            }
            return Resource.conf.getKv().getOrDefault(id, true);
        } else if (event instanceof FriendMessageEvent || event instanceof StrangerMessageEvent) {
            String id = "f" + event.getSubject().getId();
            if (Resource.conf.getKv().containsKey(id)) {
                return Resource.conf.getKv().get(id);
            }
            return Resource.conf.getKv().getOrDefault(id, true);
        } else {
            return false;
        }
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
