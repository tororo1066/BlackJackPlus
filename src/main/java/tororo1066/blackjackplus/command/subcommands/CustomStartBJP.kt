package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.CheckBet

//カスタム部屋作成 権限：bjp.custom
class CustomStartBJP : CommandExecutor {
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

        val round = args[2].toInt()
        val coin = args[3].toInt()
        val bet = args[4].toInt()
        val time = args[5].toInt()

        if (round !in 1..10 || coin !in 5..30 || bet !in 1..10 || time !in 3..60){
            BlackJackPlus.sendMsg(sender,"§a/bjp custom [チップ1枚当たりの金額] [ラウンド数(1~10)] [チップ数(5~30)] [初期ベット数(1~10)] [1ターンの時間(3~60)]")
        }

        val tip = CheckBet(args[1].toDouble()).call()
        if (tip == -1.0){
            BlackJackPlus.sendMsg(sender,"§4賭け金は${BlackJackPlus.BJPConfig.getDouble("price.min")}~" +
                    "${BlackJackPlus.BJPConfig.getDouble("price.max")}円の間で、負の数で入力しないでください")
            return true
        }



        if (BlackJackPlus.vault.getBalance(sender.uniqueId) < tip*coin){
            BlackJackPlus.sendMsg(sender,"§4お金が足りません")
            return true
        }



        BlackJackPlus.vault.withdraw(sender.uniqueId,tip*coin)

        BlackJackPlus.bjpData[sender.uniqueId] = BJPGame()
        BlackJackPlus.bjpData[sender.uniqueId]!!.setGameConfig(round,time)
        BlackJackPlus.bjpData[sender.uniqueId]!!.
        addPlayer(sender,tip,coin, bet,sender.uniqueId)
        BlackJackPlus.bjpData[sender.uniqueId]!!.start()
        return true
    }
}