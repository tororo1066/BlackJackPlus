package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SStringBuilder
import tororo1066.blackjackplus.bjputlis.BJPLog

class CollectRanking : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val ranking = BJPLog().collectRanking()
        if (ranking.isEmpty()){
            BlackJackPlus.sendMsg(sender,"§4ログが取得できませんでした")
            return true
        }

        sender.sendMessage("§a§l総獲得賞金ランキング")
        for ((index,rank) in ranking.withIndex()){
            sender.sendMessage("§7§l${index+1}.§b${rank.key}：§e${BlackJackPlus.format(rank.value)}円")
        }
        return true
    }
}