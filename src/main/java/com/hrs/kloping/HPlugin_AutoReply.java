package com.hrs.kloping;

import io.github.kloping.initialize.FileInitializeValue;
import io.github.kloping.map.MapUtils;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.console.MiraiConsoleImplementation;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @version 0.2.6
 * @Author HRS 3474006766@qq.com github.com/kloping
 * @Create_Date 21/9/17
 * @Update 21/11/26
 */
public final class HPlugin_AutoReply extends JavaPlugin {
    public static final HPlugin_AutoReply INSTANCE = new HPlugin_AutoReply();
    public static final ExecutorService threads = Executors.newFixedThreadPool(10);
    public static String thisPath = MiraiConsoleImplementation.getInstance().getRootPath().toFile().getAbsolutePath();
    public static boolean touchK = true;
    public static Map<String, List<MessageChain>> k2v = new ConcurrentHashMap<>();
    public static Map<String, List<String>> k2vs = new ConcurrentHashMap<>();
    public static List<String> illegalKeys = new CopyOnWriteArrayList<>();

//    public static String key = "开始添加";
//    public static String selectKey = "查询词";
//    public static String deleteKey = "删除词";
//    public static Long host = -1L;
//    public static List<Long> followers = new LinkedList<>();
//    public static final String splitK = ":==>";
//    public static final Map<Number, entity> list2e = new ConcurrentHashMap<>();
//    public static final Map<String, MessageChain> k2v = new ConcurrentHashMap<>();
//    public static String OneComAddStr = "/添加";
//    public static String OneComAddSplit = " ";
//    public static boolean openPrivate = false;
//    public static float cd = 0;

    public static Conf conf = new Conf();

    private static void Init() {
        createFile();
        thisPath = thisPath == null ? "." : thisPath;
        conf = FileInitializeValue.getValue(thisPath + "/conf/auto_reply/conf.json", conf, true);
        Initer.Init();
    }

    private static void createFile() {
        try {
            File ff = new File(thisPath);
            new File(ff, "/conf/auto_reply/conf.json").getParentFile().mkdirs();
            new File(ff, "/conf/auto_reply/conf.json").createNewFile();
            new File(ff, "/conf/auto_reply/data.json").getParentFile().mkdirs();
            new File(ff, "/conf/auto_reply/data.json").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HPlugin_AutoReply() {
        super(new JvmPluginDescriptionBuilder("com.hrs.kloping.h_plugin_AutoReply", "0.2.7-M1")
                .name("插件_3 Author => HRS")
                .info("自定义回话插件")
                .author("HRS")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("HRS's AutoReply Plugin loaded!");
        Init();
        CommandManager.INSTANCE.registerCommand(CommandLine.INSTANCE, true);
        if (conf.getHost() == -1) {
            System.err.println("请在/conf/auto_reply/conf.json设置您的QQ以控制你的机器人");
        }
        GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost() {
            @Override
            public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
                super.handleException(context, exception);
            }

            @EventHandler
            public void handleMessage(GroupMessageEvent event) {
                threads.execute(() -> {
                    OnCommand.onHandler(event);
                });
            }

            @EventHandler
            public void handleMessage(FriendMessageEvent event) {
                if (conf.isOpenPrivate())
                    threads.execute(() -> {
                        OnCommand.onHandler(event);
                        if (event.getSender().getId() == conf.getHost())
                            if (event.getMessage().serializeToMiraiCode().trim().equals("autoReplyReloadConf")) {
                                Initer.Init();
                                event.getSubject().sendMessage("重新加载配置完成!");
                            }
                    });

            }

            @EventHandler
            public void handleMessage(StrangerMessageEvent event) {
                if (conf.isOpenPrivate())
                    threads.execute(() -> {
                        OnCommand.onHandler(event);
                    });
            }
        });
    }

    public static synchronized void flushMap(entity entity) {
        String k = entity.getK();
        MessageChain message = entity.getV();
        String v = message.serializeToMiraiCode();
        if (k2vs.get(k) == null || !k2vs.get(k).contains(v)) {
            MapUtils.append(k2v, k, message);
            MapUtils.append(k2vs, k, v);
            FileInitializeValue.putValues(thisPath + "/conf/auto_reply/data.json", k2vs, true);
        }
    }

    public static synchronized void resourceMap() {
/*
        List<String> list = new LinkedList<>();
        for (String k : k2v.keySet()) {
            MessageChain message = k2v.get(k);
            String v = message.serializeToMiraiCode();
            String line = k + conf.getSplitK() + v;
            list.add(line);
        }
        MyUtils.putStringInFile(thisPath + "/conf/auto_reply/data.data", list.toArray(new String[0]));
*/
        FileInitializeValue.putValues(thisPath + "/conf/auto_reply/data.json", k2vs, true);
    }
}