package com.example.foursoulsstatistics

import android.os.Parcelable
import com.example.foursoulsstatistics.database.CharEntity
import com.example.foursoulsstatistics.database.GameDAO
import com.example.foursoulsstatistics.database.GameDataBase
import com.example.foursoulsstatistics.database.Player
import kotlinx.parcelize.IgnoredOnParcel
import java.util.stream.IntStream.range
import kotlin.collections.ArrayList

@kotlinx.parcelize.Parcelize
// Makes the class parcelable so it can be passed between activities - does not pass the basic character mappings
class PlayerHandler (var playerName: String, var charName: String, var charImage: Int, var soulsNum: Int, var winner: Boolean) : Parcelable {

    // Base Game Characters
    @IgnoredOnParcel
    private val baseMap = mapOf(
        "blue baby" to listOf(R.drawable.blue_baby, R.drawable.blue_baby_alt),
        "cain" to listOf(R.drawable.cain),
        "eden" to listOf(R.drawable.eden),
        "eve" to listOf(R.drawable.eve),
        "the forgotten" to listOf(R.drawable.forgotten),
        "isaac" to listOf(R.drawable.isaac, R.drawable.isaac_alt),
        "judas" to listOf(R.drawable.judas, R.drawable.judas_alt),
        "lazarus" to listOf(R.drawable.lazarus),
        "lilith" to listOf(R.drawable.lilith, R.drawable.lilith_alt),
        "maggy" to listOf(R.drawable.maggy),
        "samson" to listOf(R.drawable.samson)
    )

    // Gold Box Characters
    @IgnoredOnParcel
    private val goldMap = mapOf(
        "apollyon" to listOf(R.drawable.apollyon),
        "azazel" to listOf(R.drawable.azazel),
        "the keeper" to listOf(R.drawable.keeper, R.drawable.keeper_alt),
        "the lost" to listOf(R.drawable.lost, R.drawable.lost_alt)
    )

    // FS+ Characters
    @IgnoredOnParcel
    private val plusMap = mapOf(
        "bum-bo" to listOf(R.drawable.bumbo),
        "dark judas" to listOf(R.drawable.dark_judas),
        "guppy" to listOf(R.drawable.guppy, R.drawable.guppy_alt),
        "whore of babylon" to listOf(R.drawable.whore)
    )

    @IgnoredOnParcel
    private val requiemMap = mapOf(
        // Requiem Characters
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
        "the harlot" to listOf(R.drawable.harlot, R.drawable.harlot_alt),
        "the hoarder" to listOf(R.drawable.hoarder),
        "the miser" to listOf(R.drawable.miser),
        "the savage" to listOf(R.drawable.savage, R.drawable.savage_alt),
        "the soiled" to listOf(R.drawable.soiled),
        "the zealot" to listOf(R.drawable.zealot)
    )

    @IgnoredOnParcel
    private val warpMap = mapOf(
        // Warp Zones
        "ash" to listOf(R.drawable.ash),
        "blind johnny" to listOf(R.drawable.blind_johnny),
        "blue archer" to listOf(R.drawable.blue_archer),
        "boyfriend" to listOf(R.drawable.boyfriend),
        "bum-bo the weird" to listOf(R.drawable.bumbo_weird),
        "captain viridian" to listOf(R.drawable.captain_viridian),
        "crewmate" to listOf(R.drawable.crewmate),
        "guy spelunky" to listOf(R.drawable.guy_spelunky),
        "pink knight" to listOf(R.drawable.pink_knight),
        "psycho goreman" to listOf(R.drawable.psycho_goreman),
        "quote" to listOf(R.drawable.quote),
        "salad fingers" to listOf(R.drawable.salad_fingers),
        "steven" to listOf(R.drawable.steven),
        "the knight" to listOf(R.drawable.knight),
        "the silent" to listOf(R.drawable.silent),
        "yung venuz" to listOf(R.drawable.yung_venuz)
    )

    @IgnoredOnParcel
    val imageMap = baseMap + goldMap + plusMap + requiemMap + warpMap
    // Combine all the character maps into one big map - this can be done in settings

    companion object {
        fun makePlayerList(playerNum: Int): ArrayList<PlayerHandler> {
            // Function to make the original array of players
            val players = ArrayList<PlayerHandler>()
            // Creates the array
            for (i in range(1, playerNum + 1)) {
                // Iterates through every player in the array
                players.add(PlayerHandler("", "", R.drawable.blank_char,0, false))
                // Add a player with no name or character and a blank character image
            }
            return players
            // Returns the array of players
        }

        fun updatePlayerList(
            playerList: ArrayList<PlayerHandler>,
            playerNum: Int
        ): ArrayList<PlayerHandler> {
            // Function to update the number of players without resetting the recycler
            val currentLength = playerList.size
            // Gets the length of the player list
            if (currentLength < playerNum) {
                // If the number of players has increased
                for (i in range(currentLength, playerNum)) {
                    // For the number of extra players
                    playerList.add(PlayerHandler("", "", R.drawable.blank_char, 0, false))
                    // Add a player with no name or character and a blank character image
                }
            } else {
                // If the number of players has not increased
                while (playerList.size > playerNum) {
                    // While the current player list is too long
                    playerList.removeAt(playerNum)
                    // Remove the element that would be after the last one
                }
            }
            return playerList
            // Returns the updated list of players
        }
    }
}