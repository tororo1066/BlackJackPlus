package tororo1066.blackjackplus.bjputlis

import advancement.Advancement
import advancement.AdvancementManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.advancements.*

class AdvancementUtils : AdvancementManager(BlackJackPlus.plugin), Listener {

    fun loadAdvancements(){
        registerAll(LoginServer,StartGame,UseSp,WinGame,DeathGame,BlackJack,PerfectGame,UltimateGame,HundredBattles,Addict,ProGamer,TrueProGamer,DeadLock,DeathCombo,TenSpUse)
        createAll(true)
    }

    private fun registerAll(vararg advancement: Advancement){
        advancement.forEach { register(it) }
    }

    companion object{
        fun Player.awardAdvancement(key: NamespacedKey): Boolean {
            val advancement = Bukkit.getAdvancement(key)?:return false
            val progress = getAdvancementProgress(advancement)
            advancement.criteria.forEach { progress.awardCriteria(it) }
            return true
        }

        fun Advancement.isDone(p : Player): Boolean {
            return p.getAdvancementProgress(Bukkit.getAdvancement(this.key)?:return false).isDone
        }
    }

    @EventHandler
    fun onJoin(e : PlayerJoinEvent){
        e.player.awardAdvancement(LoginServer.key)
    }
}