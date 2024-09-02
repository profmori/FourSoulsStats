package com.profmori.foursoulsstatistics.custom_adapters

import android.content.Context
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.database.GameInstance
import com.profmori.foursoulsstatistics.online_database.OnlineGameInstance

class StatsTable(
    var name: String,
    var winrate: Double,
    var soulsAvg: Double,
    var playedGames: Int,
    var adjustedSouls: Double,
    var turnsAvg: Double,
    var excluded: Boolean = false
) {

    fun fromText(context: Context, header: String): String {
        return when (header) {
            context.resources.getString(R.string.stats_table_winrate) -> context.resources.getString(
                R.string.stats_table_entry
            ).format(winrate)

            context.resources.getString(R.string.stats_table_souls) -> context.resources.getString(R.string.stats_table_entry)
                .format(soulsAvg)

            context.resources.getString(R.string.stats_table_turns_remaining) -> context.resources.getString(
                R.string.stats_table_entry
            ).format(turnsAvg)

            context.resources.getString(R.string.stats_table_adjusted_souls) -> context.resources.getString(
                R.string.stats_table_entry
            ).format(adjustedSouls)

            else -> name
        }
    }

    private fun generateMetrics(wins: Double, souls: Double, adjSouls: Double, turns: Double) {
        winrate = wins / playedGames
        // Calculates the winrate
        soulsAvg = souls / playedGames
        // Calculates the average souls
        adjustedSouls = adjSouls / playedGames
        // Calculates the average adjusted souls
        turnsAvg = turns / playedGames
        // Calculates the average number of turns taken
        if (playedGames == 0) {
            // If the item is unplayed
            winrate = -1.0
            soulsAvg = -1.0
            adjustedSouls = -1.0
            turnsAvg = -1.0
            // Set all values to -1
        }
    }

    fun setData(rowName: String, instances: List<GameInstance>, games: Array<Game>) {
        // Function to set the data in the character table
        name = rowName
        // Gets name from the input
        val gamesList = games.map { game -> game.gameID }
        // Get the list of selected games

        playedGames = 0
        // Initialises played games counter
        var wins = 0.0
        // Initialise the number of wins
        var souls = 0.0
        // Initialise the number of souls
        var adjSouls = 0.0
        // Initialise the adjusted number of souls
        var turnsPlayed = 0.0
        // Initialise the total number of turns played

        instances.forEach {
            if (gamesList.contains(it.gameID)) {
                playedGames += 1
                if (it.winner) {
                    wins += 1
                }
                // If the character won the instance increment their win counter
                souls += it.souls
                // Increment the soul counter
                val currGame = games.map { game -> game.gameID }.indexOf(it.gameID)
                // Gets the current game id
                val gameSize = games[currGame].playerNo
                // Gets the size of the game based on the game ID
                adjSouls += it.souls * gameSize
                // Increments the adjusted number of souls
                turnsPlayed += games[currGame].turnsLeft
                // Initialise the total number of turns played
            }
        }
        generateMetrics(wins, souls, adjSouls, turnsPlayed)
        // Generates all the winrates and averages from the data
    }

    fun setData(rowName: String, tableType: String, instances: Array<OnlineGameInstance>) {
        name = rowName
        // Get the name from the input
        val gamesList = when (tableType) {
            "Character" -> instances.filter { instance -> instance.charName == name }
            "Eternal" -> instances.filter { instance -> instance.eternal == name }
            else -> instances.filter { false }
        }
        // Filter all the games to only include those with the correct item
        playedGames = 0
        // Initialises played games counter
        var wins = 0.0
        // Initialise the number of wins
        var souls = 0.0
        // Initialise the number of souls
        var adjSouls = 0.0
        // Initialise the adjusted number of souls
        var turnsPlayed = 0.0
        // Initialise the total number of turns played
        gamesList.forEach {
            playedGames += 1
            if (it.winner) {
                wins += 1
            }
            // If the character won the instance increment their win counter
            souls += it.souls
            // Increment the soul counter
            adjSouls += it.souls * it.gameSize
            // Increments the adjusted number of souls
            turnsPlayed += it.turnsLeft
            // Initialise the total number of turns played
        }
        generateMetrics(wins, souls, adjSouls, turnsPlayed)
        // Generates all the winrates and averages from the data
    }
}