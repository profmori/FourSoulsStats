package com.profmori.foursoulsstatistics.data_handlers

import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.Player

class PlayerHandler(
    var playerName: String,
    var charName: String,
    var charImage: Int,
    var eternal: String?,
    var soulsNum: Int,
    var winner: Boolean
) {

    var charList = emptyArray<CharEntity>()
    var charNames = emptyArray<String>()
    var playerList = emptyArray<Player>()
    var playerNames = emptyArray<String>()

    // Creates variables for all the stored data about characters and players
    var fonts = emptyMap<String, Typeface>()

    // Global font variable
    var useAlts = true

    // Variable for whether or not to use alternate art
    var usePromo = true

    // Variable for whether or not to use promo eden cards
    var useRequiem = true
    // Variable for whether or not to use requiem eden cards

    var useRetro = true
    // Variable for whether or not to use retro eden cards

    var useRetroText = true
    // variable for whether or not to use the pixel eden font


    companion object {
        fun makePlayerList(playerNum: Int): Array<PlayerHandler> {
            // Function to make the original array of players
            var players = emptyArray<PlayerHandler>()
            // Creates the array
            for (i in (1 until playerNum + 1)) {
                // Iterates through every player in the array
                players += (PlayerHandler("", "", ImageHandler.randomBlank(), null, 0, false))
                // Add a player with no name or character and a blank character image
            }
            return players
            // Returns the array of players
        }

        fun updatePlayerList(
            playerList: Array<PlayerHandler>,
            playerNum: Int
        ): Array<PlayerHandler> {
            // Function to update the number of players without resetting the recycler
            val newPlayerList = playerList.toMutableList()
            // Creates a mutable list that can be reduced or increased in length
            val currentLength = playerList.size
            // Gets the length of the player list
            if (currentLength < playerNum) {
                // If the number of players has increased
                for (i in (currentLength until playerNum)) {
                    // For the number of extra players
                    newPlayerList += PlayerHandler(
                        "",
                        "",
                        ImageHandler.randomBlank(),
                        null,
                        0,
                        false
                    )
                    // Add a player with no name or character and a blank character image
                    newPlayerList.last().fonts = playerList[0].fonts
                    // Set the new player font correctly
                    newPlayerList.last().useAlts = playerList[0].useAlts
                    // Match the alternate art setting
                    newPlayerList.last().addData(playerList[0].charList, playerList[0].playerList)
                    // Add the existing player and character lists to the new player handler
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

    fun addData(chars: Array<CharEntity>, players: Array<Player>) {
        charList = chars
        // Get the list of characters from the input
        charList.sortBy { it.charName }
        // Sorts the character list alphabetically
        charNames = charList.map { charEntity -> charEntity.charName }.toTypedArray()
        // Store the names from the list of characters

        playerList = players
        // Gets the list of players from the input
        playerList.sortBy { it.playerName }
        // Gets the sorted list of players
        playerNames = playerList.map { player -> player.playerName }.toTypedArray()
        // Stores the names of the players from the list of players
    }

    fun updateCharacter(newName: String) {
        if (charName != newName) {
            // If the character name has changed

            if (newName == "the lost" && soulsNum == 0) {
                // If they have just changed to the lost, and their souls number had not been updated
                soulsNum = 1
                // Set their souls to 1
            } else if (charName == "the lost" && soulsNum == 1) {
                // If they were the lost and their soul number was 1
                soulsNum = 0
                // Set their souls to 0
            }

            charName = newName
            // The character name is now the new name
            val pos = charNames.indexOf(charName)
            // Find where the character name is stored
            if (pos >= 0) {
                // if the character has an index (it exists)
                val currentChar = charList[pos]
                // Get the current character
                charImage = currentChar.image
                // Set the image to the basic value
                if (charName == "eden") {
                    // If Eden is selected
                    var imageArray = arrayOf(R.drawable.b2_eden)
                    // The basic eden image is always an option
                    if (useAlts) {
                        imageArray += arrayOf(R.drawable.aa_eden_1, R.drawable.aa_eden_2)
                    }
                    // Adds the 2 alt art Eden cards if alternate arts are an option
                    if (usePromo) {
                        imageArray += arrayOf(R.drawable.p_eden_1, R.drawable.p_eden_2, R.drawable.p_eden_3)
                    }
                    // Adds the 2 promo eden cards if promos are being used
                    if (useRequiem) {
                        imageArray += arrayOf(R.drawable.r_eden_1, R.drawable.r_eden_2)
                    }
                    // Adds the 2 requiem eden cards if requiem is being used
                    if (useRetro) {
                        imageArray += arrayOf(R.drawable.ret_eden)
                    }
                    // Adds the retro eden card if retro is being used
                    charImage = imageArray.random()
                    // Picks a random Eden card
                } else if ((currentChar.imageAlt != null) and useAlts) {
                    // If there is an alternate image, and the player wants the chance to use alternate art
                    charImage =
                        arrayOf(currentChar.image, currentChar.imageAlt).random()!!
                    // Select a random character image
                }
            } else if (newName == "") {
                // If the name is now blank
                charName = newName
                // The character name is now blank
                charImage = ImageHandler.randomBlank()
                // The image is now the blank image
            }
        }
    }
}