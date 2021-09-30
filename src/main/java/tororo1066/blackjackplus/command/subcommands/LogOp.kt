package tororo1066.blackjackplus.command.subcommands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.BJPLog

class LogOp : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val log = BJPLog().getLog(Bukkit.getOfflinePlayer(args[1]).uniqueId)
        if (!log.success){
            BlackJackPlus.sendMsg(sender,"§4ログの取得に失敗しました")
            return true
        }

        sender.sendMessage(BJPLog().logFormat(log))
        return true
    }
}