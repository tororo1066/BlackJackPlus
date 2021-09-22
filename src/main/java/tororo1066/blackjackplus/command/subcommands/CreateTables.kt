package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.blackjackplus.BlackJackPlus

//テーブル作成 権限：bjp.op
class CreateTables : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        BlackJackPlus.createTables()
        BlackJackPlus.sendMsg(sender,"テーブルを作成しました")
        return true
    }
}