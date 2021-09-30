package tororo1066.blackjackplus.command

import tororo1066.blackjackplus.Utils.SCommandRouter.SCommandArgument
import tororo1066.blackjackplus.Utils.SCommandRouter.SCommandArgumentType
import tororo1066.blackjackplus.Utils.SCommandRouter.SCommandObject
import tororo1066.blackjackplus.Utils.SCommandRouter.SCommandRouter
import tororo1066.blackjackplus.command.subcommands.*

class BJPCommand : SCommandRouter() {


    init {
        pluginPrefix = "§5§lBlackJack§a§lPlus"
        registerEvents()
        registerCommands()
    }

    private fun registerEvents(){
        setNoPermissionEvent { it.sender.sendMessage("§4権限がありません") }
        setOnNoCommandFoundEvent { it.sender.sendMessage("§4コマンドが存在しません") }
    }

    private fun registerCommands(){

        //bjp start [かけ金]
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("start")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.DOUBLE).addAlias("賭け金")).
        addRequiredPermission("bjp.user").addExplanation("BJPの部屋を作成する").
        setExecutor(StartBJP()))

        //bjp join [name]
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("join")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.ONLINE_PLAYER).addAlias("プレイヤー名")).
        addRequiredPermission("bjp.user").addExplanation("BJPの部屋に参加する").
        setExecutor(JoinBJP()))

        //bjp open
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("open")).
        addRequiredPermission("bjp.user").addExplanation("BJPのインベントリを開きなおす").
        setExecutor(OpenBJPMenu()))

        //bjp mode <true or false>
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("mode")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.BOOLEAN)).
        addRequiredPermission("bjp.op").addExplanation("BJPのmodeを切り替える").
        setExecutor(SwitchMode()))

        //bjp custom [かけ金] [ラウンド数] [チップ数] [初期ベット数] [1ターンの時間]
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("custom")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.DOUBLE).addAlias("賭け金")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.INT).addAlias("ラウンド数(1~10)")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.INT).addAlias("チップ数(5~30)")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.INT).addAlias("初期ベット数(1~10)")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.INT).addAlias("1ターンの時間(3~60)")).
        addRequiredPermission("bjp.custom").addExplanation("BJPの設定を細かく変えて部屋を作成する").
        setExecutor(CustomStartBJP()))

        //bjp reload
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("reload")).
        addRequiredPermission("bjp.op").addExplanation("configをreloadする").
        setExecutor(ReloadConfig()))


        //bjp createtables
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("createtables")).
        addRequiredPermission("bjp.op").addExplanation("tableを作成する").
        setExecutor(CreateTables()))

        //bjp wiki
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("wiki")).
        addRequiredPermission("bjp.user").addExplanation("wikiのリンクを出す").
        setExecutor(WikiCommand()))

        //bjp log
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("log")).
        addRequiredPermission("bjp.log").addExplanation("戦績を確認する").
        setExecutor(LogCommand()))

        //bjp logop <name>
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("logop")).
        addArgument(SCommandArgument().addAllowedType(SCommandArgumentType.STRING).addAlias("プレイヤー名")).
        addRequiredPermission("bjp.op").addExplanation("他のプレイヤーの戦績を確認する").
        setExecutor(LogOp()))


        //bjp ranking
        addCommand(SCommandObject().
        addArgument(SCommandArgument().addAllowedString("ranking")).
        addRequiredPermission("bjp.log").addExplanation("獲得賞金ランキングを見る").
        setExecutor(CollectRanking()))
    }


}