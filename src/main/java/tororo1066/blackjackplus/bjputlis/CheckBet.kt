package tororo1066.blackjackplus.bjputlis

import tororo1066.blackjackplus.BlackJackPlus
import java.util.concurrent.Callable
import kotlin.math.floor

//賭け金チェック
class CheckBet(private val bet : Double) : Callable<Double>{

    override fun call(): Double {
        val round = floor(bet)
        if (!BlackJackPlus.BJPConfig.getBoolean("price.enable")){
            return round
        }


        if (BlackJackPlus.BJPConfig.getDouble("price.min") > bet || BlackJackPlus.BJPConfig.getDouble("price.max") < bet){
            return -1.0
        }

        return round
    }


}