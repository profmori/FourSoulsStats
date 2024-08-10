package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.graphics.Typeface
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.Player

class PlayerHandler(
    var playerName: String,
    var charName: String,
    var charImage: Int,
    var eternal: String?,
    var soulsNum: Int,
    var winner: Boolean,
    var solo: Boolean
) {

    var charList = emptyArray<CharEntity>()
    var charNames = emptyArray<String>()
    var playerList = emptyArray<Player>()
    var playerNames = emptyArray<String>()

    // Creates variables for all the stored data about characters and players
    var fonts = emptyMap<String, Typeface>()
    // Global font variable

    private var useAlts = true
    // Variable for whether or not to use alternate art

    private var usePromo = true
    // Variable for whether or not to use promo eden cards

    private var useRequiem = true
    // Variable for whether or not to use requiem eden cards

    private var useRetro = true
    // Variable for whether or not to use retro eden card

    private var useUnboxing = true
    // Variable for whether or not to use unboxing of isaac alt arts

    private var useBumbo = true
    // Variable for whether or not to use bumbo bumbo

    private var useYoutooz = true
    // Variable for whether or not to use youtooz guppy

    private var useSummer = true
    // Variable for whether or not to use youtooz guppy


    companion object {
        fun makePlayerList(playerNum: Int): Array<PlayerHandler> {
            // Function to make the original array of players
            var players = emptyArray<PlayerHandler>()
            // Creates the array
            for (i in (1 until playerNum + 1)) {
                // Iterates through every player in the array
                players += (PlayerHandler(
                    "", "", ImageHandler.randomBlank(), null, 0, winner = false, solo = false
                ))
                // Add a player with no name or character and a blank character image
            }
            return players
            // Returns the array of players
        }

        fun updatePlayerList(
            playerList: Array<PlayerHandler>, playerNum: Int, context: Context
        ): Array<PlayerHandler> {
            // Function to update the number of players without resetting the recycler
            val newPlayerList = playerList.toMutableList()
            // Creates a mutable list that can be reduced or increased in length
            val currentLength = playerList.size
            // Gets the length of the player list

            var charNum = playerNum
            var newSolo = false

            if (playerNum == 1) {
                charNum = 2
                newSolo = true
            }

            if (currentLength < charNum) {
                // If the number of players has increased
                for (i in (currentLength until charNum)) {
                    // For the number of extra players
                    newPlayerList += PlayerHandler(
                        "", "", ImageHandler.randomBlank(), null, 0, winner = false, solo = false
                    )
                    // Add a player with no name or character and a blank character image
                    newPlayerList.last().fonts = playerList[0].fonts
                    // Set the new player font correctly
                    newPlayerList.last()
                        .addData(playerList[0].charList, playerList[0].playerList, context)
                    // Add the existing player and character lists to the new player handler
                }
            } else {
                // If the number of players has not increased
                while (newPlayerList.size > charNum) {
                    // While the current player list is too long
                    newPlayerList.removeAt(charNum)
                    // Remove the element that would be after the last one
                }
            }

            if (newPlayerList[0].solo != newSolo) {
                for (player in newPlayerList) {
                    player.solo = newSolo
                }
            }

            return newPlayerList.toTypedArray()
            // Returns the updated list of players
        }
    }

    fun addData(chars: Array<CharEntity>, players: Array<Player>, context: Context) {
        charList = chars
        // Get the list of characters from the input
        val settings = SettingsHandler.readSettings(context)
        // Get the current settings
        useAlts = settings["alt_art"].toBoolean()
        // Set the player handler flag for using alt art correctly
        usePromo = settings["promo"].toBoolean()
        // Set the player handler flag for using promo edens correctly
        useRequiem = settings["requiem"].toBoolean()
        // Set the player handler flag for using requiem edens correctly
        useRetro = settings["retro"].toBoolean()
        // Set the player handler flag for using retro edens correctly
        useUnboxing = settings["unboxing"].toBoolean()
        // Set the player handler flag for using unboxing cards correctly
        useBumbo = settings["bumbo"].toBoolean()
        // Set the player handler flag for using bumbo bumbo correctly
        useYoutooz = settings["youtooz"].toBoolean()
        // Set the player handler flag for using youtooz guppy correctly
        useSummer = settings["summer"].toBoolean()
        // Set the player handler flag for using summer of Isaac cards correctly

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
                var imageArray = arrayOf(currentChar.image)
                // Set the image to the basic value
                if (charName == "eden") {
                    // If Eden is selected
                    if (useAlts) {
                        imageArray += arrayOf(R.drawable.aa_eden_1, R.drawable.aa_eden_2)
                    }
                    // Adds the 2 alt art Eden cards if alternate arts are an option
                    if (usePromo) {
                        imageArray += arrayOf(
                            R.drawable.p_eden_1, R.drawable.p_eden_2, R.drawable.p_eden_3
                        )
                    }
                    // Adds the 2 promo eden cards if promos are being used
                    if (useRequiem) {
                        imageArray += arrayOf(R.drawable.r_eden_1, R.drawable.r_eden_2)
                    }
                    // Adds the 2 requiem eden cards if requiem is being used
                    if (useRetro) {
                        imageArray += arrayOf(R.drawable.ret_eden)
                    }
                    // Adds the retro eden card if retro is being use
                    // Picks a random Eden card
                } else {
                    if ((currentChar.imageAlt != null) and useAlts) {
                        // If there is an alternate image, and the player wants the chance to use alternate art
                        val altArt = currentChar.imageAlt
                        if (altArt != null) {
                            imageArray += arrayOf(altArt)
                        }
                    }

                    if (useUnboxing or useBumbo or useYoutooz or useSummer) {
                        val limitedImage = ImageHandler.getLimitedAlt(charName)
                        if (limitedImage > 0) {
                            imageArray += arrayOf(limitedImage)
                        }
                    }
                }
                charImage = imageArray.random()
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