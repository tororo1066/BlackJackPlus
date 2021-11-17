package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object TenSpUse : Advancement(BlackJackPlus.plugin,"ten_sp_use") {

    init {
        addCriteria("ten_sp_use",TriggerType.IMPOSSIBLE){}
        setParent(UseSp.key)
        setDisplay {
            it.setTitle("ハーヴェスト")
            it.setDescription("1ラウンドでSPカードを10回使う")
            it.setIcon(SpCard.spmaterial,"{CustomModelData:${BlackJackPlus.BJPConfig.getInt("cardconfig.spcards.22.csm")}}")
            it.setFrame(FrameType.GOAL)
        }
    }
}