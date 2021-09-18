package tororo1066.blackjackplus.bjputlis.spcards

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SInventory.SInventoryItem
import tororo1066.blackjackplus.bjputlis.Cards
import tororo1066.blackjackplus.bjputlis.InventoryUtil

class SpCardEvent {

    private fun getNBT(item : SInventoryItem,nbt : String): Int? {
        return item.itemStack.itemMeta.persistentDataContainer[NamespacedKey(BlackJackPlus.plugin,nbt), PersistentDataType.INTEGER]
    }

    private fun isBJPTable(e : InventoryClickEvent): Boolean {
        return e.view.title == "BJPTable" && e.inventory.getItem(53) != null
    }


    private fun spTask(e : InventoryClickEvent,playerData: BJPGame.PlayerData): SInventoryItem {
        playerData.action = BJPGame.PlayerData.Action.SPUSE
        val slotitem = playerData.inv.getItem(e.slot)
        playerData.inv.removeItem(e.slot)

        BlackJackPlus.bjpData[playerData.starter]?.allPlayerSend(slotitem.itemStack.itemMeta.displayName)
        for (lore in slotitem.itemStack.lore!!){
            BlackJackPlus.bjpData[playerData.starter]?.allPlayerSend(lore)
        }
        BlackJackPlus.bjpData[playerData.starter]?.allPlaySound(Sound.ITEM_TOTEM_USE,0.8f,1f)
        InventoryUtil(playerData).sortSpCard()
        playerData.inv.renderInventory()
        Thread.sleep(3000)
        return slotitem
    }

    fun drawAny(e : InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e))return
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
        playerData.inv.renderInventory()
        BlackJackPlus.bjpData[playerData.starter]!!.playerData[playerData.enemy]!!.inv.renderInventory()

        BlackJackPlus.bjpData[playerData.starter]?.allPlayerSend("§d${playerData.mcid}は${draw}のカードを引いた")
        return

    }

    fun remove(e : InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e))return

        var loc = InventoryUtil(playerData).checkEnemyCard()
        if (loc == 6){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c相手の場にカードが一枚しかありません！") }
            return
        }
        spTask(e, playerData)
        if (loc == null)loc = 0 else loc -= 1

        val gameData = BlackJackPlus.bjpData[playerData.starter]!!
        val enemyData = gameData.playerData[playerData.enemy]!!
        var enemyLoc = InventoryUtil(enemyData).checkPlayerCard()

        if (enemyLoc == null)enemyLoc = 35 else loc -= 1

        val card = getNBT(playerData.inv.getItem(loc),"card")!!
        playerData.inv.removeItem(loc)
        enemyData.inv.removeItem(enemyLoc)


        InventoryUtil(playerData).deckset(card)

        gameData.allPlayerSend("§d${enemyData.mcid}の最後にひいたカードは山札に戻された")

    }

    fun perfectDraw(e : InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e))return

        val loc = InventoryUtil(playerData).checkPlayerCard()

        if (loc == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§ccカードを引くスペースがありません！") }
            return
        }

        val deck = Cards.checkdeck(playerData.inv)
        if (deck == null){
            Bukkit.getPlayer(playerData.uuid)?.let { BlackJackPlus.sendMsg(it,"§c山札が空です！") }
            return
        }


    }

}