package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object UltimateGame : Advancement(BlackJackPlus.plugin,"ultimate_game") {

    init {
        addCriteria("ultimate_game",TriggerType.IMPOSSIBLE){}
        setParent(PerfectGame.key)
        setDisplay {
            it.setTitle("アルティメットゲーム")
            it.setDescription("手際よい強奪")
            it.setIcon(SpCard.spmaterial,"{CustomModelData:${BlackJackPlus.BJPConfig.getInt("cardconfig.spcards.7.csm")}}")
            it.setHidden(true)
            it.setFrame(FrameType.CHALLENGER)
        }
    }
}