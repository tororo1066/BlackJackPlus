package tororo1066.blackjackplus.bjputlis

import tororo1066.blackjackplus.BJPGame

class InventoryUtil(val playerData: BJPGame.PlayerData) {

    fun checkPlayerCard(): Int? {
        for (i in 28..35){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    fun checkEnemyCard(): Int? {
        for (i in 7 downTo 0){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    fun checkPlayerSpCard(): Int?{
        for (i in 36..44){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    fun checkPlayerPutSpCard(): Int?{
        for (i in 29..33){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }

    fun checkEnemyPutSpCard(): Int?{
        for (i in 15 downTo 11){
            if (playerData.inv.getItem(i) != null)continue
            return i
        }
        return null
    }




}