package tororo1066.blackjackplus.bjputlis

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SInventory.SInventoryItem

class InventoryUtil(val playerData: BJPGame.PlayerData) {

    //プレイヤーの手札位置確認
    fun checkPlayerCard(): Int? {
        for (i in 28..35){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    //敵の手札位置確認
    fun checkEnemyCard(): Int? {
        for (i in 7 downTo 0){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    //プレイヤーのspカード位置確認
    fun checkPlayerSpCard(): Int?{
        for (i in 36..44){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    //プレイヤーのspカード置き位置確認
    fun checkPlayerPutSpCard(): Int?{
        for (i in 20..24){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    //敵のspカード置き位置確認
    fun checkEnemyPutSpCard(): Int?{
        for (i in 15 downTo 11){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }


    //spカードのソート
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

    fun countSpCard(): Int {
        var count = 0
        for (i in 36..44){
            if (playerData.inv.getItem(i) == null)continue
            count += 1
        }
        return count
    }

    //場に出てるspカードのソート
    fun sortSpPutCard(){
        val itemList = ArrayList<SInventoryItem>()
        val enemyData = BlackJackPlus.bjpData[playerData.starter]!!.playerData[playerData.enemy]!!
        val enemyItemList = ArrayList<SInventoryItem>()
        for (i in 20..24){
            if (playerData.inv.getItem(i) == null)continue
            itemList.add(playerData.inv.getItem(i))
            playerData.inv.removeItem(i)
        }

        for (i in 15 downTo 11){
            if (enemyData.inv.getItem(i) == null)continue
            enemyItemList.add(enemyData.inv.getItem(i))
            enemyData.inv.removeItem(i)
        }

        var i = 20
        for (item in itemList){
            playerData.inv.setItem(i,item)
            i++
        }

        i = 15
        for (item in enemyItemList){
            enemyData.inv.setItem(i,item)
            i--
        }

    }

    //山札に戻す(消す)
    fun deckSet(set : Int, remove : Boolean): Boolean {
        val enemyData = BlackJackPlus.bjpData[playerData.starter]!!.playerData[playerData.enemy]!!
        val item = playerData.inv.getItem(18).itemStack?:return false
        val enemyItem = enemyData.inv.getItem(18).itemStack?:return false

        val meta = item.itemMeta
        val enemyMeta = enemyItem.itemMeta
        meta.persistentDataContainer.set(NamespacedKey(BlackJackPlus.plugin,"$set"), PersistentDataType.INTEGER,if (remove) 0 else 1)
        enemyMeta.persistentDataContainer.set(NamespacedKey(BlackJackPlus.plugin,"$set"), PersistentDataType.INTEGER,if (remove) 0 else 1)

        item.itemMeta = meta
        enemyItem.itemMeta = enemyMeta

        playerData.inv.setItem(18,SInventoryItem(item).clickable(false))
        enemyData.inv.setItem(18,SInventoryItem(enemyItem).clickable(false))

        playerData.inv.renderInventory()
        enemyData.inv.renderInventory()
        return true
    }


}