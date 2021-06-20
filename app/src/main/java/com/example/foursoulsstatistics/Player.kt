package com.example.foursoulsstatistics

import java.io.File
import java.io.FileInputStream
import android.content.Context
import java.util.stream.IntStream.range

class Player (val playerName: String, val charName: String, val charImage: Int) {

    companion object {
        fun makePlayerList(playerNum: Int): ArrayList<Player>{
            val players = ArrayList<Player>()
            for (i in range(1,playerNum+1)) {
                players.add(Player("Player$i", "", R.drawable.blank_char))
            }
            return players
        }

        fun getPlayerList() {
            /*val filename = "myfile"
            val fileContents = "Hello world!"
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(fileContents.toByteArray())
             */
        }
    }

}