package com.hrs.kloping;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import static com.hrs.kloping.HPlugin_AutoReply.*;

public class OnCommand {
    public static void onHandler(GroupMessageEvent event) {
        String text = event.getMessage().serializeToMiraiCode().trim();
        System.err.println(text);
        long q = event.getSender().getId();
        if (list2e.containsKey(q)) {
            onAdding(q, event);
            return;
        }
        if ((q == host.longValue() || followers.contains(q))) {
            if (text.equals(key)) {
                if (list2e.containsKey(q)) {
                    event.getGroup().sendMessage("请先完成当前添加");
                } else {
                    list2e.put(q, new entity(q));
                    event.getGroup().sendMessage("已添加到队列,请发送触发词");
                    return;
                }
            } else if (text.startsWith(selectKey)) {
                event.getGroup().sendMessage(selectOne(text));
                return;
            } else if (text.startsWith(deleteKey) && q == host) {
                event.getGroup().sendMessage(deleteOne(text));
                return;
            } else if (text.startsWith(OneComAddStr)) {
                if (OneComAdd(text.substring(OneComAddStr.length()).trim())) {
                    event.getGroup().sendMessage(String.format("添加完成"));
                } else {
                    event.getGroup().sendMessage(String.format("添加失败,可能字符中,没有分割关键字(%s),或已存在该关键词", OneComAddSplit));
                }
            }
        }
        String code = event.getMessage().serializeToMiraiCode().trim();
        if (k2v.containsKey(code)) {
            Message message = k2v.get(code);
            event.getGroup().sendMessage(message);
        }
    }

    private static boolean OneComAdd(String text) {
        try {
            String[] ss = text.split(OneComAddSplit);
            if (k2v.containsKey(ss[0])) return false;
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

    private static void onAdding(long q, GroupMessageEvent event) {
        entity entity = list2e.get(q);
        if (entity.getK() == null) {
            String code = event.getMessage().serializeToMiraiCode().trim().replaceAll("\n", "");
            if (k2v.containsKey(code)) {
                event.getGroup().sendMessage("该触发词,已存在");
                return;
            }
            entity.setK(code);
            event.getGroup().sendMessage("触发词设置完成,请添加回复词!");
        } else {
            MessageChain message = event.getMessage();
            entity.setV(message);
            String code = entity.getK();
            flushMap(entity);
            event.getGroup().sendMessage("添加完成");
            list2e.remove(q);
        }
    }

    private static Message selectOne(String code) {
        String k = code.substring(selectKey.length(), code.length());
        if (k2v.containsKey(k)) {
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append("触发词:").append(k).append("\r\n").append("回复词:").append(k2v.get(k));
            return builder.build();
        } else {
            return noFound;
        }
    }

    private static Message deleteOne(String code) {
        String k = code.substring(deleteKey.length(), code.length());
        if (k2v.containsKey(k)) {
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append("已删除\r\n触发词:").append(k).append("\r\n").append("回复词:").append(k2v.get(k));
            k2v.remove(k);
            resourceMap();
            return builder.build();
        } else {
            return new PlainText("未查询到该词(" + k + ")");
        }
    }

    private static final PlainText noFound = new PlainText("未查询到该词");
}
