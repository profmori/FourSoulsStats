package com.profmori.foursoulsstatistics.statistics_pages

import android.content.Context
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.database.GameInstance

class TableRow(rowName: String) {
    val rowName = TextHandler.capitalise(rowName)
    private val storedInstances = mutableMapOf<GameInstance, Boolean>()
    val storedGames = mutableMapOf<String, Game>()

    fun addGames(gameList: Array<Game>) {
        val gameIDList = storedInstances.keys.map { it -> it.gameID }
        gameList.forEach { game ->
            if (game.gameID in gameIDList) {
                storedGames.put(game.gameID, game)
            }
        }
    }

    fun addInstance(instance: GameInstance) {
        storedInstances.put(instance, true)
    }

    fun addInstances(instanceList: Array<GameInstance>) {
        instanceList.forEach { instance -> storedInstances.put(instance, true) }
    }

    fun filterData(
        playerLimits: List<Float>, treasureLimits: List<Float>
    ) {
        storedInstances.keys.forEach { instance ->
            val gameID = instance.gameID
            val relevantGame = storedGames[gameID]!!
            storedInstances[instance] =
                !(relevantGame.playerNo < playerLimits[0] || relevantGame.playerNo > playerLimits[1] || relevantGame.treasureNo < treasureLimits[0] || relevantGame.treasureNo > treasureLimits[1])
        }

    }

    fun getDataFromHeader(headerName: String, context: Context): Any {
        var gamesPlayed = emptyArray<String>()
        val updatedVal = when (headerName) {
            context.resources.getString(R.string.stats_table_winrate) -> {
                var gamesWon = 0f
                storedInstances.keys.forEach { instance ->
                    if (storedInstances[instance]!!) {
                        gamesPlayed += instance.gameID
                        if (instance.winner) {
                            gamesWon += 1
                        }
                    }
                }
                gamesWon / gamesPlayed.distinct().size
            }

            context.resources.getString(R.string.stats_table_wins) -> {
                var gamesWon = 0
                storedInstances.keys.forEach { instance ->
                    if (storedInstances[instance]!!) {
                        gamesPlayed += instance.gameID
                        if (instance.winner) {
                            gamesWon += 1
                        }
                    }
                }
                gamesWon
            }

            context.resources.getString(R.string.stats_table_soulrate) -> {
                var soulsWon = 0f
                storedInstances.keys.forEach { instance ->
                    if (storedInstances[instance]!!) {
                        gamesPlayed += instance.gameID
                        soulsWon += instance.souls
                    }
                }
                soulsWon / gamesPlayed.distinct().size
            }

            context.resources.getString(R.string.stats_table_souls) -> {
                var soulsWon = 0
                storedInstances.keys.forEach { instance ->
                    if (storedInstances[instance]!!) {
                        gamesPlayed += instance.gameID
                        soulsWon += instance.souls
                    }
                }
                soulsWon
            }

            context.resources.getString(R.string.stats_table_turns_remaining) -> {
                var turnsRemaining = 0f
                storedInstances.keys.forEach { instance ->
                    if (storedInstances[instance]!!) {
                        gamesPlayed += instance.gameID
                        val currentGame = storedGames[instance.gameID]!!
                        turnsRemaining += currentGame.turnsLeft
                    }
                }
                turnsRemaining / gamesPlayed.distinct().size
            }

            context.resources.getString(R.string.stats_table_adjusted_soulrate) -> {
                var adjustedSouls = 0f
                storedInstances.keys.forEach { instance ->
                    if (storedInstances[instance]!!) {
                        gamesPlayed += instance.gameID
                        val currentGame = storedGames[instance.gameID]!!
                        adjustedSouls += instance.souls * currentGame.playerNo
                    }
                }
                adjustedSouls / gamesPlayed.distinct().size
            }

            context.resources.getString(R.string.stats_table_adjusted_souls) -> {
                var adjustedSouls = 0
                storedInstances.keys.forEach { instance ->
                    if (storedInstances[instance]!!) {
                        gamesPlayed += instance.gameID
                        val currentGame = storedGames[instance.gameID]!!
                        adjustedSouls += instance.souls * currentGame.playerNo
                    }
                }
                adjustedSouls
            }

            context.resources.getString(R.string.stats_table_played) -> {
                storedInstances.keys.forEach { instance ->
                    if (storedInstances[instance]!!) {
                        gamesPlayed += instance.gameID
                    }
                }
                gamesPlayed.distinct().size
            }

            else -> rowName
        }
        return updatedVal
    }
}

class RowComparator(val header: TableHeader, val context: Context) : Comparator<TableRow> {
    override fun compare(
        row1: TableRow, row2: TableRow
    ): Int {
        var sortDir = 0
        val row1Value = row1.getDataFromHeader(header.headerName, context)
        val row2Value = row2.getDataFromHeader(header.headerName, context)
        if (row1Value is String) {
            sortDir = (row2Value as String).compareTo(row1Value)
        } else if (row1Value is Float) {
            sortDir = if (row1Value.isNaN()) {
                1
            } else if ((row2Value as Float).isNaN()) {
                -1
            } else {
                row1Value.compareTo(row2Value)
            }
        } else if (row1Value is Int) {
            sortDir = row1Value.compareTo(row2Value as Int)
        }

        if (header.sortDescending) {
            if ((row1Value !is Float) || (!row1Value.isNaN() && !(row2Value as Float).isNaN())) sortDir *= -1
        }
        return sortDir
    }
}