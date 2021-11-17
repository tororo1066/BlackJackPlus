package tororo1066.blackjackplus.bjputlis.advancements

import advancement.Advancement
import advancement.trigger.TriggerType
import org.bukkit.Material
import tororo1066.blackjackplus.BlackJackPlus

object StartGame : Advancement(BlackJackPlus.plugin,"start_game") {

    init {
        addCriteria("start_game",TriggerType.IMPOSSIBLE){}
        setParent(LoginServer.key)
        setDisplay {
            it.setTitle("死のゲーム")
            it.setDescription("BJPをプレイする")
            it.setIcon(Material.TOTEM_OF_UNDYING)
        }
    }
}