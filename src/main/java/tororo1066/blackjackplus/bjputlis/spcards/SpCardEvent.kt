package tororo1066.blackjackplus.bjputlis.spcards

import com.comphenix.protocol.PacketType
import org.bukkit.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SInventory.SInventoryItem
import tororo1066.blackjackplus.bjputlis.Cards
import tororo1066.blackjackplus.bjputlis.InventoryUtil
import kotlin.random.Random

class SpCardEvent {

    //数字nbt取得
    private fun getNBT(item : SInventoryItem,nbt : String): Int? {
        return item.itemStack.itemMeta.persistentDataContainer[NamespacedKey(BlackJackPlus.plugin,nbt), PersistentDataType.INTEGER]
    }

    //spカードを使える条件に当てはまっているか確認
    private fun isBJPTable(e : InventoryClickEvent, playerData: BJPGame.PlayerData): Boolean {
        return e.view.title == "BJPTable" && e.inventory.getItem(53) != null && playerData.canSpUse &&
                e.slot !in 11..15 && e.slot !in 20..24
    }

    //パーフェクトドロー、パーフェクトドロー+、アルティメットドローに使用
    private fun drawPerfect(playerData: BJPGame.PlayerData, log : Boolean): Boolean {
        val gameData = BlackJackPlus.bjpData[playerData.starter]?:return false
        val deck = Cards.checkdeck(playerData.inv)?:return false
        val count = gameData.countCard()
        val playercount = if (playerData.uuid == playerData.starter) count.first else count.second
        for (i in playerData.bjnumber-playercount downTo 0){
            if (i == 0){
                if (log) gameData.allPlayerSend("§b適切なカードが見つからなかったので、カードは引かれなかった")
                return false
            }
            if (!deck.contains(i))continue
            Cards.drawCard(playerData,i)
            gameData.renderInventory()
            gameData.allPlayerSend("§d${playerData.mcid}は${i}のカードを引いた")
            return true
        }
        return false
    }

    //breakSpCardに使用
    private fun getEnemySpLoc(loc : Int): Int {
        when(loc){
            15-> return 20
            14-> return 21
            13-> return 22
            12-> return 23
            11-> return 24
        }
        return 0
    }

    //デストロイ、デストロイ+、デストロイ++に使用
    private fun breakSpCard(playerData: BJPGame.PlayerData, slot: Int){
        val item = playerData.inv.getItem(slot)?:return
        val enemyData = BlackJackPlus.bjpData[playerData.starter]!!.playerData[playerData.enemy]!!

        when(getNBT(item,"sp")!!){
            6->{
                playerData.bet -= 5
            }
            8->{
                playerData.bet -= 1
            }
            9->{
                playerData.bet -= 2
            }
            10->{
                playerData.bet -= 2
            }
            11->{
                enemyData.bet += 1
            }
            12->{
                enemyData.bet += 2
            }
            15,16,17->{
                enemyData.bjnumber = 21
                playerData.bjnumber = 21
            }
            22->{
                enemyData.harvest = false
            }
            23->{
                enemyData.death = false
                playerData.bet -= 100
                enemyData.bet -= 100
            }

        }

        playerData.inv.removeItem(slot)
        enemyData.inv.removeItem(getEnemySpLoc(slot))

        InventoryUtil(enemyData).sortSpPutCard()

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        gameData.betUpdate()

    }


