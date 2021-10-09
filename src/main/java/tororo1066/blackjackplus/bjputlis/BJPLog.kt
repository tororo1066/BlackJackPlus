package tororo1066.blackjackplus.bjputlis

import tororo1066.blackjackplus.BlackJackPlus
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
        log.winPer = floor(((log.win / (log.win + log.draw + log.lose) * 100).toDouble()))
        log.collect = result.getDouble("collect")
        log.success = true

        return log
    }

    fun logFormat(log : BJPLog): String {
        return "§6========${log.mcid}の成績========\n" +
                "§c勝利：${log.win}回\n" +
                "§d引分：${log.draw}回\n" +
                "§b敗北：${log.lose}回\n" +
                "§a勝率：${log.winPer}% (切り捨て)\n" +
                "§${if (log.collect >= 0) "e" else "c"}収支：${BlackJackPlus.format(log.collect)}円\n" +
                "§6========${log.mcid}の成績========"
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