package com.example.foursoulsstatistics

import java.util.stream.IntStream.range

class Player (var playerName: String, var charName: String, var charImage: Int) {

    companion object {
        fun makePlayerList(playerNum: Int): ArrayList<Player>{
            val players = ArrayList<Player>()
            for (i in range(1,playerNum+1)) {
                players.add(Player("Player$i", "", R.drawable.blank_char))
            }
            return players
        }
    }

    fun updateCharacter(newChar: String){
        charName = newChar
        val charImageString = "R.drawable." + charName.toLowerCase()
        //charImage = charImageString
    }

}