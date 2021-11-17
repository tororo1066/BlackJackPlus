package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object UseSp : Advancement(BlackJackPlus.plugin,"use_sp") {

    init {
        addCriteria("use_sp",TriggerType.IMPOSSIBLE){}
        setParent(StartGame.key)
        setDisplay {
            it.setTitle("特殊カード")
            it.setDescription("SPカードを使う")
            it.setIcon(SpCard.spmaterial,"{CustomModelData:${BlackJackPlus.BJPConfig.getInt("cardconfig.spcards.8.csm")}}")
        }
    }
}