    //spカード使用成功時の処理 必ず通す
    private fun spTask(e : InventoryClickEvent,playerData: BJPGame.PlayerData): SInventoryItem {
        val enemyData = BlackJackPlus.bjpData[playerData.starter]!!.playerData[playerData.enemy]!!
        playerData.action = BJPGame.PlayerData.Action.SPUSE
        val slotitem = playerData.inv.getItem(e.slot)
        playerData.inv.removeItem(e.slot)
        if (Bukkit.getPlayer(playerData.uuid) != null){
            val player = Bukkit.getPlayer(playerData.uuid)!!
            val offhand = player.inventory.itemInOffHand.clone()
            player.inventory.setItem(EquipmentSlot.OFF_HAND,slotitem.itemStack.apply { type = Material.TOTEM_OF_UNDYING })
            val protocol = BlackJackPlus.protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS)
            protocol.integers.write(0,player.entityId)
            protocol.bytes.write(0,EntityEffect.TOTEM_RESURRECT.data)
            BlackJackPlus.protocolManager.sendServerPacket(player,protocol)
            player.inventory.setItem(EquipmentSlot.OFF_HAND,offhand)

        }
        if (Bukkit.getPlayer(enemyData.uuid) != null){
            val player = Bukkit.getPlayer(enemyData.uuid)!!
            val offhand = player.inventory.itemInOffHand.clone()

            player.inventory.setItem(EquipmentSlot.OFF_HAND,slotitem.itemStack.apply { type = Material.TOTEM_OF_UNDYING })
            val protocol = BlackJackPlus.protocolManager.createPacket(PacketType.Play.Server.ENTITY_STATUS)
            protocol.integers.write(0,player.entityId)
            protocol.bytes.write(0,EntityEffect.TOTEM_RESURRECT.data)
            BlackJackPlus.protocolManager.sendServerPacket(player,protocol)
            player.inventory.setItem(EquipmentSlot.OFF_HAND,offhand)

        }

