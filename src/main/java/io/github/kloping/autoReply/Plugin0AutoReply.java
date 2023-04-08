package io.github.kloping.autoReply;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.GroupTempMessageEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;
import org.jetbrains.annotations.NotNull;

/**
 * this plugin make on mirai make in jetbrains-idea make by [@Author]
 *
 * @Author HRS 3474006766@qq.com github.com/kloping
 * @Create_Date 21/9/17
 * @Update 21/12/16
 */
public class Plugin0AutoReply extends JavaPlugin {
    public static final Plugin0AutoReply INSTANCE = new Plugin0AutoReply();

    private Plugin0AutoReply() {
        super(new JvmPluginDescriptionBuilder("io.github.kloping.autoReply.Plugin0AutoReply", "1.2")
                .name("Custom Reply")
                .info("Custom Reply")
                .author("HRS")
                .dependsOn("io.github.gdpl2112.lib-tts",  true)
                .build());
        System.getProperties().setProperty("file.encoding", "utf-8");
    }

    @Override
    public void onEnable() {
        getLogger().info("HRS-AutoReply-Plugin-loaded");
        CommandManager.INSTANCE.registerCommand(CommandLine.INSTANCE, true);
        if (Resource.conf.getHost() == -1) {
            System.err.println("请在/conf/auto_reply/conf.json设置您的QQ以控制你的机器人");
        }
        GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost() {
            @Override
            public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
                super.handleException(context, exception);
            }

            @EventHandler
            public void handleMessage2(GroupTempMessageEvent event) {
                OnCommand.onEvent(event);
            }

            @EventHandler
            public void handleMessage1(GroupMessageEvent event) {
                OnCommand.onEvent(event);
            }

            @EventHandler
            public void handleMessage0(FriendMessageEvent event) {
                if (Resource.conf.isPrivateK()) {
                    OnCommand.onEvent(event);
                }
            }

            @EventHandler
            public void handleMessage0(StrangerMessageEvent event) {
                if (Resource.conf.isPrivateK()) {
                    OnCommand.onEvent(event);
                }
            }
        });
    }
}