package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object WinGame : Advancement(BlackJackPlus.plugin,"win_game") {

    init {
        addCriteria("win_game",TriggerType.IMPOSSIBLE){}
        setParent(UseSp.key)
        setDisplay {
            it.setTitle("勝利")
            it.setDescription("ゲームに勝利する")
            it.setIcon(SpCard.spmaterial,"{CustomModelData:${BlackJackPlus.BJPConfig.getInt("cardconfig.spcards.4.csm")}}")
        }
    }
}