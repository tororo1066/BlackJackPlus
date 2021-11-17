package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object BlackJack : Advancement(BlackJackPlus.plugin,"black_jack") {

    init {
        addCriteria("black_jack",TriggerType.IMPOSSIBLE){}
        setParent(LoginServer.key)
        setDisplay {
            it.setTitle("BlackJack")
            it.setDescription("BlackJackを成立させる")
            it.setIcon(SpCard.spmaterial,"{CustomModelData:${BlackJackPlus.BJPConfig.getInt("cardconfig.spcards.5.csm")}}")
        }
    }
}