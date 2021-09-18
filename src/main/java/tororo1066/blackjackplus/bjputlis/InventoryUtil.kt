package tororo1066.blackjackplus.bjputlis

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SInventory.SInventoryItem

class InventoryUtil(val playerData: BJPGame.PlayerData) {

    fun checkPlayerCard(): Int? {
        for (i in 28..35){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    fun checkEnemyCard(): Int? {
        for (i in 7 downTo 0){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    fun checkPlayerSpCard(): Int?{
        for (i in 36..44){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    fun checkPlayerPutSpCard(): Int?{
        for (i in 29..33){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    fun checkEnemyPutSpCard(): Int?{
        for (i in 15 downTo 11){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }


    fun sortSpCard(){
        val itemList = ArrayList<SInventoryItem>()
        for (i in 36..44){
            if (playerData.inv.getItem(i) == null)continue
            itemList.add(playerData.inv.getItem(i))
            playerData.inv.removeItem(i)
        }

        var i = 36
        for (item in itemList){
            playerData.inv.setItem(i,item)
            i++
        }

        playerData.inv.renderInventory()

    }

    fun deckset(set : Int){
        val enemyData = BlackJackPlus.bjpData[playerData.starter]!!.playerData[playerData.enemy]!!
        val item = playerData.inv.getItem(18).itemStack?:return
        val enemyItem = enemyData.inv.getItem(18).itemStack?:return

        val meta = item.itemMeta
        val enemyMeta = enemyItem.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(BlackJackPlus.plugin,"$set"), PersistentDataType.INTEGER,0)
        enemyMeta.persistentDataContainer.set(NamespacedKey(BlackJackPlus.plugin,"$set"), PersistentDataType.INTEGER,0)

        item.itemMeta = meta
        enemyItem.itemMeta = enemyMeta

        playerData.inv.setItem(18,SInventoryItem(item).clickable(false))
        enemyData.inv.setItem(18,SInventoryItem(enemyItem).clickable(false))

        playerData.inv.renderInventory()
        enemyData.inv.renderInventory()
    }


}