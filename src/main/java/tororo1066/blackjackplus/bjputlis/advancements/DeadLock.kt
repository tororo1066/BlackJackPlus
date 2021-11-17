package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.display.FrameType
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.spcards.SpCard

object DeadLock : Advancement(BlackJackPlus.plugin,"dead_lock") {

    init {
        addCriteria("dead_lock",TriggerType.IMPOSSIBLE){}
        setParent(DeathGame.key)
        setDisplay {
            it.setTitle("デッドロック")
            it.setDescription("お互いにデスぺレーションを出す")
            it.setFrame(FrameType.CHALLENGER)
            it.setIcon(SpCard.spmaterial,"{CustomModelData:${BlackJackPlus.BJPConfig.getInt("cardconfig.spcards.23.csm")}}")
        }
    }
}