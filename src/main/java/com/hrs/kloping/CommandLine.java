package com.hrs.kloping;

import io.github.kloping.initialize.FileInitializeValue;
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
    public String onCommand(String arg) {
        switch (arg) {
            case "reload":
                conf = FileInitializeValue.getValue(thisPath + "/conf/auto_reply/conf.json", conf, true);
                System.out.println("已重新加载配置");
                return "已重新加载配置";
        }
        if (arg.startsWith("setHost=")) {
            long q = Long.parseLong(arg.substring("setHost=".length()).trim());
            conf.host = q;
            conf.apply();
            System.out.println("设置主人为:" + q);
            return "设置主人为:" + q;
        }
        if (arg.startsWith("addFollower:")) {
            long q = Long.parseLong(arg.substring("addFollower:".length()).trim());
            conf.followers.add(q);
            conf.apply();
            System.out.println("添加follower:" + q);
            return "添加follower:" + q;
        }
        System.err.println(tips);
        return tips;
    }

    private static final String tips = "reload\t#重新加载配置\n" +
            "setHost=1\t#设置主人qq\n" +
            "addFollower:1\t#添加 follower";
}
