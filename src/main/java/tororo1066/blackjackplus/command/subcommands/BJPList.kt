package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus

class BJPList : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!BlackJackPlus.pluginEnable){
            BlackJackPlus.sendMsg(sender,"§4は現在停止中です")
            return true
        }

        if (sender !is Player){
            BlackJackPlus.sendMsg(sender,"§4このコマンドはプレイヤーのみ実行できます")
            return true
        }

        val nonFull = ArrayList<BJPGame>()
        val full = ArrayList<BJPGame>()

        for (list in BlackJackPlus.bjpData){
            if (list.value.playerData.size == 2) full.add(list.value) else nonFull.add(list.value)
        }

        sender.sendMessage("§a募集中の部屋")
        for (game in nonFull){
            val data = game.playerData.entries.first().value
            sender.sendMessage("§c${data.mcid}§f ： §e必要金額 ${data.onetip * data.initialcoin}円\n" +
                    "§b部屋設定：チップ一枚当たりの賭け金：${data.onetip}円 ラウンド数：${game.round}回 チップ数：${data.initialcoin}枚 初期ベット数：${data.initialbet}枚 1ターンの時間：${game.clocktime}秒")
        }

        sender.sendMessage("§7既に埋まっている部屋")

        for (game in full){
            val data = game.playerData.entries.first().value
            sender.sendMessage("§c${data.mcid}§f vs §c${game.playerData.entries.last().value.mcid}§f ： §e必要金額 ${data.onetip * data.initialcoin}円\n" +
                    "§b部屋設定：チップ一枚当たりの賭け金：${data.onetip}円 ラウンド数：${game.round}回 チップ数：${data.initialcoin}枚 初期ベット数：${data.initialbet}枚 1ターンの時間：${game.clocktime}秒")
        }

        return true
    }
}