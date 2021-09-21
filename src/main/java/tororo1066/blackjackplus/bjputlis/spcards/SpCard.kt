package tororo1066.blackjackplus.bjputlis.spcards

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.event.inventory.InventoryClickEvent
import tororo1066.blackjackplus.BJPGame
import tororo1066.blackjackplus.BlackJackPlus
import tororo1066.blackjackplus.Utils.SInventory.SInventoryItem
import tororo1066.blackjackplus.Utils.SItemStack
import tororo1066.blackjackplus.bjputlis.InventoryUtil
import java.util.concurrent.Callable
import kotlin.random.Random
import kotlin.random.nextInt

class SpCard : Callable<SInventoryItem>{

    var id : Int = 0
    lateinit var spCard : SInventoryItem
    lateinit var name : String
    lateinit var description : List<String>
    private val spmaterial = Material.valueOf(BlackJackPlus.BJPConfig.getString("cardconfig.spcardmaterial")?:"PAPER")
    val event = SpCardEvent()

    constructor()
    constructor(id : Int){
        this.id = id
    }

    private fun generateSpCard(name : String, description : List<String>, id : Int, csm : Int, event : (e : InventoryClickEvent)->Unit){
        this.name = name
        this.description = description

        spCard = SInventoryItem(SItemStack(spmaterial).
        setDisplayName(name).setLore(description).setIntNBT(NamespacedKey(BlackJackPlus.plugin,"sp"),id).setCustomModelData(csm).build()).clickable(false).setAsyncEvent { event.invoke(it) }

    }

