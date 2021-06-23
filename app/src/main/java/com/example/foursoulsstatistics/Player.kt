package com.example.foursoulsstatistics

import java.util.stream.IntStream.range

class Player (var playerName: String, var charName: String, var charImage: Int) {

    val imageMap = mapOf(
            // Base Game Characters
            "blue baby" to listOf(R.drawable.blue_baby,R.drawable.blue_baby_alt),
            "cain" to listOf(R.drawable.cain),
            "eden" to listOf(R.drawable.eden),
            "eve" to listOf(R.drawable.eve),
            "the forgotten" to listOf(R.drawable.forgotten),
            "isaac" to listOf(R.drawable.isaac),
            "judas" to listOf(R.drawable.judas,R.drawable.judas_alt),
            "lazarus" to listOf(R.drawable.lazarus),
            "lilith" to listOf(R.drawable.lilith,R.drawable.lilith_alt),
            "maggy" to listOf(R.drawable.maggy),
            "samson" to listOf(R.drawable.samson),
            // Gold Box Characters
            "apollyon" to listOf(R.drawable.apollyon),
            "azazel" to listOf(R.drawable.azazel),
            "the keeper" to listOf(R.drawable.keeper,R.drawable.keeper_alt),
            "the lost" to listOf(R.drawable.lost,R.drawable.lost_alt),
            // FS+ Characters
            "bumbo" to listOf(R.drawable.bumbo),
            "dark judas" to listOf(R.drawable.dark_judas),
            "guppy" to listOf(R.drawable.guppy, R.drawable.guppy_alt),
            "whore of babylon" to listOf(R.drawable.whore),
            // Reqiuem Characters
            "bethany" to listOf(R.drawable.bethany),
            "jacob & esau" to listOf(R.drawable.jacob_esau),
            // Tainted Characters
            "the baleful" to listOf(R.drawable.baleful),
            "the benighted" to listOf(R.drawable.benighted),
            "the broken" to listOf(R.drawable.broken, R.drawable.broken_alt),
            "the capricious" to listOf(R.drawable.capricious),
            "the curdled" to listOf(R.drawable.curdled),
            "the dauntless" to listOf(R.drawable.dauntless),
            "the deceiver" to listOf(R.drawable.deceiver),
            "the deserter" to listOf(R.drawable.deserter),
            "the empty" to listOf(R.drawable.empty),
            "the enigma" to listOf(R.drawable.enigma, R.drawable.enigma2),
            "the fettered" to listOf(R.drawable.fettered),
            //"the harlot" to listOf(R.drawable.harlot,R.drawable.harlot_alt),
            "the hoarder" to listOf(R.drawable.hoarder),
            "the miser" to listOf(R.drawable.miser),
            "the savage" to listOf(R.drawable.savage, R.drawable.savage_alt),
            "the soiled" to listOf(R.drawable.soiled),
            "the zealot" to listOf(R.drawable.zealot),
            // Warp Zones
            "ash" to listOf(R.drawable.ash),
            "blind johnny" to listOf(R.drawable.blind_johnny),
            "blue archer" to listOf(R.drawable.blue_archer),
            "boyfriend" to listOf(R.drawable.boyfriend),
            "captain viridian" to listOf(R.drawable.captain_viridian),
            "guy spelunky" to listOf(R.drawable.guy_spelunky),
            "pink knight" to listOf(R.drawable.pink_knight),
            "psycho goreman" to listOf(R.drawable.psycho_goreman),
            "quote" to listOf(R.drawable.quote),
            "salad fingers" to listOf(R.drawable.salad_fingers),
            "the knight" to listOf(R.drawable.knight),
            "the silent" to listOf(R.drawable.silent),
            "yung venuz" to listOf(R.drawable.yung_venuz)
    )

    val playerNameList = arrayOf<String>("Jim", "Abs", "Char", "Femi")

    companion object {
        fun makePlayerList(playerNum: Int): ArrayList<Player>{
            val players = ArrayList<Player>()
            for (i in range(1,playerNum+1)) {
                players.add(Player("", "", R.drawable.blank_char))
            }
            return players
        }
        fun updatePlayerList(playerList : ArrayList<Player>, playerNum: Int): ArrayList<Player>{
            val players = playerList
            val currentLength = playerList.size
            if(currentLength < playerNum) {
                for (i in range(currentLength+1, playerNum+1)) {
                    players.add(Player("", "", R.drawable.blank_char))
                }
            }
            else{
                while (players.size > playerNum){
                    players.removeAt(playerNum)
                }
            }
            return players
        }
    }

    fun updateCharacter(newChar: String){
        if (newChar != charName){
            charName = newChar
            if (imageMap.get(charName) != null){
                charImage = imageMap.get(charName)!!.random()
            }
            else{
                charImage = R.drawable.blank_char
            }
        }
    }

    fun updatePlayer(newPlayer: String) {
        playerName = newPlayer
    }

}