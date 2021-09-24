package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.blackjackplus.BlackJackPlus

//menuopen 権限：bjp.user
class OpenBJPMenu : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!BlackJackPlus.pluginEnable){
            BlackJackPlus.sendMsg(sender,"§4は現在停止中です")
            return true
        }

        if (sender !is Player){
            BlackJackPlus.sendMsg(sender,"§4このコマンドはプレイヤーのみ実行できます")
            return true
        }
        for (data in BlackJackPlus.bjpData){
            if (data.value.playerData.containsKey(sender.uniqueId)){
                return try {
                    data.value.playerData[sender.uniqueId]!!.inv.open(sender)
                    true
                }catch (e : UninitializedPropertyAccessException){
                    true
                }
            }

            BlackJackPlus.sendMsg(sender,"§4データが存在しません")
            return true
        }
        return true
    }
}