package com.hrs.kloping;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import static com.hrs.kloping.HPlugin_AutoReply.*;

public class OnCommand {
    public static void onHandler(MessageEvent event) {
        String text = event.getMessage().serializeToMiraiCode().trim();
        long q = event.getSender().getId();
        if (list2e.containsKey(q)) {
            onAdding(q, event);
            return;
        }
        if ((q == host.longValue() || followers.contains(q))) {
            if (text.equals(key)) {
                if (list2e.containsKey(q)) {
                    event.getSubject().sendMessage("请先完成当前添加");
                } else {
                    list2e.put(q, new entity(q));
                    event.getSubject().sendMessage("已添加到队列,请发送触发词");
                    return;
                }
            } else if (text.startsWith(selectKey)) {
                event.getSubject().sendMessage(selectOne(text));
                return;
            } else if (text.startsWith(deleteKey) && q == host) {
                event.getSubject().sendMessage(deleteOne(text));
                return;
            } else if (text.startsWith(OneComAddStr)) {
                if (OneComAdd(text.substring(OneComAddStr.length()).trim())) {
                    event.getSubject().sendMessage(String.format("添加完成"));
                } else {
                    event.getSubject().sendMessage(String.format("添加失败,可能字符中,没有分割关键字(%s)\n或存在敏感词\n或已存在该关键词", OneComAddSplit));
                }
                return;
            } else if (text.startsWith("设置冷却") && q == host) {
                try {
                    Float cd = Float.valueOf(text.substring(4).trim());
                    setCD(cd);
                    event.getSubject().sendMessage("当前冷却:" + HPlugin_AutoReply.cd + "秒");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        if (touchK) {
            String code = event.getMessage().serializeToMiraiCode().trim();
            String key = null;
            if ((key = MyUtils.mather(code, k2v.keySet())) != null) {
                Message message = k2v.get(key);
                event.getSubject().sendMessage(message);
            }
            if (cd <= 0) return;
            else {
                touchK = false;
                threads.execute(() -> {
                    try {
                        Thread.sleep((long) (cd * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    touchK = true;
                });
            }
        }
    }

    private static boolean OneComAdd(String text) {
        try {
            String[] ss = text.split(OneComAddSplit);
            String key = null;
            if ((key = MyUtils.mather(ss[0], k2v.keySet())) != null) return false;
            if (illegal(ss[0])) return false;
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
        entity entity = list2e.get(q);
        if (entity.getK() == null) {
            String code = event.getMessage().serializeToMiraiCode().trim();
            if (MyUtils.mather(code, k2v.keySet()) != null) {
                event.getSubject().sendMessage("该触发词,已存在");
                return;
            }
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
            list2e.remove(q);
        }
    }

    private static Message selectOne(String code) {
        String k = code.substring(selectKey.length()).trim();
        String key = null;
        if ((key = MyUtils.mather(k, k2v.keySet())) != null) {
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append("触发词:").append(k).append("\r\n").append("回复词:").append(k2v.get(key));
            return builder.build();
        } else {
            return noFound;
        }
    }

    private static Message deleteOne(String code) {
        String k = code.substring(deleteKey.length()).trim();
        String key = null;
        if ((key = MyUtils.mather(k, k2v.keySet())) != null) {
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append("已删除\r\n触发词:").append(k).append("\r\n").append("回复词:").append(k2v.get(key));
            k2v.remove(key);
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
        HPlugin_AutoReply.cd = cd;
        MyUtils.putStringInFile(thisPath + "/conf/auto_reply/cd", cd.toString());
    }
}
