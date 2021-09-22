package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.blackjackplus.BlackJackPlus

//wikiコマンド 権限：bjp.user
class WikiCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        BlackJackPlus.sendMsg(sender,"§5§lBJP§b§lWiki: https://man10.red/wiki/doku.php?id=bjp")
        return true
    }
}