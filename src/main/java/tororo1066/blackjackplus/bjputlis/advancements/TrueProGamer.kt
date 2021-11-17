package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import org.bukkit.Material
import tororo1066.blackjackplus.BlackJackPlus

object TrueProGamer : Advancement(BlackJackPlus.plugin,"true_pro_gamer") {

    init {
        addCriteria("true_pro_gamer",TriggerType.IMPOSSIBLE){}
        setParent(ProGamer.key)
        setDisplay {
            it.setTitle("本当のプロゲーマー")
            it.setDescription("200試合以上プレイし、総獲得賞金を200万以上にする")
            it.setFrame(FrameType.CHALLENGER)
            it.setHidden(true)
            it.setIcon(Material.PLAYER_HEAD,"{SkullOwner:MHF_Herobrine}")
        }
    }
}