package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.display.BackgroundType
import advancement.trigger.TriggerType
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.Cards

object LoginServer : Advancement(BlackJackPlus.plugin,"login_server") {

    init {
        addCriteria("login_server",TriggerType.IMPOSSIBLE){}
        setDisplay {
            it.setTitle("BlackJackPlus")
            it.setDescription("カードゲーム")
            it.setAnnounce(false)
            it.setIcon(Cards.cardmaterial,"{CustomModelData:${BlackJackPlus.invisibleCardCSM}}")
            it.setBackground(BackgroundType.BEDROCK)
        }
    }
}