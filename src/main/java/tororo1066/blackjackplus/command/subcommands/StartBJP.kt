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

        for (data in BlackJackPlus.bjpData){
            if (data.value.playerData.containsKey(sender.uniqueId)){
                BlackJackPlus.sendMsg(sender,"§4ゲームに参加中です")
                return true
            }
        }

        val bet = CheckBet(args[1].toDouble()).call()
        if (bet == -1.0){
            BlackJackPlus.sendMsg(sender,"§4賭け金は${BlackJackPlus.BJPConfig.getDouble("price.min")}~" +
                    "${BlackJackPlus.BJPConfig.getDouble("price.max")}円の間で、負の数で入力しないでください")
            return true
        }

        val coin = BlackJackPlus.BJPConfig.getInt("gameconfig.coin",10)


        if (BlackJackPlus.vault.getBalance(sender.uniqueId)*coin < bet){
            BlackJackPlus.sendMsg(sender,"§4お金が足りません")
            return true
        }



        BlackJackPlus.vault.withdraw(sender.uniqueId,bet*coin)

        BlackJackPlus.bjpData[sender.uniqueId] = BJPGame()
        BlackJackPlus.bjpData[sender.uniqueId]!!.setGameConfig(BlackJackPlus.BJPConfig.getInt("gameconfig.round",3),BlackJackPlus.BJPConfig.getInt("gameconfig.clocktime",30))
        BlackJackPlus.bjpData[sender.uniqueId]!!.
        addPlayer(sender,bet,coin,BlackJackPlus.BJPConfig.getInt("gameconfig.bet",2),sender.uniqueId)
        BlackJackPlus.bjpData[sender.uniqueId]!!.start()
        return true
    }
}