package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import org.bukkit.Material
import tororo1066.blackjackplus.BlackJackPlus

object ProGamer : Advancement(BlackJackPlus.plugin,"pro_gamer") {

    init {
        addCriteria("pro_gamer",TriggerType.IMPOSSIBLE){}
        setParent(Addict.key)
        setDisplay {
            it.setTitle("プロゲーマー")
            it.setDescription("200試合以上プレイし、勝率を60%以上にする")
            it.setFrame(FrameType.CHALLENGER)
            it.setIcon(Material.PLAYER_HEAD,"{SkullOwner:MHF_WSkeleton}")
        }
    }
}