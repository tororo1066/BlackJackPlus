package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.CheckBet

class StartBJP : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!BlackJackPlus.pluginEnable){
            BlackJackPlus.sendMsg(sender,"§4は現在停止中です")
            return true
        }

        if (sender !is Player){
            BlackJackPlus.sendMsg(sender,"§4このコマンドはプレイヤーのみ実行できます")
            return true
        }

        val bet = args[1].toDouble()
        if (CheckBet(bet).call() == -1.0){
            BlackJackPlus.sendMsg(sender,"§4賭け金は${BlackJackPlus.BJPConfig.getDouble("price.min")}~" +
                    "${BlackJackPlus.BJPConfig.getDouble("price.max")}円の間で、負の数で入力しないでください")
            return true
        }

        if (BlackJackPlus.vault.getBalance(sender.uniqueId) < bet){
            BlackJackPlus.sendMsg(sender,"§4お金が足りません")
            return true
        }

        BlackJackPlus.bjpData[sender.uniqueId] = BJPGame()
        BlackJackPlus.bjpData[sender.uniqueId]!!.setGameConfig(BlackJackPlus.BJPConfig.getInt("gameconfig.round",3),BlackJackPlus.BJPConfig.getInt("gameconfig.clocktime",30))
        BlackJackPlus.bjpData[sender.uniqueId]!!.
        addPlayer(sender,bet,BlackJackPlus.BJPConfig.getInt("gameconfig.coin",10),BlackJackPlus.BJPConfig.getInt("gameconfig.bet",2),sender.uniqueId)
        BlackJackPlus.bjpData[sender.uniqueId]!!.start()
        return true
    }
}