        BlackJackPlus.bjpData[playerData.starter]?.allPlayerSend(slotitem.itemStack.itemMeta.displayName)
        for (lore in slotitem.itemStack.lore!!){
            BlackJackPlus.bjpData[playerData.starter]?.allPlayerSend(lore)
        }
        BlackJackPlus.bjpData[playerData.starter]?.allPlaySound(Sound.ITEM_TOTEM_USE,0.8f,1f)
        InventoryUtil(playerData).sortSpCard()
        if (playerData.harvest) SpCard().drawSpCard(playerData)
        playerData.inv.renderInventory()
        playerData.inv.close(Bukkit.getPlayer(playerData.uuid))
        enemyData.inv.close(Bukkit.getPlayer(enemyData.uuid))
        Thread.sleep(3000)
        Bukkit.getScheduler().runTask(BlackJackPlus.plugin, Runnable {
            playerData.inv.open(Bukkit.getPlayer(playerData.uuid))
            enemyData.inv.open(Bukkit.getPlayer(enemyData.uuid))
        })
        if (slotitem != null)
            return slotitem
        return SInventoryItem(ItemStack(Material.AIR))
    }

    //ドロー?
    fun drawAny(e : InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return
        if (playerData.death){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くことを禁じられています！") }
            return
        }
        if (InventoryUtil(playerData).checkPlayerCard() == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くスペースがありません！") }
            return
        }

        val deck = Cards.checkdeck(playerData.inv)
        if (deck == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c山札が空です！") }
            return
        }
        val slotitem = spTask(e,playerData)

        val draw = slotitem.itemStack.itemMeta.persistentDataContainer[NamespacedKey(BlackJackPlus.plugin,"draw"), PersistentDataType.INTEGER]!!
        if (!deck.contains(draw)){
            BlackJackPlus.bjpData[playerData.starter]?.allPlayerSend("§b${draw}は山札にないため、除外された")
            return
        }


        Cards.drawCard(playerData,draw)

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!

        gameData.renderInventory()

        gameData.allPlayerSend("§d${playerData.mcid}は${draw}のカードを引いた")
        return

    }

    //リムーブ
    fun remove(e : InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return

        var loc = InventoryUtil(playerData).checkEnemyCard()
        if (loc == 6){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c相手の場にカードが一枚しかありません！") }
            return
        }
        spTask(e, playerData)
        if (loc == null)loc = 0 else loc += 1

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!
        var enemyLoc = InventoryUtil(enemyData).checkPlayerCard()

        if (enemyLoc == null)enemyLoc = 35 else enemyLoc -= 1

        val card = getNBT(playerData.inv.getItem(loc),"card")!!
        playerData.inv.removeItem(loc)
        enemyData.inv.removeItem(enemyLoc)


        InventoryUtil(playerData).deckSet(card,false)

        gameData.allPlayerSend("§d${enemyData.mcid}の最後にひいたカードは山札に戻された")

    }

    //リターン
    fun reTurn(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return

        var loc = InventoryUtil(playerData).checkPlayerCard()
        if (loc == 29){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c自分の場にカードが一枚しかありません！") }
            return
        }
        spTask(e, playerData)
        if (loc == null)loc = 35 else loc -= 1

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!
        var enemyLoc = InventoryUtil(enemyData).checkEnemyCard()

        if (enemyLoc == null)enemyLoc = 0 else enemyLoc += 1

        val card = getNBT(playerData.inv.getItem(loc),"card")!!

        playerData.inv.removeItem(loc)
        enemyData.inv.removeItem(enemyLoc)


        InventoryUtil(playerData).deckSet(card,false)

        gameData.allPlayerSend("§d${playerData.mcid}の最後にひいたカードは山札に戻された")
    }

    //エクスチェンジ
    fun exchange(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return

        var loc = InventoryUtil(playerData).checkPlayerCard()
        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!
        var enemyLoc = InventoryUtil(enemyData).checkPlayerCard()
        if (loc == 29){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c自分の場にカードが一枚しかありません！") }
            return
        }
        if (enemyLoc == 29){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c相手の場にカードが一枚しかありません！") }
            return
        }
        spTask(e, playerData)

        if (loc == null)loc = 35 else loc -= 1
        if (enemyLoc == null)enemyLoc = 35 else enemyLoc -= 1

        val card = getNBT(playerData.inv.getItem(loc),"card")!!
        val enemyCard = getNBT(enemyData.inv.getItem(enemyLoc),"card")!!

        playerData.inv.setItem(loc,Cards.generateCard(enemyCard))
        enemyData.inv.setItem(enemyLoc,Cards.generateCard(card))
        playerData.inv.setItem((InventoryUtil(playerData).checkEnemyCard()?:10)+1, Cards.generateCard(card))
        enemyData.inv.setItem((InventoryUtil(enemyData).checkEnemyCard()?:10)+1, Cards.generateCard(enemyCard))


        gameData.allPlayerSend("§d${playerData.mcid}と${enemyData.mcid}の最後にひいたカードは入れ替えられた")
    }

    //パーフェクトドロー
    fun perfectDraw(e : InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return

        if (playerData.death){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くことを禁じられています！") }
            return
        }

        val loc = InventoryUtil(playerData).checkPlayerCard()

        if (loc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くスペースがありません！") }
            return
        }

        val deck = Cards.checkdeck(playerData.inv)
        if (deck == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c山札が空です！") }
            return
        }

        spTask(e, playerData)

        drawPerfect(playerData,true)
    }

    //パーフェクトドロー+
    fun perfectDrawPlus(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return

        if (playerData.death){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くことを禁じられています！") }
            return
        }

        val loc = InventoryUtil(playerData).checkPlayerCard()

        if (loc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くスペースがありません！") }
            return
        }

        val deck = Cards.checkdeck(playerData.inv)
        if (deck == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c山札が空です！") }
            return
        }

        val sploc = InventoryUtil(playerData).checkPlayerPutSpCard()
        if (sploc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードを出すスペースがありません！") }
            return
        }

        spTask(e, playerData)

        drawPerfect(playerData,true)

        SpCard().putSpCard(playerData,6)

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!

        gameData.playerData[playerData.enemy]!!.bet += 5

        gameData.betUpdate()

    }

    //アルティメットドロー
    fun ultimateDraw(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return


        if (playerData.death){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くことを禁じられています！") }
            return
        }

        val loc = InventoryUtil(playerData).checkPlayerCard()

        if (loc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くスペースがありません！") }
            return
        }

        val deck = Cards.checkdeck(playerData.inv)
        if (deck == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c山札が空です！") }
            return
        }

        spTask(e, playerData)

        drawPerfect(playerData,true)

        SpCard().drawSpCard(playerData)
        SpCard().drawSpCard(playerData)
    }

    //ベットアップ1、2
    fun betUp(e: InventoryClickEvent, playerData: BJPGame.PlayerData, level : Int){
        if (!isBJPTable(e,playerData))return

        val sploc = InventoryUtil(playerData).checkPlayerPutSpCard()
        if (sploc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードを出すスペースがありません！") }
            return
        }

        spTask(e,playerData)

        SpCard().drawSpCard(playerData)
        SpCard().putSpCard(playerData,if (level == 1) 8 else 9)

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!

        gameData.playerData[playerData.enemy]!!.bet += level

        gameData.betUpdate()

        gameData.allPlayerSend("§d${gameData.playerData[playerData.enemy]!!.mcid}の賭け数が${level}増加した")
    }

    //ベットアップ2+
    fun betUp2Plus(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return

        var loc = InventoryUtil(playerData).checkEnemyCard()
        if (loc == 6){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c相手の場にカードが一枚しかありません！") }
            return
        }
        val sploc = InventoryUtil(playerData).checkPlayerPutSpCard()
        if (sploc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードを出すスペースがありません！") }
            return
        }
        spTask(e, playerData)
        if (loc == null)loc = 0 else loc += 1


        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!
        var enemyLoc = InventoryUtil(enemyData).checkPlayerCard()

        if (enemyLoc == null)enemyLoc = 35 else enemyLoc -= 1

        val card = getNBT(playerData.inv.getItem(loc),"card")!!
        playerData.inv.removeItem(loc)
        enemyData.inv.removeItem(enemyLoc)

        InventoryUtil(playerData).deckSet(card,false)

        SpCard().putSpCard(playerData,10)

        gameData.playerData[playerData.enemy]!!.bet += 2

        gameData.betUpdate()

        gameData.allPlayerSend("§d${gameData.playerData[playerData.enemy]!!.mcid}の賭け数が2増加し、最後に引いたカードは山札に戻された")

    }

    //シールド、シールド+
    fun shield(e: InventoryClickEvent, playerData: BJPGame.PlayerData, level: Int){
        if (!isBJPTable(e,playerData))return

        val sploc = InventoryUtil(playerData).checkPlayerPutSpCard()
        if (sploc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードを出すスペースがありません！") }
            return
        }
        spTask(e, playerData)

        SpCard().putSpCard(playerData,if (level == 1) 11 else 12)


        val gameData = BlackJackPlus.bjpData[playerData.starter]!!

        playerData.bet -= level

        gameData.betUpdate()

        gameData.allPlayerSend("§d${playerData.mcid}の賭け数が${level}減少した")

    }

    //spチェンジ、spチェンジ+
    fun spChange(e: InventoryClickEvent, playerData: BJPGame.PlayerData, isPlus : Boolean){
        if (!isBJPTable(e,playerData))return

        if (isPlus){
            var sploc = InventoryUtil(playerData).checkPlayerSpCard()
            if (sploc == null)sploc = 44 else sploc -= 1
            if (sploc == 36){
                Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードが一枚必要です！") }
                return
            }

            spTask(e, playerData)

            playerData.inv.removeItem(Random.nextInt(36,sploc))
            InventoryUtil(playerData).sortSpCard()
            for (i in 1..3){
                SpCard().drawSpCard(playerData)
            }
        }else{
            var sploc = InventoryUtil(playerData).checkPlayerSpCard()
            if (sploc == null)sploc = 44 else sploc -= 1
            if (sploc <= 37){
                Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードが二枚必要です！") }
                return
            }

            spTask(e, playerData)

            playerData.inv.removeItem(Random.nextInt(36,sploc))
            InventoryUtil(playerData).sortSpCard()
            playerData.inv.removeItem(Random.nextInt(36,sploc-1))
            InventoryUtil(playerData).sortSpCard()
            for (i in 1..3){
                SpCard().drawSpCard(playerData)
            }
        }

    }

    //ゴール17、24、27
    fun goalAny(e: InventoryClickEvent, playerData: BJPGame.PlayerData, goal : Int){
        if (!isBJPTable(e,playerData))return

        val sploc = InventoryUtil(playerData).checkPlayerPutSpCard()
        if (sploc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードを出すスペースがありません！") }
            return
        }

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!

        for (i in 20..24){
            if (playerData.inv.getItem(i) == null)continue
            if (getNBT(playerData.inv.getItem(i),"sp") in 15..17)playerData.inv.removeItem(i)
        }

        for (i in 15 downTo 11){
            if (enemyData.inv.getItem(i) == null)continue
            if (getNBT(enemyData.inv.getItem(i),"sp") in 15..17)enemyData.inv.removeItem(i)
        }

        InventoryUtil(playerData).sortSpPutCard()

        for (i in 20..24){
            if (enemyData.inv.getItem(i) == null)continue
            if (getNBT(enemyData.inv.getItem(i),"sp") in 15..17)enemyData.inv.removeItem(i)
        }

        for (i in 15 downTo 11){
            if (playerData.inv.getItem(i) == null)continue
            if (getNBT(playerData.inv.getItem(i),"sp") in 15..17)playerData.inv.removeItem(i)
        }

        InventoryUtil(enemyData).sortSpPutCard()

        val slotitem = spTask(e, playerData)

        val id = getNBT(slotitem,"sp")!!

        SpCard().putSpCard(playerData,id)

        playerData.bjnumber = goal
        enemyData.bjnumber = goal

        gameData.allPlayerSend("§dゴールが${goal}になった")

    }

    //デストロイ
    fun destroy(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return
        var spLoc = InventoryUtil(playerData).checkEnemyPutSpCard()
        if (spLoc == 15){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c相手の場にSPカードが一枚も出されていません！") }
            return
        }

        if (spLoc == null)spLoc = 11 else spLoc += 1

        spTask(e, playerData)

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!

        breakSpCard(playerData,spLoc)

        gameData.allPlayerSend("§d${enemyData.mcid}の最後に出したSPカードは壊された")
    }

    //デストロイ+
    fun destroyPlus(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return
        val spLoc = InventoryUtil(playerData).checkEnemyPutSpCard()
        if (spLoc == 15){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c相手の場にSPカードが一枚も出されていません！") }
            return
        }


        spTask(e, playerData)

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!

        while (playerData.inv.getItem(15) != null){
            breakSpCard(playerData,15)
        }

        gameData.allPlayerSend("§d${enemyData.mcid}の出したSPカードは全て壊された")
    }

    //デストロイ++
    fun destroyPlusPlus(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return
        val spLoc = InventoryUtil(playerData).checkEnemyPutSpCard()
        if (spLoc == 15){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c相手の場にSPカードが一枚も出されていません！") }
            return
        }

        val putSpLoc = InventoryUtil(playerData).checkPlayerPutSpCard()
        if (putSpLoc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードを出すスペースがありません！") }
            return
        }


        spTask(e, playerData)

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!

        while (playerData.inv.getItem(15) != null){
            breakSpCard(playerData,15)
        }

        SpCard().putSpCard(playerData,20)
        enemyData.canSpUse = false

        gameData.allPlayerSend("§d${enemyData.mcid}の出したSPカードは全て壊され、SPカードが使えなくなった")
    }

    //ラブ・ユア・エネミー
    fun loveYourEnemy(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return

        val loc = InventoryUtil(playerData).checkEnemyCard()


        if (loc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くスペースがありません！") }
            return
        }

        val deck = Cards.checkdeck(playerData.inv)
        if (deck == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c山札が空です！") }
            return
        }

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!

        if (enemyData.death){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c相手はカードを引くことを禁じられています！") }
            return
        }

        spTask(e, playerData)



        if (!drawPerfect(enemyData,false)){
            for (int in 1..12){
                if (int == 12){
                    gameData.allPlayerSend("§b適切なカードが見つからなかったので、カードは引かれなかった")
                    break
                }
                if (!deck.contains(int))continue

                Cards.drawCard(enemyData,int)
                gameData.renderInventory()

                gameData.allPlayerSend("§d${enemyData.mcid}は${int}のカードを引いた")
                break
            }
        }
    }

    //ハーヴェスト
    fun harvest(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return

        if (playerData.harvest){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cすでにハーヴェストを出しています！") }
            return
        }

        val spLoc = InventoryUtil(playerData).checkPlayerPutSpCard()
        if (spLoc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードを出すスペースがありません！") }
            return
        }

        spTask(e, playerData)

        SpCard().putSpCard(playerData,22)

        playerData.harvest = true

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        gameData.allPlayerSend("§d${playerData.mcid}はSPカード使用時にSPカードを引くようになった")
    }

    //デスぺレーション
    fun desperation(e: InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e,playerData))return

        val spLoc = InventoryUtil(playerData).checkPlayerPutSpCard()
        if (spLoc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cSPカードを出すスペースがありません！") }
            return
        }

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!

        if (enemyData.death){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cすでにデスぺレーションが出されています！") }
            return
        }

        spTask(e, playerData)

        SpCard().putSpCard(playerData,23)

        enemyData.death = true
        playerData.bet += 100
        enemyData.bet += 100
        gameData.betUpdate()

        gameData.allPlayerSend("§d両者の賭け数が100増加し、${enemyData.mcid}はカードを引けなくなった")


    }










}


