package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object DeathCombo : Advancement(BlackJackPlus.plugin, "death_combo") {

    init {
        addCriteria("death_combo",TriggerType.IMPOSSIBLE){}
        setParent(DeathGame.key)
        setDisplay {
            it.setTitle("即死コンボ")
            it.setDescription("相手がどうしようもできない状態を作る")
            it.setFrame(FrameType.GOAL)
            it.setIcon(SpCard.spmaterial,"{CustomModelData:${BlackJackPlus.BJPConfig.getInt("cardconfig.spcards.20.csm")}}")
        }
    }
}