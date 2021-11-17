package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object DeathGame : Advancement(BlackJackPlus.plugin,"death_game") {

    init {
        addCriteria("death_game",TriggerType.IMPOSSIBLE){}
        setParent(UseSp.key)
        setDisplay {
            it.setTitle("生か死か")
            it.setDescription("デスぺレーションを使用する")
            it.setIcon(SpCard.spmaterial,"{CustomModelData:${BlackJackPlus.BJPConfig.getInt("cardconfig.spcards.23.csm")}}")
        }
    }
}