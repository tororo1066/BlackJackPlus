package tororo1066.blackjackplus.bjputlis

import org.bukkit.Material
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SInventory.SInventoryItem
import tororo1066.blackjackplus.Utils.SItemStack
import java.util.*

object OtherItem {

    fun betNugget(mcid : String, bet : Int, tip : Int): SInventoryItem {
        return SInventoryItem(SItemStack(Material.GOLD_NUGGET).setDisplayName("§c${mcid}の賭け金/チップ").addLore("§e$bet/${tip}枚").build()).clickable(false)
    }

    fun drawCardButton(playerData: BJPGame.PlayerData): SInventoryItem {
        return SInventoryItem(SItemStack(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("§f§lカードを引く").build()).clickable(false).setEvent {
            if (Cards.drawCard(playerData,canDrawSp = true,invisible = false)) playerData.action = BJPGame.PlayerData.Action.DRAW
        }
    }
    fun noDrawCardButton(playerData: BJPGame.PlayerData): SInventoryItem {
        return SInventoryItem(SItemStack(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§a§lカードを引かない").build()).clickable(false).setEvent {
            playerData.action = BJPGame.PlayerData.Action.THROUGH
        }
    }
}