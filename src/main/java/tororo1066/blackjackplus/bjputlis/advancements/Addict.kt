package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import org.bukkit.Material
import tororo1066.blackjackplus.BlackJackPlus

object Addict : Advancement(BlackJackPlus.plugin,"addict") {

    init {
        addCriteria("addict",TriggerType.IMPOSSIBLE){}
        setParent(HundredBattles.key)
        setDisplay {
            it.setTitle("中毒")
            it.setDescription("試合を200回以上プレイする")
            it.setIcon(Material.PLAYER_HEAD,"{SkullOwner:MHF_Creeper}")
        }
    }
}