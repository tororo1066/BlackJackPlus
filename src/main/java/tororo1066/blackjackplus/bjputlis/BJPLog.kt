package tororo1066.blackjackplus.bjputlis

import org.bukkit.Bukkit
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.bjputlis.AdvancementUtils.Companion.awardAdvancement
import tororo1066.blackjackplus.bjputlis.AdvancementUtils.Companion.isDone
import tororo1066.blackjackplus.bjputlis.advancements.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.floor

class BJPLog {

    lateinit var uuid : UUID
    var mcid = ""
    var win = 0
    var draw = 0
    var lose = 0
    var winPer = 0.0
    var collect = 0.0
    var success = false

    fun getLog(uuid: UUID): BJPLog {
        val mysql = BlackJackPlus.mysql
        val rs = mysql.query("select * from bjp_player_log where uuid = '${uuid}'")
        if (rs.size == 0){
            return BJPLog()
        }

        val result = rs.first()
        val log = BJPLog()
        log.uuid = uuid
        log.mcid = result.getString("mcid")
        log.win = result.getInt("win")
        log.draw = result.getInt("draw")
        log.lose = result.getInt("lose")
        log.winPer = floor(((log.win.toDouble() / (log.win.toDouble() + log.draw.toDouble() + log.lose.toDouble()) * 100.0)))
        log.collect = result.getDouble("collect")
        log.success = true

        val battle = log.win + log.draw + log.lose
        val player = Bukkit.getPlayer(uuid)
        if (player != null){
            Bukkit.getScheduler().runTask(BlackJackPlus.plugin, Runnable {
                if (battle >= 100 && LoginServer.isDone(player))player.awardAdvancement(HundredBattles.key)
                if (battle >= 200 && HundredBattles.isDone(player))player.awardAdvancement(Addict.key)
                if (battle >= 200 && log.winPer >= 60.0 && Addict.isDone(player))player.awardAdvancement(ProGamer.key)
                if (battle >= 200 && log.collect >= 2000000 && ProGamer.isDone(player))player.awardAdvancement(TrueProGamer.key)
            })
        }


        return log
    }

    fun logFormat(log : BJPLog): String {
        return "??6========${log.mcid}?????????========\n" +
                "??c?????????${log.win}???\n" +
                "??d?????????${log.draw}???\n" +
                "??b?????????${log.lose}???\n" +
                "??a?????????${log.winPer}% (????????????)\n" +
                "??${if (log.collect >= 0) "e" else "c"}?????????${BlackJackPlus.format(log.collect)}???\n" +
                "??6========${log.mcid}?????????========"
    }

    fun collectRanking() : List<MutableMap.MutableEntry<String, Double>> {
        val rs = BlackJackPlus.mysql.query("select * from bjp_player_log order by collect desc limit 10")
        if (rs.size == 0)return Collections.emptyList()
        val collectList = HashMap<String,Double>()
        for (result in rs){
            if (result.getDouble("collect") < 1)continue
            collectList[result.getString("mcid")] = result.getDouble("collect")
        }

        return collectList.entries.sortedByDescending { it.value }
    }
}