package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.blackjackplus.BlackJackPlus

//コンフィグリロード 権限：bjp.op
class ReloadConfig : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (BlackJackPlus.bjpData.isNotEmpty()){
            BlackJackPlus.sendMsg(sender,"§4ゲーム中のプレイヤーがいます")
            return true
        }
        BlackJackPlus.reloadBJPConfig()
        BlackJackPlus.sendMsg(sender,"§dリロードが完了しました")
        return true
    }
}