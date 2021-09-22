package tororo1066.blackjackplus.bjputlis

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class BJPListener : Listener {

    //イベントキャンセル用
    @EventHandler
    fun invClick(e : InventoryClickEvent){
        if (e.view.title == "BJPTable" || e.view.title == "BJPResult")e.isCancelled = true
    }
}