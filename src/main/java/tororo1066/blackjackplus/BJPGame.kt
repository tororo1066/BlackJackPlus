package tororo1066.blackjackplus

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.checkerframework.checker.nullness.qual.NonNull
import tororo1066.blackjackplus.Utils.MySQL.MySQLAPI
import tororo1066.blackjackplus.Utils.SInventory.SInventory
import tororo1066.blackjackplus.Utils.SInventory.SInventoryItem
import tororo1066.blackjackplus.Utils.SItemStack
import tororo1066.blackjackplus.bjputlis.Cards
import tororo1066.blackjackplus.bjputlis.CreateGUI
import tororo1066.blackjackplus.bjputlis.OtherItem
import tororo1066.blackjackplus.bjputlis.spcards.SpCard
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.random.Random
import kotlin.random.nextInt

class BJPGame : Thread() {

    val playerData = LinkedHashMap<UUID,PlayerData>()

    var round = 3
    var clocktime = 30

    class PlayerData{
        lateinit var uuid : UUID
        var mcid = ""
        var onetip = 0.0
        var initialcoin = 0
        var coin = 0
        var initialbet = 0
        var bet = 0
        var bjnumber = 21

        var action : Action = Action.Nothing
        var through = false

        var canSpUse = true
        var harvest = false
        var death = false

        lateinit var inv : SInventory



        lateinit var starter : UUID
        lateinit var enemy : UUID



        enum class Action{
            DRAW,
            THROUGH,
            SPUSE,
            Nothing
        }

    }

    //ラウンド、時計設定
    fun setGameConfig(round : Int, clocktime : Int){
        this.round = round
        this.clocktime = clocktime
    }

    //プレイヤー追加
    fun addPlayer(p : Player, onetip : Double, coin : Int, initialbet : Int, starter : UUID){
        val data = PlayerData()
        data.uuid = p.uniqueId
        data.mcid = p.name
        data.onetip = onetip
        data.coin = coin
        data.initialcoin = coin
        data.initialbet = initialbet
        data.bet = initialbet
        data.starter = starter
        playerData[p.uniqueId] = data
    }

    //参加側のプレイヤー追加処理
    fun addJoinPlayer(p : Player){
        val data = playerData.entries.first().value
        data.enemy = p.uniqueId
        addPlayer(p,data.onetip,data.coin,data.initialbet,data.starter)
        playerData.entries.last().value.enemy = data.uuid
    }

    //ぶろーどきゃすと
    private fun broadcast(component : Component){
        Bukkit.broadcast(component, Server.BROADCAST_CHANNEL_USERS)
    }

    //コマンドを実行できるテキストを作る
    private fun runCmd(s : String, cmd : String, hover : String): @NonNull Component {
        return text(s).clickEvent(ClickEvent.runCommand(cmd)).hoverEvent(HoverEvent.showText(text(hover))).asComponent()
    }

    //部屋のプレイヤー全員にメッセージを送る
    fun allPlayerSend(msg : String){
        for (p in playerData.keys){
            Bukkit.getPlayer(p)?.let { BlackJackPlus.sendMsg(it,msg) }
        }
    }

    //部屋のプレイヤー全員に音を送る
    fun allPlaySound(sound : Sound, volume : Float, pitch : Float){
        for (p in playerData.keys){
            Bukkit.getPlayer(p)?.location?.let { Bukkit.getPlayer(p)?.playSound(it,sound,volume,pitch) }
        }
    }

    //runTask
    private fun runTask(unit : ()->Unit){
        Bukkit.getScheduler().runTask(BlackJackPlus.plugin, Runnable {
            unit.invoke()
            return@Runnable
        })
    }

    //inv読み込み
    fun renderInventory(){
        if (playerData.size != 2)return
        playerData.entries.first().value.inv.renderInventory()
        playerData.entries.last().value.inv.renderInventory()
    }

    //カードを引く、カードを引かないをせっと
    private fun fillAction(uuid: UUID){
        if (!playerData.containsKey(uuid))return
        playerData[uuid]!!.inv.setItem(53,OtherItem.drawCardButton(playerData[uuid]!!))
        playerData[uuid]!!.inv.setItem(52,OtherItem.noDrawCardButton(playerData[uuid]!!))
        playerData[uuid]!!.inv.renderInventory()
    }

