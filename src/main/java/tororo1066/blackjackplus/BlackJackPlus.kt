package tororo1066.blackjackplus

import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.blackjackplus.Utils.MySQL.ThreadedMySQLAPI
import tororo1066.blackjackplus.Utils.VaultAPI
import tororo1066.blackjackplus.bjputlis.BJPListener
import tororo1066.blackjackplus.command.BJPCommand
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BlackJackPlus : JavaPlugin() {

    companion object{
        val bjpData = HashMap<UUID,BJPGame>()

        var pluginEnable = true
        var pluginPrefix = "§f[§5§lBJ§a§lP§f]§r"
        lateinit var BJPConfig : FileConfiguration
        lateinit var plugin : BlackJackPlus
        lateinit var vault : VaultAPI
        lateinit var mysql : ThreadedMySQLAPI

        val enableSpCards = HashMap<Int,Int>()
        val cardCSM = ArrayList<Int>()
        val drawAnyCSM = ArrayList<Int>()
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


        //コンフィグリロード /bjp reload
        fun reloadBJPConfig(){
            plugin.saveConfig()
            var loop = 1
            while (BJPConfig.isSet("cardconfig.spcards.$loop")){
                if (BJPConfig.getBoolean("cardconfig.spcards.$loop.enable")){
                    enableSpCards[loop] = BJPConfig.getInt("cardconfig.spcards.$loop.csm")
                }
                loop++
            }

            if (enableSpCards.isEmpty()){
                enableSpCards[1] = 0
            }

            for (card in BJPConfig.getIntegerList("cardconfig.cardcsm")){
                cardCSM.add(card)
            }

            invisibleCardCSM = BJPConfig.getInt("cardconfig.invisiblecsm")

            if (BJPConfig.getIntegerList("cardconfig.draw").size != 5){
                drawAnyCSM.addAll(mutableListOf(0,0,0,0,0))
            }else{
                drawAnyCSM.addAll(BJPConfig.getIntegerList("cardconfig.draw"))
            }
        }

        //db作成 /bjp createtables
        fun createTables(): Boolean {
            if (!mysql.execute("CREATE TABLE IF NOT EXISTS `bjp_player_log` (\n" +
                        "\t`id` INT(10) NOT NULL AUTO_INCREMENT,\n" +
                        "\t`uuid` VARCHAR(36) NULL DEFAULT NULL,\n" +
                        "\t`mcid` VARCHAR(16) NULL DEFAULT NULL,\n" +
                        "\t`win` INT(10) NULL DEFAULT NULL,\n" +
                        "\t`draw` INT(10) NULL DEFAULT NULL,\n" +
                        "\t`lose` INT(10) NULL DEFAULT NULL,\n" +
                        "\t`collect` DOUBLE NULL DEFAULT NULL,\n" +
                        "\tPRIMARY KEY (`id`) USING BTREE,\n" +
                        "\tINDEX `uuid` (`uuid`) USING BTREE,\n" +
                        "\tINDEX `mcid` (`mcid`) USING BTREE\n" +
                        ")\n" +
                        ";\n")) return false
            if (!mysql.execute("CREATE TABLE IF NOT EXISTS `bjp_log` (\n" +
                        "\t`id` INT(10) NOT NULL AUTO_INCREMENT,\n" +
                        "\t`startUUID` VARCHAR(36) NULL DEFAULT NULL,\n" +
                        "\t`startMCID` VARCHAR(16) NULL DEFAULT NULL,\n" +
                        "\t`joinUUID` VARCHAR(36) NULL DEFAULT NULL,\n" +
                        "\t`joinMCID` VARCHAR(16) NULL DEFAULT NULL,\n" +
                        "\t`tip` DOUBLE NULL DEFAULT NULL,\n" +
                        "\t`round` INT(10) NULL DEFAULT NULL,\n" +
                        "\t`coin` INT(10) NULL DEFAULT NULL,\n" +
                        "\t`bet` INT(10) NULL DEFAULT NULL,\n" +
                        "\t`time` INT(10) NULL DEFAULT NULL,\n" +
                        "\t`startCoin` INT(10) NULL DEFAULT NULL,\n" +
                        "\t`joinCoin` INT(10) NULL DEFAULT NULL,\n" +
                        "\tPRIMARY KEY (`id`) USING BTREE\n" +
                        ")\n" +
                        ";\n")) return false

            return true
        }
    }

    override fun onEnable() {
        saveDefaultConfig()
        BJPConfig = config
        vault = VaultAPI()
        mysql = ThreadedMySQLAPI(this)
        plugin = this

        getCommand("bjp")?.setExecutor(BJPCommand())
        getCommand("bjp")?.tabCompleter = BJPCommand()
        server.pluginManager.registerEvents(BJPListener(),this)

        reloadBJPConfig()

    }






}