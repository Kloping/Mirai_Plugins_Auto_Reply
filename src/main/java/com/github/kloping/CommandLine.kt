package com.github.kloping

import com.github.kloping.Resource.conf
import com.github.kloping.Resource.loadIllegals
import com.github.kloping.cron.Work
import io.github.kloping.number.NumberUtils
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.ConsoleCommandSender
import net.mamoe.mirai.console.command.ConsoleCommandSender.sendMessage
import net.mamoe.mirai.console.command.FriendCommandSenderOnMessage
import net.mamoe.mirai.console.command.MemberCommandSender
import net.mamoe.mirai.console.command.java.JCompositeCommand

class CommandLine private constructor() : JCompositeCommand(Plugin0AutoReply.INSTANCE, "autoReply") {
    companion object {
        @JvmField
        val INSTANCE = CommandLine()
    }

    init {
        description = "AutoReply 命令"
    }

    @Description("改变auto_reply所处环境的开关状态")
    @SubCommand("changeState")
    suspend fun CommandSender.autoReplyChangeState() {
        if (this is MemberCommandSender) {
            val sender: MemberCommandSender = this;
            val gid = sender.group.id
            conf.map["g$gid"] = !conf.map.getOrDefault("g$gid", false);
            conf.apply()
            sendMessage("该群($gid)开启状态:" + (if (conf.map["g$gid"] == true) "开启" else "关闭"))
        } else if (this is FriendCommandSenderOnMessage) {
            val sender: FriendCommandSenderOnMessage = this;
            val sid = sender.subject.id
            conf.map["f$sid"] = !conf.map.getOrDefault("f$sid", false);
            conf.apply()
            sendMessage("该好友($sid)开启状态:" + (if (conf.map["f$sid"] == true) "开启" else "关闭"))
        } else if (this is ConsoleCommandSender) {
            val sid = "-1"
            conf.map[sid] = !conf.map.getOrDefault(sid, false);
            if (!conf.map[sid]!!) {
                conf.map.remove(sid);
            }
            conf.apply()
            sendMessage("全局开启状态:" + (if (conf.map[sid] == true) "开启" else "关闭"))
        }
    }

    @Description("改变auto_reply开关状态")
    @SubCommand("changeState0")
    suspend fun CommandSender.autoReplyChangeState0(@Name("id") id: String) {
        conf.map[id] = !conf.map.getOrDefault(id, false);
        conf.apply()
        sendMessage("该($id)开启状态:" + (if (conf.map[id] == true) "开启" else "关闭"))
    }

    var k = false;

    @Description("改变auto_reply所有开关状态")
    @SubCommand("changeState1")
    suspend fun CommandSender.autoReplyChangeState1() {
        k = !k;
        for (mutableEntry in conf.map) {
            conf.map[mutableEntry.key] = k;
        }
        conf.apply()
        sendMessage("所有环境开启状态:" + (if (k) "开启" else "关闭"))
    }

    @Description("设置主人")
    @SubCommand("setHost")
    suspend fun CommandSender.autoReplyM1(q: Long) {
        conf.setHost(q).apply()
        sendMessage("当前host=" + conf.host)
    }

    @Description("设置CD(单位秒)")
    @SubCommand("setCd")
    suspend fun CommandSender.autoReplySetCd(cd: Float) {
        conf.setCd(cd).apply()
        sendMessage("当前cd=${conf.cd}秒")
    }

    @Description("改变自定义回复私信开关状态")
    @SubCommand("changePk")
    suspend fun CommandSender.autoReplyChangePk() {
        conf.setPrivateK(!conf.isPrivateK).apply()
        sendMessage("当前私信回复属于" + (if (conf.isPrivateK) "开启状态" else "关闭状态"))
    }

    @Description("添加follower")
    @SubCommand("addF")
    suspend fun CommandSender.autoReplyM2(qs: String) {
        val q = java.lang.Long.parseLong(NumberUtils.findNumberFromString(qs))
        conf.addF(q).apply()
        sendMessage("当前 follower=\n" + conf.followers)
    }

    @Description("添加deleter")
    @SubCommand("addD")
    suspend fun CommandSender.autoReplyM3(qs: String) {
        val q = java.lang.Long.parseLong(NumberUtils.findNumberFromString(qs))
        conf.addC(q).apply()
        sendMessage("当前 delete=\n" + (conf.deletes))
    }

    @Description("移除follower")
    @SubCommand("removeF")
    suspend fun CommandSender.autoReplyRemoveF(qs: String) {
        val q = java.lang.Long.parseLong(NumberUtils.findNumberFromString(qs))
        conf.removeF(q).apply()
        sendMessage("当前 follower=\n" + conf.followers)
    }

    @Description("移除deleter")
    @SubCommand("removeD")
    suspend fun CommandSender.autoReplyRemoveD(qs: String) {
        val q = java.lang.Long.parseLong(NumberUtils.findNumberFromString(qs))
        conf.removeC(q).apply()
        sendMessage("当前 delete=\n" + (conf.deletes))
    }

    @Description("重新加载配置")
    @SubCommand("reload")
    suspend fun CommandSender.autoReplyReload() {
        conf = Conf.reload(conf)
        loadIllegals()
        sendMessage(" Reloading the complete ")
    }

    @Description("添加一个定时任务")
    @SubCommand("addA")
    suspend fun CommandSender.addA(@Name("时间") t: String, @Name("ID") qid: String, @Name("内容") content: String) {
        sendMessage(Resource.addA(t, qid, content))
    }

    @Description("列出所有定时任务")
    @SubCommand("listA")
    suspend fun CommandSender.listA() {
        sendMessage(Resource.listA())
    }

    @Description("删除一个定时任务")
    @SubCommand("deleteA")
    suspend fun CommandSender.deleteA(@Name("序号") s: Int) {
        sendMessage(Resource.deleteA(s - 1))
    }

    @Description("cron定时任务的添加")
    @SubCommand("cronAdd")
    suspend fun CommandSender.cronAdd(
        @Name("ID") id: String,
        @Name("内容miraicode") code: String,
        @Name("cron表达式") vararg cron: String
    ) {
        val cron0 = cron.joinToString(" ")
        sendMessage(Work.add(cron0.trim(), id, code))
    }

    @Description("cron定时任务的删除")
    @SubCommand("cronDelete")
    suspend fun CommandSender.cronDelete(@Name("list中的序号id") cron: Int) {
        sendMessage(Work.delete(cron))
    }

    @Description("cron定时任务的列表")
    @SubCommand("cronList")
    suspend fun CommandSender.cronList() {
        sendMessage(Work.list())
    }
}