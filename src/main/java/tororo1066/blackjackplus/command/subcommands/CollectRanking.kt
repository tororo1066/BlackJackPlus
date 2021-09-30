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

        val str = SStringBuilder()
        for ((index,rank) in ranking.withIndex()){
            str.gray().bold().text("${index+1}.").aqua().text("${rank.key}：").yellow().text("${BlackJackPlus.format(rank.value)}円\n")
        }
        sender.sendMessage("§a§l総獲得賞金ランキング\n" +
                str.build()
        )
        return true
    }
}