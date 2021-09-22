package tororo1066.blackjackplus.bjputlis

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SInventory.SInventory
import tororo1066.blackjackplus.Utils.SInventory.SInventoryItem
import tororo1066.blackjackplus.Utils.SItemStack
import java.util.*

//GUI作成
class CreateGUI(playerData: BJPGame.PlayerData, time : Int) : SInventory("BJPTable",6,BlackJackPlus.plugin){

    /*
    0  1  2  3  4  5  6  7  8
    9  10 11 12 13 14 15 16 17
    18 19 20 21 22 23 24 25 26
    27 28 29 30 31 32 33 34 35
    36 37 38 39 40 41 42 43 44
    45 46 47 48 49 50 51 52 53
     */



    private fun createHead(uuid: UUID) : SInventoryItem{
        return SInventoryItem(SItemStack(Material.PLAYER_HEAD).setHeadOwner(uuid).build()).clickable(false)
    }

    init {
        setItem(9,ItemStack(Material.GOLD_NUGGET))

        val item = ItemStack(Material.BOOKSHELF)
        val meta = item.itemMeta
        for (i in 1..11){
            meta.persistentDataContainer.set(NamespacedKey(BlackJackPlus.plugin,"$i"), PersistentDataType.INTEGER,1)
        }
        item.itemMeta = meta

        setItem(18,SInventoryItem(item).clickable(false))

        setItem(27, createHead(playerData.uuid))

        setItem(8,createHead(playerData.enemy))

        setItem(17, SInventoryItem(SItemStack(Material.CLOCK).setDisplayName("§6残り時間").setAmount(time).build()).clickable(false))

        setItem(26, ItemStack(Material.GOLD_NUGGET))

    }
}