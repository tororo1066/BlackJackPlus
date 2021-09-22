package tororo1066.blackjackplus.command.subcommands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.blackjackplus.BlackJackPlus

//部屋参加 権限：bjp.user
class JoinBJP : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!BlackJackPlus.pluginEnable){
            BlackJackPlus.sendMsg(sender,"§4は現在停止中です")
            return true
        }

        if (sender !is Player){
            BlackJackPlus.sendMsg(sender,"§4このコマンドはプレイヤーのみ実行できます")
            return true
        }

        val uuid = Bukkit.getOfflinePlayer(args[1]).uniqueId

        if (!BlackJackPlus.bjpData.containsKey(uuid)){
            BlackJackPlus.sendMsg(sender,"§4ゲームが存在しません")
            return true
        }

        if (BlackJackPlus.bjpData[uuid]!!.playerData.size >= 2){
            BlackJackPlus.sendMsg(sender,"§4ゲームが一杯です")
            return true
        }

        for (data in BlackJackPlus.bjpData){
            if (data.value.playerData.containsKey(sender.uniqueId)){
                BlackJackPlus.sendMsg(sender,"§4ゲームに参加中です")
                return true
            }
        }

        if (BlackJackPlus.vault.getBalance(sender.uniqueId) < BlackJackPlus.bjpData[uuid]!!.getNeedMoney()){
            BlackJackPlus.sendMsg(sender,"§4お金が足りません")
            return true
        }

        BlackJackPlus.vault.withdraw(sender.uniqueId,BlackJackPlus.bjpData[uuid]!!.getNeedMoney())

        BlackJackPlus.bjpData[uuid]!!.addJoinPlayer(sender)

        return true
    }
}