package com.hrs.kloping;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import java.util.List;
import java.util.Random;

import static com.hrs.kloping.HPlugin_AutoReply.*;

public class OnCommand {
    public static boolean sohwed;

    public static void onHandler(MessageEvent event) {
        if (!sohwed) {
            sohwed = true;
            if (conf.getFollowers().contains(-1L))
                System.out.println("开放模式,所有人都可添加");
        }
        String text = event.getMessage().serializeToMiraiCode().trim();
        long qid = event.getSender().getId();
        if (conf.getList2e().containsKey(qid)) {
            onAdding(qid, event);
            return;
        }
        if (text.equals(conf.getKey())) {
            if (conf.getFollowers().contains(-1L) || conf.getFollowers().contains(qid) || conf.host.longValue() == qid)
                if (conf.getList2e().containsKey(qid))
                    event.getSubject().sendMessage("请先完成当前添加");
                else {
                    conf.getList2e().put(qid, new entity(qid));
                    event.getSubject().sendMessage("已添加到队列,请发送触发词");
                }
        } else if (text.startsWith(conf.getSelectKey())) {
            if (conf.getFollowers().contains(-1L) || conf.getFollowers().contains(qid) || conf.host.longValue() == qid)
                event.getSubject().sendMessage(selectOne(text));
        } else if (text.startsWith(conf.getDeleteKey())) {
            if (conf.getCanDeletes().contains(-1L) || qid == conf.getHost().longValue() || conf.getCanDeletes().contains(qid))
                event.getSubject().sendMessage(deleteOne(text));
        } else if (text.startsWith(conf.getOneComAddStr())) {
            if (conf.getFollowers().contains(-1L) || conf.getFollowers().contains(qid) || conf.host.longValue() == qid)
                if (OneComAdd(text.substring(conf.getOneComAddStr().length()).trim())) {
                    event.getSubject().sendMessage(String.format("添加完成"));
                } else {
                    event.getSubject().sendMessage(String.format("添加失败,可能字符中,没有分割关键字(%s)\n或存在敏感词\n或已存在该关键词", conf.oneComAddSplit));
                }
        } else if (text.startsWith("设置冷却") && qid == conf.getHost()) {
            try {
                Float cd = Float.valueOf(text.substring(4).trim());
                setCD(cd);
                event.getSubject().sendMessage("当前冷却:" + HPlugin_AutoReply.conf.getCd() + "秒");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (touchK) {
            String code = event.getMessage().serializeToMiraiCode().trim();
            String key = null;
            if ((key = MyUtils.mather(code, k2v.keySet())) != null) {
                List<MessageChain> message = k2v.get(key);
                event.getSubject().sendMessage(rand(message));
            }
            if (conf.getCd() <= 0) return;
            else {
                touchK = false;
                threads.execute(() -> {
                    try {
                        Thread.sleep((long) (conf.getCd() * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    touchK = true;
                });
            }
        }
    }

    public static final Random random = new Random();

    private static MessageChain rand(List<MessageChain> message) {
        return message.get(random.nextInt(message.size()));
    }

    private static boolean OneComAdd(String text) {
        try {
            String[] ss = text.split(conf.oneComAddSplit);
            String key = null;
//            if ((key = MyUtils.mather(ss[0], k2v.keySet())) != null) return false;
            if (illegal(ss[0])) return false;
            if (ss[0].trim().isEmpty()) return false;
            if (ss[1].trim().isEmpty()) return false;
            entity entity = new entity(0);
            entity.setK(ss[0]);
            entity.setV(MiraiCode.deserializeMiraiCode(ss[1]));
            String code = entity.getK();
            flushMap(entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void onAdding(long q, MessageEvent event) {
        entity entity = conf.getList2e().get(q);
        if (entity.getK() == null) {
            String code = event.getMessage().serializeToMiraiCode().trim();
            /*if (MyUtils.mather(code, k2v.keySet()) != null) {
                event.getSubject().sendMessage("该触发词,已存在");
                return;
            }*/
            if (illegal(code)) {
                event.getSubject().sendMessage("敏感词汇!");
                return;
            }
            entity.setK(code);
            event.getSubject().sendMessage("触发词设置完成,请添加回复词!");
        } else {
            MessageChain message = event.getMessage();
            entity.setV(message);
            String code = entity.getK();
            flushMap(entity);
            event.getSubject().sendMessage("添加完成");
            conf.getList2e().remove(q);
        }
    }

    private static Message selectOne(String code) {
        String k = code.substring(conf.getSelectKey().length()).trim();
        String key = null;
        if ((key = MyUtils.mather(k, k2v.keySet())) != null) {
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append("触发词:").append(k).append("\r\n");
            for (MessageChain chain : k2v.get(key)) {
                builder.append("回复词:");
                builder.append(chain);
                builder.append("\n");
            }
            return builder.build();
        } else {
            return noFound;
        }
    }

    private static Message deleteOne(String code) {
        String k = code.substring(conf.getDeleteKey().length()).trim();
        String key = null;
        if ((key = MyUtils.mather(k, k2v.keySet())) != null) {
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append("已删除\r\n触发词:").append(k).append("\r\n");
            for (MessageChain chain : k2v.get(key)) {
                builder.append("回复词:");
                builder.append(chain);
                builder.append("\n");
            }
            k2v.remove(key);
            k2vs.remove(key);
            resourceMap();
            return builder.build();
        } else {
            return new PlainText("未查询到该词(" + k + ")");
        }
    }

    private static final PlainText noFound = new PlainText("未查询到该词");

    private static boolean illegal(String k) {
        return illegalKeys.contains(k.trim());
    }

    public static void setCD(Float cd) {
        HPlugin_AutoReply.conf.setCd(cd);
        HPlugin_AutoReply.conf.apply();
    }
}
