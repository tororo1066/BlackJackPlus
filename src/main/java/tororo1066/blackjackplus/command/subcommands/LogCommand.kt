package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.BJPLog

class LogCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!BlackJackPlus.pluginEnable){
            BlackJackPlus.sendMsg(sender,"§4は現在停止中です")
            return true
        }

        if (sender !is Player){
            BlackJackPlus.sendMsg(sender,"§4このコマンドはプレイヤーのみ実行できます")
            return true
        }

        val log = BJPLog().getLog(sender.uniqueId)
        if (!log.success){
            BlackJackPlus.sendMsg(sender,"§4ログの取得に失敗しました")
            return true
        }

        sender.sendMessage(BJPLog().logFormat(log))
        return true
    }
}