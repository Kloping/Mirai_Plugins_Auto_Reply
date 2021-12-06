package com.hrs.kloping;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

import static com.hrs.kloping.OnCommand.onEvent;
import static com.hrs.kloping.Resource.conf;

/**
 * this plugin make on mirai make in idea make by [@Author]
 *
 * @Author HRS 3474006766@qq.com github.com/kloping
 * @Create_Date 21/9/17
 * @Update 21/12/6
 */
public final class HPlugin_AutoReply extends JavaPlugin {
    public static final HPlugin_AutoReply INSTANCE = new HPlugin_AutoReply();

    private HPlugin_AutoReply() {
        super(new JvmPluginDescriptionBuilder("com.hrs.kloping.h_plugin_AutoReply", "0.3.1")
                .name("自定义回话插件 Author => HRS")
                .info("自定义回话插件")
                .author("HRS")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("HRS's AutoReply Plugin loaded!");
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
            public void handleMessage1(GroupMessageEvent event) {
                onEvent(event);
            }

            @EventHandler
            public void handleMessage0(FriendMessageEvent event) {
                if (conf.isPrivateK())
                    onEvent(event);
            }
        });
    }
}