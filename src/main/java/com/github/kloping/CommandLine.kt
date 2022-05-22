package com.github.kloping

import com.github.kloping.Resource.conf
import com.github.kloping.Resource.loadIllegals
import io.github.kloping.number.NumberUtils
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.java.JCompositeCommand

class CommandLine private constructor() : JCompositeCommand(Plugin0AutoReply.INSTANCE, "autoReply") {

    @Description("设置主人")
    @SubCommand("setHost")
    suspend fun CommandSender.autoReplyM1(q: Long) {
        conf.setHost(q).apply()
        sendMessage("当前host=" + conf.host)
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

    @Description("重新加载配置")
    @SubCommand("reload")
    suspend fun CommandSender.autoReplyReload() {
        conf = Conf.reload(conf)
        loadIllegals()
        sendMessage(" Reloading the complete ")
    }

    companion object {
        @JvmField
        val INSTANCE = CommandLine()
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
        sendMessage(Resource.deleteA(s-1))
    }


    init {
        description = "AutoReply 命令"
    }
}