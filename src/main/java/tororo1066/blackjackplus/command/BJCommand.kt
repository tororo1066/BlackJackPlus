package tororo1066.blackjackplus.command

import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SCommandRouter.SCommandArgument
import tororo1066.blackjackplus.Utils.SCommandRouter.SCommandArgumentType
import tororo1066.blackjackplus.Utils.SCommandRouter.SCommandObject
import tororo1066.blackjackplus.Utils.SCommandRouter.SCommandRouter
import tororo1066.blackjackplus.command.subcommands.JoinBJP
import tororo1066.blackjackplus.command.subcommands.StartBJP

class BJCommand : SCommandRouter() {


    init {
        pluginPrefix = BlackJackPlus.pluginPrefix
        registerEvents()
        registerCommands()
    }

    fun registerEvents(){
        setNoPermissionEvent { it.sender.sendMessage("§4権限がありません") }
        setOnNoCommandFoundEvent { it.sender.sendMessage("§4コマンドが存在しません") }
    }

    fun registerCommands(){

        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("start")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.DOUBLE).addAlias("賭け金")).
        addRequiredPermission("bjp.user").addExplanation("BJPの部屋を作成する").
        setExecutor(StartBJP()))

        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("join")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.ONLINE_PLAYER).addAlias("プレイヤー名")).
        addRequiredPermission("bjp.user").addExplanation("BJPの部屋に参加する").
        setExecutor(JoinBJP()))
    }


}