package com.hrs.kloping;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @version 0.1
 * @Author HRS 3474006766@qq.com
 * @Date 21\\9\\16
 */
public final class HPlugin_AutoReply extends JavaPlugin {
    public static final HPlugin_AutoReply INSTANCE = new HPlugin_AutoReply();
    private static String key = "开始添加";
    private static String selectKey = "查询词";
    private static String deleteKey = "删除词";
    private static Long host = -1L;
    private static List<Long> followers = new LinkedList<>();
    private static final String splitK = ":==>";
    public static final ExecutorService threads = Executors.newFixedThreadPool(10);
    public static final Map<Number, entity> list2e = new ConcurrentHashMap<>();
    public static final Map<String, MessageChain> k2v = new ConcurrentHashMap<>();
    private static final String thisPath = System.getProperty("user.dir");

    private static void Init() {
        String s1 = MyUtils.getStringFromFile(thisPath + "\\conf\\auto_reply\\key");
        if (s1 == null || s1.isEmpty())
            MyUtils.appendStringInFile(thisPath + "\\conf\\auto_reply\\key", "#在写上触发添加的关键词", false);
        else key = s1;
        String s2 = MyUtils.getStringFromFile(thisPath + "\\conf\\auto_reply\\host");
        if (s2 == null || s2.isEmpty())
            MyUtils.appendStringInFile(thisPath + "\\conf\\auto_reply\\host", "#在写上你的的QQ号", false);
        else host = Long.valueOf(s2);
        String s3 = MyUtils.getStringFromFile(thisPath + "\\conf\\auto_reply\\selectKey");
        if (s3 == null || s3.isEmpty())
            MyUtils.appendStringInFile(thisPath + "\\conf\\auto_reply\\selectKey", "#在写上查询时关键词", false);
        else selectKey = s3;

        String[] sss = MyUtils.getStringsFromFile(thisPath + "\\conf\\auto_reply\\followers");
        if (sss == null)
            MyUtils.appendStringInFile(thisPath + "\\conf\\auto_reply\\followers", "#在这里添加所有能添加消息的人的QQ号", false);
        else for (String s : sss) {
            try {
                followers.add(Long.valueOf(s));
            } catch (Exception e) {
                continue;
            }
        }
        sss = MyUtils.getStringsFromFile(thisPath + "\\conf\\auto_reply\\data.data");
        if (sss != null)
            for (String ss : sss) {
                try {
                    String[] ss2 = ss.split(splitK);
                    MessageChain chain = MiraiCode.deserializeMiraiCode(ss2[1]);
                    k2v.put(ss2[0], chain);
                } catch (Exception e) {
                    continue;
                }
            }
    }

    private HPlugin_AutoReply() {
        super(new JvmPluginDescriptionBuilder("com.hrs.kloping.h_plugin_AutoReply", "0.1")
                .name("插件_3 Author => HRS")
                .info("自定义回话插件")
                .author("HRS")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("HRS's SImg Plugin loaded!");
        Init();
        if (host == -1) {
            System.err.println("请在\\conf\\auto_reply\\host设置您的QQ以控制你的机器人");
        }
        GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost() {
            @Override
            public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
                super.handleException(context, exception);
            }

            @EventHandler
            public void handleMessage(GroupMessageEvent event) {
                String text = event.getMessage().get(1).toString().trim();
                long q = event.getSender().getId();
                if (list2e.containsKey(q)) {
                    entity entity = list2e.get(q);
                    if (entity.getK() == null) {
                        String code = event.getMessage().serializeToMiraiCode().trim().replaceAll("\n", "");
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
                        String k = text.substring(selectKey.length(), text.length());
                        if (k2v.containsKey(k)) {
                            MessageChainBuilder builder = new MessageChainBuilder();
                            builder.append("触发词:").append(k).append("\r\n").append("回复词:").append(k2v.get(k));
                            event.getGroup().sendMessage(builder.build());
                        } else {
                            event.getGroup().sendMessage("未查询到该词");
                        }
                        return;
                    } else if (text.startsWith(deleteKey) && q == host) {
                        String k = text.substring(deleteKey.length(), text.length());
                        if (k2v.containsKey(k)) {
                            MessageChainBuilder builder = new MessageChainBuilder();
                            builder.append("已删除\r\n触发词:").append(k).append("\r\n").append("回复词:").append(k2v.get(k));
                            k2v.remove(k);
                            resourceMap();
                            event.getGroup().sendMessage(builder.build());
                        } else {
                            event.getGroup().sendMessage("未查询到该词(" + k + ")");
                        }
                        return;
                    }
                }
                String code = event.getMessage().serializeToMiraiCode().trim();
                if (k2v.containsKey(code)) {
                    Message message = k2v.get(code);
                    event.getGroup().sendMessage(message);
                }
            }

            @EventHandler
            public void handleMessage(FriendMessageEvent event) {
            }
        });
    }

    private static synchronized void flushMap(entity entity) {
        String k = entity.getK();
        MessageChain message = entity.getV();
        String v = message.serializeToMiraiCode();
        String line = k + splitK + v;
        MyUtils.appendStringInFile(thisPath + "\\conf\\auto_reply\\data.data", line, true);
        k2v.put(k, message);
    }

    private static synchronized void resourceMap() {
        List<String> list = new LinkedList<>();
        for (String k : k2v.keySet()) {
            MessageChain message = k2v.get(k);
            String v = message.serializeToMiraiCode();
            String line = k + splitK + v;
            list.add(line);
        }
        MyUtils.putStringInFile(thisPath + "\\conf\\auto_reply\\data.data", list.toArray(new String[0]));
    }
}