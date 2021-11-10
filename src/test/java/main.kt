import com.hrs.kloping.HPlugin_AutoReply
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader

suspend fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon()

    HPlugin_AutoReply.INSTANCE.apply {
        load()
        enable()
    }

    val bot = MiraiConsole.addBot(0,"").alsoLogin() // 登录一个测试环境的 Bot

    MiraiConsole.job.join()
}
