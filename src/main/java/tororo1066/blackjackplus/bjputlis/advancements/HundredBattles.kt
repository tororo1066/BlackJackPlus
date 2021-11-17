package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import org.bukkit.Material
import tororo1066.blackjackplus.BlackJackPlus

object HundredBattles : Advancement(BlackJackPlus.plugin,"hundred") {

    init {
        addCriteria("hundred",TriggerType.IMPOSSIBLE){}
        setParent(LoginServer.key)
        setDisplay {
            it.setTitle("百戦錬磨")
            it.setDescription("試合を100回以上プレイする")
            it.setIcon(Material.PLAYER_HEAD,"{SkullOwner:MHF_Slime}")
        }
    }
}