package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object PerfectGame : Advancement(BlackJackPlus.plugin,"perfect_game") {

    init {
        addCriteria("perfect_game",TriggerType.IMPOSSIBLE){}
        setParent(BlackJack.key)
        setDisplay {
            it.setTitle("パーフェクトゲーム")
            it.setDescription("相手のチップを全て奪い取る")
            it.setIcon(SpCard.spmaterial,"{CustomModelData:${BlackJackPlus.BJPConfig.getInt("cardconfig.spcards.6.csm")}}")
        }
    }
}