    private fun callSpCard(playerData: BJPGame.PlayerData){
        val csm = BlackJackPlus.enableSpCards[id]!!
        when(id){
            1->{
                val random = Random.nextInt(3..7)

                generateSpCard("§6ドロー$random",listOf("§e山札に残っている場合のみ、${random}のカードを引く。"),id,BlackJackPlus.drawAnyCSM[random-3]) { event.drawAny(it,playerData) }
                spCard = SInventoryItem(SItemStack(spCard.itemStack).setIntNBT(NamespacedKey(BlackJackPlus.plugin,"draw"),random).build()).clickable(false)
                spCard = spCard.setAsyncEvent { event.drawAny(it,playerData) }
            }

            2->{
                generateSpCard("§6リムーブ", listOf("§e相手が最後にひいたカードを山札に戻す。","§e相手の残りのカードが一枚だと使えない。"),id,csm) { event.remove(it,playerData) }
            }

            3->{
                generateSpCard("§6リターン", listOf("§e自分が最後にひいたカードを山札に戻す。","§e自分の残りのカードが一枚だと使えない。"),id,csm) { event.reTurn(it,playerData) }
            }

            4->{
                generateSpCard("§6エクスチェンジ", listOf("§e両プレイヤーがそれぞれ最後に引いたカードを交換する。"),id,csm) { event.exchange(it,playerData) }
            }

            5->{
                generateSpCard("§6パーフェクトドロー", listOf("§e山札の中から、一番良い数字のカードを引く。","§e適切なカードが見つからなければ引かない。"),id,csm) { event.perfectDraw(it,playerData) }
            }

            6->{
                generateSpCard("§6パーフェクトドロー+", listOf("§e山札の中から、一番良い数字のカードを引く。","§e適切なカードが見つからなければ引かない。","§eさらに場に置かれている間、相手の賭け数を5つ増やす。"),id,csm) { event.perfectDrawPlus(it,playerData) }
            }

            7->{
                generateSpCard("§6アルティメットドロー", listOf("§e山札の中から、一番良い数字のカードを引く。","§e適切なカードが見つからなければ引かない。","§eさらに、spカードを2枚引く。"),id,csm) { event.ultimateDraw(it,playerData) }
            }

            8->{
                generateSpCard("§6ベットアップ1", listOf("§eSPカードを1枚引く。","§eさらに場に置かれている間、相手の賭け数を1つ増やす。"),id,csm) { event.betUp(it,playerData,1) }
            }

            9->{
                generateSpCard("§6ベットアップ2", listOf("§eSPカードを1枚引く。","§eさらに場に置かれている間、相手の賭け数を2つ増やす。"),id,csm) { event.betUp(it,playerData,2) }
            }

            10->{
                generateSpCard("§6ベットアップ2+", listOf("§e相手の最後にひいたカードを山札に戻す。","§eさらに場に置かれている間、相手の賭け数を2つ増やす。"),id,csm) { event.betUp2Plus(it,playerData) }
            }

            11->{
                generateSpCard("§6シールド", listOf("§e場に置かれている間、自分の賭け数を1つ減らす。"),id,csm) { event.shield(it,playerData,1) }
            }

            12->{
                generateSpCard("§6シールド+", listOf("§e場に置かれている間、自分の賭け数を2つ減らす。"),id,csm) { event.shield(it,playerData,2) }
            }

            13->{
                generateSpCard("§6spチェンジ", listOf("§e自分のSPカードをランダムで2枚捨てる。","§eさらにSPカードを3枚引く。"),id,csm) { event.spChange(it,playerData,false) }
            }

            14->{
                generateSpCard("§6spチェンジ+", listOf("§e自分のSPカードをランダムで1枚捨てる。","§eさらにSPカードを3枚引く。"),id,csm) { event.spChange(it,playerData,true) }
            }

            15->{
                generateSpCard("§6ゴール17", listOf("§e場に置かれている間、勝利条件を17にする。","§e他の「ゴール」系カードが場にある場合、それを取り除く。"),id,csm) { event.goalAny(it,playerData,17) }
            }

            16->{
                generateSpCard("§6ゴール24", listOf("§e場に置かれている間、勝利条件を24にする。","§e他の「ゴール」系カードが場にある場合、それを取り除く。"),id,csm) { event.goalAny(it,playerData,24) }
            }

            17->{
                generateSpCard("§6ゴール27", listOf("§e場に置かれている間、勝利条件を27にする。","§e他の「ゴール」系カードが場にある場合、それを取り除く。"),id,csm) { event.goalAny(it,playerData,27) }
            }

            18->{
                generateSpCard("§6デストロイ", listOf("§e相手が最後に場に置いたSPカードを取り除く。"),id,csm) { event.destroy(it,playerData) }
            }

            19->{
                generateSpCard("§6デストロイ+", listOf("§e相手が場に置いた全てのSPカードを取り除く。"),id,csm) { event.destroyPlus(it,playerData) }
            }

            20->{
                generateSpCard("§6デストロイ++", listOf("§e相手の場に出てるspカードを全て消す。","§eさらに、場に置かれている間相手のspカードの仕様を封じる。"),id,csm) { event.destroyPlusPlus(it,playerData) }
            }

            21->{
                generateSpCard("§6ラブ・ユア・エネミー", listOf("§e相手は1枚カードを引く。","§eそのカードの数字は、相手にとって一番良い数字が選ばれる。"),id,csm) { event.loveYourEnemy(it,playerData) }
            }

            22->{
                generateSpCard("§6ハーヴェスト", listOf("§e場に置かれている間、SPカードを使う度にSPカードを1枚引く。"),id,csm) { event.harvest(it,playerData) }
            }

            23->{
                generateSpCard("§6デスぺレーション", listOf("§e場に置かれている間、互いの賭け数が100上がる。","§eまた、相手はカードを引けない。"),id,csm) { event.desperation(it,playerData) }

            }
        }

    }

    fun drawSpCard(playerData: BJPGame.PlayerData){
        val random = BlackJackPlus.enableSpCards.keys.random()
        id = random
        callSpCard(playerData)

        playerData.inv.setItem(InventoryUtil(playerData).checkPlayerSpCard()?:return,spCard)
        playerData.inv.renderInventory()
        val gamedata = BlackJackPlus.bjpData[playerData.starter]?:return
        gamedata.allPlayerSend("§a${playerData.mcid}はSPカードを引いた")
        gamedata.allPlaySound(Sound.ENTITY_PLAYER_LEVELUP,1f,1f)

    }

    fun putSpCard(playerData: BJPGame.PlayerData, id : Int){
        val loc = InventoryUtil(playerData).checkPlayerPutSpCard() ?: return
        this.id = id
        callSpCard(playerData)

        val gamedata = BlackJackPlus.bjpData[playerData.starter]?:return
        gamedata.playerData[playerData.enemy]!!.inv.setItem(InventoryUtil(gamedata.playerData[playerData.enemy]!!).checkEnemyPutSpCard()?:return,spCard)
        playerData.inv.setItem(loc,spCard)

        gamedata.renderInventory()

    }


    @JvmName("getId1")
    fun getId(): Int {
        return id
    }

    @JvmName("getName1")
    fun getName(): String {
        return name
    }

    @JvmName("getDescription1")
    fun getDescription(): List<String> {
        return description
    }


    override fun call(): SInventoryItem {
        return spCard
    }


}

