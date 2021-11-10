package com.hrs.kloping;

import cn.kloping.initialize.FileInitializeValue;
import net.mamoe.mirai.console.command.java.JSimpleCommand;

import static com.hrs.kloping.HPlugin_AutoReply.conf;
import static com.hrs.kloping.HPlugin_AutoReply.thisPath;

public class CommandLine extends JSimpleCommand {
    public static final CommandLine INSTANCE = new CommandLine();

    private CommandLine() {
        super(HPlugin_AutoReply.INSTANCE, "autoReply");
        setDescription("AutoReply 命令");
    }

    @Handler
    public void onCommand(String arg) {
        switch (arg) {
            case "reload":
                conf = FileInitializeValue.getValue(thisPath + "/conf/auto_reply/conf.json", conf, true);
                System.out.println("已重新加载配置");
                return;
        }
        if (arg.startsWith("setHost=")) {
            long q = Long.parseLong(arg.substring("setHost=".length()).trim());
            conf.host = q;
            conf.apply();
            System.out.println("设置主人为:" + q);
            return;
        }
        if (arg.startsWith("addFollower:")) {
            long q = Long.parseLong(arg.substring("addFollower:".length()).trim());
            conf.followers.add(q);
            conf.apply();
            System.out.println("添加follower:" + q);
            return;
        }
        System.err.println("reload\t#重新加载配置");
        System.err.println("setHost=1\t#设置主人qq");
        System.err.println("addFollower:1\t#添加 follower");
    }
}
