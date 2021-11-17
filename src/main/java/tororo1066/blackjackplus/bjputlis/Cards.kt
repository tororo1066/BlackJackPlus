package tororo1066.blackjackplus.bjputlis

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.persistence.PersistentDataType
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SInventory.SInventory
import tororo1066.blackjackplus.Utils.SInventory.SInventoryItem
import tororo1066.blackjackplus.Utils.SItemStack
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object Cards {


    val cardmaterial = Material.valueOf(BlackJackPlus.BJPConfig.getString("cardconfig.cardmaterial")?:"PAPER")


    //山札確認
    fun checkdeck(inv : SInventory): ArrayList<Int>? {
        val deck = inv.getItem(18).itemStack?:return null
        val decklist = ArrayList<Int>()
        for (card in deck.itemMeta.persistentDataContainer.keys){
            if (deck.itemMeta.persistentDataContainer[card, PersistentDataType.INTEGER] == 1) decklist.add(card.key.toInt())
        }
        return decklist
    }

    //通常時
    fun drawCard(playerData: BJPGame.PlayerData, canDrawSp : Boolean, invisible : Boolean): Boolean {
        if (playerData.death){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くことを禁じられています！") }
            return false
        }
        val inv = playerData.inv
        val random = checkdeck(inv)?.random()
        val count = BlackJackPlus.bjpData[playerData.starter]!!.countCard()
        val playercount = if (playerData.uuid == playerData.starter) count.first else count.second
        if (playercount > playerData.bjnumber){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cすでにバーストしています！") }
            return false
        }

        if (random == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c山札が空です！") }
            return false
        }
        val util = InventoryUtil(playerData)

        val cardloc = util.checkPlayerCard()

        if (cardloc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くスペースがありません！") }
            return false
        }
        if (canDrawSp && Math.random() <= BlackJackPlus.BJPConfig.getDouble("gameconfig.spdrawchance")/100){
            SpCard().drawSpCard(playerData)
        }

        val enemyData = BlackJackPlus.bjpData[playerData.starter]!!.playerData[playerData.enemy]!!
        val enemyLoc = InventoryUtil(enemyData).checkEnemyCard()?:return false

        if (invisible){
            playerData.inv.setItem(cardloc, generateCard(random))
            enemyData.inv.setItem(enemyLoc, generateNullCard(random,false))
        }else{
            playerData.inv.setItem(cardloc, generateCard(random))
            enemyData.inv.setItem(enemyLoc, generateCard(random))
        }

        val item = playerData.inv.getItem(18).itemStack?:return false
        val enemyItem = enemyData.inv.getItem(18).itemStack?:return false

        val meta = item.itemMeta
        val enemyMeta = enemyItem.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(BlackJackPlus.plugin,"$random"), PersistentDataType.INTEGER,0)
        enemyMeta.persistentDataContainer.set(NamespacedKey(BlackJackPlus.plugin,"$random"), PersistentDataType.INTEGER,0)

        item.itemMeta = meta
        enemyItem.itemMeta = enemyMeta

        playerData.inv.setItem(18,SInventoryItem(item).clickable(false))
        enemyData.inv.setItem(18,SInventoryItem(enemyItem).clickable(false))



        BlackJackPlus.bjpData[playerData.starter]?.allPlaySound(Sound.ITEM_BOOK_PAGE_TURN,2f,1f)

        playerData.inv.renderInventory()
        enemyData.inv.renderInventory()
        return true
    }

    //指定したカードを引く
    fun drawCard(playerData: BJPGame.PlayerData, card : Int): Boolean {
        val deck = checkdeck(playerData.inv)

        if (deck == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c山札が空です！") }
            return false
        }

        if (!deck.contains(card)){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c${card}は存在しません！") }
            return false
        }
        val util = InventoryUtil(playerData)

        val cardloc = util.checkPlayerCard()

        if (cardloc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§cカードを引くスペースがありません！") }
            return false
        }

        val enemyData = BlackJackPlus.bjpData[playerData.starter]!!.playerData[playerData.enemy]!!
        val enemyLoc = InventoryUtil(enemyData).checkEnemyCard()?:return false


        playerData.inv.setItem(cardloc, generateCard(card))
        enemyData.inv.setItem(enemyLoc, generateCard(card))


        val item = playerData.inv.getItem(18).itemStack?:return false
        val enemyItem = enemyData.inv.getItem(18).itemStack?:return false

        val meta = item.itemMeta
        val enemyMeta = enemyItem.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(BlackJackPlus.plugin,"$card"), PersistentDataType.INTEGER,0)
        enemyMeta.persistentDataContainer.set(NamespacedKey(BlackJackPlus.plugin,"$card"), PersistentDataType.INTEGER,0)

        item.itemMeta = meta
        enemyItem.itemMeta = enemyMeta

        playerData.inv.setItem(18,SInventoryItem(item).clickable(false))
        enemyData.inv.setItem(18,SInventoryItem(enemyItem).clickable(false))



        BlackJackPlus.bjpData[playerData.starter]?.allPlaySound(Sound.ITEM_BOOK_PAGE_TURN,2f,1f)

        playerData.inv.renderInventory()
        enemyData.inv.renderInventory()
        return true
    }

    //カード生成
    fun generateCard(int : Int): SInventoryItem {
        return SInventoryItem(SItemStack(cardmaterial).setDisplayName("$int").setIntNBT(NamespacedKey(BlackJackPlus.plugin,"card"),int).setCustomModelData(BlackJackPlus.cardCSM[int-1]).build()).clickable(false)
    }

    //見えないカード生成
    private fun generateNullCard(int : Int, show : Boolean): SInventoryItem {
        return if (show){
            SInventoryItem(SItemStack(cardmaterial).setDisplayName("$int").setIntNBT(NamespacedKey(BlackJackPlus.plugin,"card"),int).setCustomModelData(BlackJackPlus.invisibleCardCSM).build()).clickable(false)
        }else{
            SInventoryItem(SItemStack(cardmaterial).setDisplayName("§l?").setIntNBT(NamespacedKey(BlackJackPlus.plugin,"card"),int).setCustomModelData(BlackJackPlus.invisibleCardCSM).build()).clickable(false)
        }
    }



}