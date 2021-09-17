package tororo1066.blackjackplus

import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.blackjackplus.Utils.VaultAPI
import tororo1066.blackjackplus.bjputlis.Cards
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BlackJackPlus : JavaPlugin() {

    companion object{
        val bjpData = HashMap<UUID,BJPGame>()

        var pluginEnable = false
        var pluginPrefix = "§f[§5§lBJ§a§lP§f]§r"
        lateinit var BJPConfig : FileConfiguration
        lateinit var plugin : BlackJackPlus
        lateinit var vault : VaultAPI

        val enableSpCards = HashMap<Int,Int>()
        val cardCSM = ArrayList<Int>()
        var invisibleCardCSM = 0

        fun format(double: Double):String{
            return String.format("%,.0f",double)
        }

        fun sendMsg(p : Player, msg : String){
            p.sendMessage(pluginPrefix + msg)
        }

        fun sendMsg(p : CommandSender, msg : String){
            p.sendMessage(pluginPrefix + msg)
        }
    }

    override fun onEnable() {
        saveDefaultConfig()
        BJPConfig = config
        vault = VaultAPI()
        plugin = this

        var loop = 1
        while (config.isSet("cardconfig.spcards.$loop")){
            if (config.getBoolean("cardconfig.spcards.$loop.enable")){
                enableSpCards[loop] = config.getInt("cardconfig.spcards.$loop.csm")
            }
            loop++
        }

        if (enableSpCards.isEmpty()){
            enableSpCards[1] = 0
        }

        for (card in config.getIntegerList("cardconig.cardcsm")){
            cardCSM.add(card)
        }

        invisibleCardCSM = config.getInt("cardconfig.invisiblecsm")

    }
}