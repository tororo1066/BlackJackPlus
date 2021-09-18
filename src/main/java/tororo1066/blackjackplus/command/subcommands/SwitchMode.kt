package tororo1066.blackjackplus.command.subcommands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import tororo1066.blackjackplus.BlackJackPlus

class SwitchMode : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (args[1].toBoolean()){
            if (BlackJackPlus.pluginEnable){
                BlackJackPlus.sendMsg(sender,"§4すでにonになっています")
                return true
            }

            BlackJackPlus.BJPConfig.set("mode",true)
            BlackJackPlus.plugin.saveConfig()
            BlackJackPlus.pluginEnable = true

            BlackJackPlus.sendMsg(sender,"§bmodeをtrueにしました")

        }else{
            if (!BlackJackPlus.pluginEnable){
                BlackJackPlus.sendMsg(sender,"§4すでにoffになっています")
                return true
            }

            BlackJackPlus.BJPConfig.set("mode",false)
            BlackJackPlus.plugin.saveConfig()
            BlackJackPlus.pluginEnable = false

            BlackJackPlus.sendMsg(sender,"§bmodeをfalseにしました")

        }
        return true
    }
}