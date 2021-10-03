package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.blackjackplus.BlackJackPlus

//テーブル作成 権限：bjp.op
class CreateTables : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (BlackJackPlus.createTables()){
            BlackJackPlus.sendMsg(sender,"§aテーブルを作成しました")
        }else{
            BlackJackPlus.sendMsg(sender,"§4テーブルの作成に失敗しました")
        }

        return true
    }
}