    //↑のを取り除く
    private fun replaceAction(uuid : UUID){
        if (!playerData.containsKey(uuid))return
        playerData[uuid]!!.inv.setItem(53, ItemStack(Material.AIR))
        playerData[uuid]!!.inv.setItem(52, ItemStack(Material.AIR))
        playerData[uuid]!!.inv.renderInventory()
    }

    //必要な金額の取得
    fun getNeedMoney(): Double {
        val data = playerData.entries.first().value
        return data.initialcoin * data.onetip
    }

    //ベット状況の更新
    fun betUpdate(){
        if (playerData.size != 2)return

        val data = playerData.entries.first().value
        val enemyData = playerData.entries.last().value

        if (data.bet < 0){
            data.inv.setItem(9,OtherItem.betNugget(data.mcid,0,data.coin))
            enemyData.inv.setItem(26,OtherItem.betNugget(data.mcid,0,data.coin))
        }else{
            data.inv.setItem(9,OtherItem.betNugget(data.mcid,data.bet,data.coin))
            enemyData.inv.setItem(26,OtherItem.betNugget(data.mcid,data.bet,data.coin))
        }

        if (enemyData.bet < 0){
            data.inv.setItem(26,OtherItem.betNugget(enemyData.mcid,0,enemyData.coin))
            enemyData.inv.setItem(9,OtherItem.betNugget(enemyData.mcid,0,enemyData.coin))
        }else{
            data.inv.setItem(26,OtherItem.betNugget(enemyData.mcid,enemyData.bet,enemyData.coin))
            enemyData.inv.setItem(9,OtherItem.betNugget(enemyData.mcid,enemyData.bet,enemyData.coin))
        }

        renderInventory()
    }

    //カードを数える 募集者がfirstで参加者がsecond
    fun countCard(): Pair<Int, Int> {
        if (playerData.size != 2)return Pair(0,0)
        val startData = playerData.entries.first().value
        val joinData = playerData.entries.last().value

        var count = 0
        for (i in 28..35){
            if (startData.inv.getItem(i) == null)continue
            count += startData.inv.getItem(i).itemStack.itemMeta.persistentDataContainer[NamespacedKey(BlackJackPlus.plugin,"card"), PersistentDataType.INTEGER]!!
        }
        var count2 = 0

        for (i in 28..35){
            if (joinData.inv.getItem(i) == null)continue
            count2 += joinData.inv.getItem(i).itemStack.itemMeta.persistentDataContainer[NamespacedKey(BlackJackPlus.plugin,"card"), PersistentDataType.INTEGER]!!
        }

        return Pair(count,count2)
    }

    //頭にカードの合計を反映させる
    private fun showCardSum(){

        val sum = countCard()
        if (sum == Pair(0,0))return
        val startData = playerData.entries.first().value
        val joinData = playerData.entries.last().value

        val startCard = startData.inv.getItem(28)!!.itemStack.itemMeta.persistentDataContainer[NamespacedKey(BlackJackPlus.plugin,"card"), PersistentDataType.INTEGER]!!
        val joinCard = joinData.inv.getItem(28)!!.itemStack.itemMeta.persistentDataContainer[NamespacedKey(BlackJackPlus.plugin,"card"), PersistentDataType.INTEGER]!!

        val startHead = SInventoryItem(SItemStack(Material.PLAYER_HEAD).setHeadOwner(startData.uuid).setDisplayName("§e${startData.mcid}の合計数字 ${sum.first} / ${startData.bjnumber}").build()).clickable(false)
        val joinHead = SInventoryItem(SItemStack(Material.PLAYER_HEAD).setHeadOwner(joinData.uuid).setDisplayName("§e${joinData.mcid}の合計数字 ${sum.second} / ${startData.bjnumber}").build()).clickable(false)
        val startNullHead = SInventoryItem(SItemStack(Material.PLAYER_HEAD).setHeadOwner(startData.uuid).setDisplayName("§e${startData.mcid}の合計数字 ${sum.first-startCard} + ? / ${startData.bjnumber}").build()).clickable(false)
        val joinNullHead = SInventoryItem(SItemStack(Material.PLAYER_HEAD).setHeadOwner(joinData.uuid).setDisplayName("§e${joinData.mcid}の合計数字 ${sum.second-joinCard} + ? / ${joinData.bjnumber}").build()).clickable(false)
        startData.inv.setItem(27,startHead)
        startData.inv.setItem(8,joinNullHead)
        joinData.inv.setItem(27,joinHead)
        joinData.inv.setItem(8,startNullHead)

        renderInventory()
    }

