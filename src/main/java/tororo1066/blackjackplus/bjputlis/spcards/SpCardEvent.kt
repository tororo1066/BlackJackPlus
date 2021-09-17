package tororo1066.blackjackplus.bjputlis.spcards

import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import tororo1066.blackjackplus.BJPGame

class SpCardEvent {

    private fun isBJPTable(e : InventoryClickEvent): Boolean {
        return e.view.title == "BJPTable"
    }

    private fun spTask(){

    }

    fun drawAny(e : InventoryClickEvent, playerData: BJPGame.PlayerData){
        if (!isBJPTable(e))return
        Bukkit.broadcastMessage("test message")
    }
}