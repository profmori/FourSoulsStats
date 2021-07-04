package com.example.foursoulsstatistics

import android.os.Parcelable
import com.example.foursoulsstatistics.database.CharEntity
import com.example.foursoulsstatistics.database.Player
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.stream.IntStream.range

class PlayerHandler (var playerName: String, var charName: String, var charImage: Int, var soulsNum: Int, var winner: Boolean) {

    var charList = emptyArray<CharEntity>()
    var charNames = emptyArray<String>()
    var playerList = emptyArray<Player>()
    var playerNames = emptyArray<String>()
    // Creates variables for all the stored data about characters and players


    companion object {
        fun makePlayerList(playerNum: Int): Array<PlayerHandler> {
            // Function to make the original array of players
            var players = emptyArray<PlayerHandler>()
            // Creates the array
            for (i in range(1, playerNum + 1)) {
                // Iterates through every player in the array
                players += (PlayerHandler("", "", R.drawable.blank_char,0, false))
                // Add a player with no name or character and a blank character image
            }
            return players
            // Returns the array of players
        }

        fun updatePlayerList(playerList: Array<PlayerHandler>, playerNum: Int): Array<PlayerHandler> {
            // Function to update the number of players without resetting the recycler
            val newPlayerList = playerList.toMutableList()
            val currentLength = playerList.size
            // Gets the length of the player list
            if (currentLength < playerNum) {
                // If the number of players has increased
                for (i in range(currentLength, playerNum)) {
                    // For the number of extra players
                    newPlayerList += PlayerHandler("", "", R.drawable.blank_char, 0, false)
                    // Add a player with no name or character and a blank character image
                }
            } else {
                // If the number of players has not increased
                while (newPlayerList.size > playerNum) {
                    // While the current player list is too long
                    newPlayerList.removeAt(playerNum)
                    // Remove the element that would be after the last one
                }
            }
            return newPlayerList.toTypedArray()
            // Returns the updated list of players
        }
    }
        fun addData(chars: Array<CharEntity>, players: Array<Player>){
            charList = chars
            charList.sortBy{it.charName}
            // Gets the list of characters
            charNames = charList.map{charEntity -> charEntity.charName }.toTypedArray()
            // Store the names from the list of characters

            playerList = players
            playerList.sortBy { it.playerName }
            // Gets the list of players
            playerNames = playerList.map{player -> player.playerName }.toTypedArray()
            // Stores the names of the players from the list of players

        }

    fun updateCharacter(newName: String){
        if (charName != newName) {
            charName = newName
            val pos = charNames.indexOf(charName)
            if (pos >= 0) {
                val currentChar = charList[pos]
                charImage = currentChar.image
                // Set the image to the basic value
                if (currentChar.imageAlt != null) {
                    // If there is an alternate image
                    charImage = arrayOf(currentChar.image, currentChar.imageAlt).random()
                    // Select a random character image
                }
            }
        }
    }
}