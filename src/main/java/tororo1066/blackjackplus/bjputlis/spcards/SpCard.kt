package tororo1066.blackjackplus.bjputlis.spcards

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
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
    private val spmaterial = Material.valueOf(BlackJackPlus.BJPConfig.getString("spcardmaterial")?:"PAPER")
    val event = SpCardEvent()

    constructor()
    constructor(id : Int){
        this.id = id
    }

    private fun generateSpCard(name : String, description : List<String>, id : Int, csm : Int){
        this.name = name
        this.description = description

        spCard = SInventoryItem(SItemStack(spmaterial).
        setDisplayName(name).setLore(description).setIntNBT(NamespacedKey(BlackJackPlus.plugin,"sp"),id).setCustomModelData(csm).build()).clickable(false)

    }

    private fun callSpCard(playerData: BJPGame.PlayerData){
        val csm = BlackJackPlus.enableSpCards[id]!!
        when(id){
            1->{
                val random = Random.nextInt(3..7)

                generateSpCard("§6ドロー$random",listOf("§e山札に残っている場合のみ、${random}のカードを引く。"),1,csm)
                spCard = SInventoryItem(SItemStack(spCard.itemStack).setIntNBT(NamespacedKey(BlackJackPlus.plugin,"draw"),random).build())
                spCard = spCard.setEvent { event.drawAny(it,playerData) }
            }

            2->{
                generateSpCard("§6リムーブ", listOf("§e相手が最後にひいたカードを山札に戻す。","§e相手の残りのカードが一枚だと使えない。"),2,csm)

            }
        }

    }

    fun drawSpCard(playerData: BJPGame.PlayerData){
        val random = BlackJackPlus.enableSpCards.keys.random()
        id = random
        callSpCard(playerData)

        playerData.inv.setItem(InventoryUtil(playerData).checkPlayerPutSpCard()?:return,spCard)
        playerData.inv.renderInventory()
        val gamedata = BlackJackPlus.bjpData[playerData.starter]?:return
        gamedata.allPlayerSend("§a${playerData.mcid}はSPカードを引いた")
        gamedata.allPlaySound(Sound.ENTITY_PLAYER_LEVELUP,1f,1f)

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