    //時間のカウント
    private fun timeCount(time : Int){
        if (playerData.size != 2)return
        val clock = SInventoryItem(SItemStack(Material.CLOCK).setDisplayName("§6残り時間").setAmount(time).build()).clickable(false)
        val startData = playerData.entries.first().value
        val joinData = playerData.entries.last().value

        startData.inv.setItem(17,clock)
        joinData.inv.setItem(17,clock)

        allPlaySound(Sound.UI_BUTTON_CLICK,1f,2f)

        renderInventory()
    }

    //二人のターンが終わったときにする処理 trueで募集者、falseで参加者の勝利、nullで引き分け
    private fun endTwoTurn(): Boolean? {
        if (playerData.size != 2)return null
        val count = countCard()
        val startCount = count.first
        val joinCount = count.second

        val startData = playerData.entries.first().value
        val joinData = playerData.entries.last().value

        if (startCount == joinCount)return null

        if (startCount > startData.bjnumber && joinCount > joinData.bjnumber)return null

        if (startCount > startData.bjnumber)return false

        if (joinCount > joinData.bjnumber)return true

        return startCount > joinCount
    }

    //指定したプレイヤーを勝利にする
    private fun win(uuid: UUID){
        if (playerData.size != 2)return

        val startData = playerData.entries.first().value
        val joinData = playerData.entries.last().value
        val count = countCard()

        val inv = Bukkit.createInventory(null,54, text("BJPResult"))
        val item = SItemStack(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§6§l${playerData[uuid]!!.mcid}の勝利！").
        setLore(mutableListOf("§e${startData.mcid}の合計：${count.first}","§e${joinData.mcid}の合計：${count.second}")).build()
        for (i in 0..53){
            inv.setItem(i,item)
        }

        runTask {
            Bukkit.getPlayer(startData.uuid)?.openInventory(inv)
            Bukkit.getPlayer(joinData.uuid)?.openInventory(inv)
        }

        sleep(5000)

    }

    //引き分けにする
    private fun draw(){
        if (playerData.size != 2)return

        val startData = playerData.entries.first().value
        val joinData = playerData.entries.last().value
        val count = countCard()

        val inv = Bukkit.createInventory(null,54, text("BJPResult"))
        val item = SItemStack(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§f§l引き分け").
        setLore(mutableListOf("§e${startData.mcid}の合計：${count.first}","§e${joinData.mcid}の合計：${count.second}")).build()
        for (i in 0..53){
            inv.setItem(i,item)
        }

        runTask {
            Bukkit.getPlayer(startData.uuid)?.openInventory(inv)
            Bukkit.getPlayer(joinData.uuid)?.openInventory(inv)
        }

        sleep(5000)
    }

    //1ラウンドの最後に行う処理 初期化処理ともいう
    private fun gameLaterSetting(battle : Boolean?): Boolean {

        if (playerData.size != 2)return true

        val startData = playerData.entries.first().value
        val joinData = playerData.entries.last().value

        startData.through = false
        joinData.through = false
        startData.bjnumber = 21
        joinData.bjnumber = 21
        startData.death = false
        joinData.death = false
        startData.harvest = false
        joinData.harvest = false
        startData.canSpUse = true
        joinData.canSpUse = true
        val startBet = startData.bet
        val joinBet = joinData.bet

        startData.bet = startData.initialbet
        joinData.bet = joinData.initialbet

        if (battle?:return true){
            if (joinBet <= 0)return true
            return if (joinData.coin - joinBet <= 0){
                startData.coin += joinData.coin
                joinData.coin = 0
                false
            }else{
                joinData.coin -= joinBet
                startData.coin += joinBet
                true
            }
        }else{
            if (startBet <= 0)return true
            return if (startData.coin - startBet <= 0){
                joinData.coin += startData.coin
                startData.coin = 0
                false
            }else{
                startData.coin -= startBet
                joinData.coin += startBet
                true
            }
        }
    }

    //インベントリの用意
    private fun invSetUp(first : Boolean){
        if (playerData.size != 2)return

        val startData = playerData.entries.first().value
        val joinData = playerData.entries.last().value

        if (!first){
            val p1SaveList = ArrayList<SInventoryItem>()
            for (i in 36..44){
                if (startData.inv.getItem(i) == null)continue
                p1SaveList.add(startData.inv.getItem(i))
            }

            val p2SaveList = ArrayList<SInventoryItem>()
            for (i in 36..44){
                if (joinData.inv.getItem(i) == null)continue
                p2SaveList.add(joinData.inv.getItem(i))
            }

            startData.inv = CreateGUI(startData,clocktime)
            joinData.inv = CreateGUI(joinData,clocktime)

            var i = 36
            for (set in p1SaveList){
                startData.inv.setItem(i,set)
                i++
            }

            i = 36
            for (set in p2SaveList){
                joinData.inv.setItem(i,set)
                i++
            }
        }else {
            startData.inv = CreateGUI(startData, clocktime)
            joinData.inv = CreateGUI(joinData, clocktime)
        }

    }

    //dbに情報の保存
    private fun savePlayerDataLog(uuid: UUID): Boolean {

        val data = playerData[uuid]?:return false
        val enemyData = playerData[data.enemy]!!
        val result = if (data.coin == data.initialcoin) null else data.coin > enemyData.coin

        val rs = BlackJackPlus.mysql.query("select * from bjp_player_log where uuid = '${data.uuid}'")
        if (rs.size == 0){
            if (!BlackJackPlus.mysql.execute(MySQLAPI.buildInsertQuery(hashMapOf(
                    Pair("uuid",data.uuid),Pair("mcid",data.mcid),
                    Pair("win",if (result == true) 1 else 0),Pair("draw",if (result == null) 1 else 0),Pair("lose",if (result == false) 1 else 0),
                    Pair("collect",(data.coin - data.initialcoin)*data.onetip)
                ),"bjp_player_log"))) return false
        }else{
            if (!BlackJackPlus.mysql.execute("update bjp_player_log set " +
                        "mcid = '${data.mcid}', " +
                        "win = win+${if (result == true) 1 else 0}, draw = draw+${if (result == null) 1 else 0}, lose = lose+${if (result == false) 1 else 0} " +
                        "where uuid = '${data.uuid}'")) return false
        }
        return true
    }



    //メイン部分
    override fun run() {

        val data = playerData.entries.first().value
        val name = playerData.entries.first().value.mcid
        val minmoney = playerData.entries.first().value.onetip * playerData.entries.first().value.coin


        for (time in 60 downTo 0){
            if (playerData.size == 2){
                break
            }
            if (time == 0){
                broadcast(text("§l${name}§aの§5BJP§aは人が集まらなかったので中止しました"))
                BlackJackPlus.vault.deposit(playerData.entries.first().key,minmoney)
                BlackJackPlus.bjpData.remove(playerData.entries.first().key)
                return
            }


            if (time % 20 == 0){
                broadcast(runCmd("§l${name}§aが§5§lBJP§aを募集中...残り${time}秒 §f/bjp join $name §e必要金額 ${BlackJackPlus.format(minmoney)}円\n" +
                        "§b部屋設定：チップ一枚当たりの賭け金：${data.onetip}円 ラウンド数：${round}回 チップ数：${data.initialcoin}枚 初期ベット数：${data.initialbet}枚 1ターンの時間：${clocktime}秒",
                    "/bjp join $name","§6またはここをクリック！"))
            }

            sleep(1000)
        }

        val startData = playerData.entries.first().value
        val joinData = playerData.entries.last().value


        var firstturn = startData.uuid

        invSetUp(true)
        for (round in 1..round){


            betUpdate()

            runTask {
                startData.inv.open(Bukkit.getPlayer(startData.uuid))
                joinData.inv.open(Bukkit.getPlayer(joinData.uuid))
            }
            if (round == 1){
                SpCard().drawSpCard(startData)
                SpCard().drawSpCard(joinData)
            }

            SpCard().drawSpCard(startData)
            SpCard().drawSpCard(joinData)

            Cards.drawCard(startData,canDrawSp = false,invisible = true)
            sleep(1000)
            Cards.drawCard(joinData,canDrawSp = false,invisible = true)
            sleep(1000)
            Cards.drawCard(startData,canDrawSp = false,invisible = false)
            sleep(1000)
            Cards.drawCard(joinData,canDrawSp = false,invisible = false)
            sleep(1000)


            firstturn = if (round == 1){
                if (Random.nextInt(1..2) == 1) startData.uuid else joinData.uuid
            }else{
                if (firstturn == startData.uuid) joinData.uuid else startData.uuid
            }
            var turn = firstturn
            fillAction(turn)

            showCardSum()

            while (!startData.through || !joinData.through){
                val turnData = playerData[turn]!!
                for (i in clocktime * 20 downTo 0){


                    if (turnData.action != PlayerData.Action.Nothing){
                        when(turnData.action){
                            PlayerData.Action.THROUGH->{
                                turnData.through = true
                                turnData.action = PlayerData.Action.Nothing
                                break
                            }

                            PlayerData.Action.DRAW->{
                                turnData.through = false
                                turnData.action = PlayerData.Action.Nothing
                                break
                            }

                            PlayerData.Action.SPUSE->{
                                break
                            }
                            else -> break
                        }
                    }

                    sleep(50)
                    if (i == 0){
                        turnData.through = true
                        turnData.action = PlayerData.Action.Nothing
                        break
                    }

                    if (i % 20 == 0){
                        timeCount(i / 20)
                    }

                }

                showCardSum()

                if (turnData.action == PlayerData.Action.SPUSE){
                    replaceAction(turn)
                    sleep(5000)
                    turnData.action = PlayerData.Action.Nothing
                    playerData[turnData.enemy]!!.through = false
                    fillAction(turn)
                    showCardSum()
                    continue
                }

                replaceAction(turn)

                turn = if (turn == startData.uuid) joinData.uuid else startData.uuid

                if (!startData.through || !joinData.through) fillAction(turn)


            }

            for (loop in 1..3){
                sleep(500)
                allPlaySound(Sound.BLOCK_ANVIL_PLACE,0.8f,1f)
            }

            sleep(1000)
            allPlaySound(Sound.ENTITY_GENERIC_EXPLODE,0.8f,1f)

            val gameEnd = endTwoTurn()

            if (gameEnd == null) draw() else if (gameEnd) win(startData.uuid) else win(joinData.uuid)

            val later = gameLaterSetting(gameEnd)

            if (!later)break

            invSetUp(false)


        }


        allPlayerSend("§5===============結果===============")
        allPlayerSend("§e${startData.mcid}：${startData.coin}/${startData.initialcoin}枚")
        allPlayerSend("§e${joinData.mcid}：${joinData.coin}/${joinData.initialcoin}枚")
        allPlayerSend("§5===============結果===============")


        BlackJackPlus.vault.deposit(startData.uuid,startData.coin * startData.onetip)
        BlackJackPlus.vault.deposit(joinData.uuid,joinData.coin * joinData.onetip)


        val map = HashMap<String,Any?>()
        map["startUUID"] = startData.uuid
        map["startMCID"] = startData.mcid
        map["joinUUID"] = joinData.uuid
        map["joinMCID"] = joinData.mcid
        map["tip"] = startData.onetip
        map["round"] = round
        map["coin"] = startData.initialcoin
        map["bet"] = startData.initialbet
        map["time"] = clocktime
        map["startCoin"] = startData.coin
        map["joinCoin"] = joinData.coin
        BlackJackPlus.mysql.execute(MySQLAPI.buildInsertQuery(map,"bjp_log"))
        savePlayerDataLog(startData.uuid)
        savePlayerDataLog(joinData.uuid)


        BlackJackPlus.bjpData.remove(startData.uuid)

    }
}