package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.slider.RangeSlider
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.GameInstance
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import com.profmori.foursoulsstatistics.online_database.OnlineGameInstance
import com.profmori.foursoulsstatistics.statistics_pages.StatisticsMenu
import com.profmori.foursoulsstatistics.statistics_pages.StatsTable
import com.profmori.foursoulsstatistics.statistics_pages.TableRow

class TableHandler {
    companion object {
        suspend fun gatherData(
            tableType: String, coopBool: Boolean, onlineData: Boolean, context: Context
        ): Array<TableRow> {

            val settings = SettingsHandler.readSettings(context)
            // Get the settings for later use

            var returnRows = emptyArray<TableRow>()
            // Create an object to store the list of table rows generated
            var gamesList = emptyArray<Game>()
            // Create an object to store all the games that are being read

            val gameDatabase = GameDataBase.getDataBase(context)
            val gameDao = gameDatabase.gameDAO
            // Get the local database for later use

            if (onlineData) {
                val onlineData = OnlineDataHandler.getAllGames(context, coopBool)
                var onlineGames = onlineData.map { onlineInstance -> convertToGame(onlineInstance) }
                onlineGames = onlineGames.distinct()
                val onlineInstances =
                    onlineData.map { onlineInstance -> convertToGameInstance(onlineInstance) }
                when (tableType) {
                    "Character" -> {
                        var charMap = mutableMapOf<String, TableRow>()
                        onlineInstances.forEach { instance ->
                            if (instance.charName !in charMap.keys) {
                                charMap.put(instance.charName, TableRow(instance.charName))
                            }
                            charMap[instance.charName]!!.addInstance(instance)
                        }
                        gameDao.getFullCharacterList().forEach { char ->
                            val row = charMap[char.charName]
                            if (row is TableRow) {
                                returnRows += row
                            }
                        }
                    }

                    "Eternal" -> {
                        var eternalMap = mutableMapOf<String, TableRow>()
                        onlineInstances.forEach { instance ->
                            if (!instance.eternal.isNullOrEmpty()) {
                                if (instance.eternal !in eternalMap.keys) {
                                    eternalMap.put(instance.eternal, TableRow(instance.eternal))
                                }
                                eternalMap[instance.eternal]!!.addInstance(instance)
                            }
                        }
                        eternalMap.values.forEach { returnRows += it }
                    }

                    else -> {}
                }
                returnRows.forEach { row -> row.addGames(onlineGames.toTypedArray()) }
            } else {
                gamesList = gameDao.getGames(coopBool)
                when (tableType) {
                    "Player" -> {
                        val playerList = gameDao.getPlayers()
                        playerList.forEach { player ->
                            val tableRow = TableRow(player.playerName)
                            val instances = gameDao.getPlayerWithInstance(player.playerName)
                            val gameInstances = instances[0].gameInstances
                            tableRow.addInstances(convertInstances(gameInstances, coopBool))
                            returnRows += tableRow
                        }
                    }

                    "Character" -> {
                        val charList = gameDao.getFullCharacterList()
                        charList.forEach { character ->
                            val tableRow = TableRow(character.charName)
                            val instances = gameDao.getCharacterWithInstance(character.charName)
                            val gameInstances = instances[0].gameInstances
                            tableRow.addInstances(convertInstances(gameInstances, coopBool))
                            returnRows += tableRow
                        }
                    }

                    "Eternal" -> {
                        val eternalInstances = gameDao.getEternalList()
                        var eternalMap = mutableMapOf<String, TableRow>()
                        eternalInstances.forEach { instance ->
                            if (instance.eternal !in eternalMap.keys) {
                                eternalMap.put(instance.eternal!!, TableRow(instance.eternal))
                            }
                            eternalMap[instance.eternal]!!.addInstance(instance)
                        }
                        eternalMap.values.forEach { returnRows += it }
                    }

                    else -> {}
                }
                returnRows.forEach { row -> row.addGames(gamesList) }
            }


            return returnRows
        }

        fun pageSetup(
            context: Context,
            backButton: Button,
            headerChangeButton: Button,
            background: ImageView,
            characterTitle: TextView,
            filterText: TextView,
            playerText: TextView,
            treasureText: TextView
        ) {
            val buttonBG = ImageHandler.setButtonImage()
            backButton.setBackgroundResource(buttonBG)
            headerChangeButton.setBackgroundResource(buttonBG)
            // Get the button background and set the buttons to use it

            SettingsHandler.updateBackground(context, background)
            // Set the background image

            val fonts = TextHandler.setFont(context)
            // Get the fonts from the text handler

            characterTitle.typeface = fonts["title"]
            filterText.typeface = fonts["body"]
            playerText.typeface = fonts["body"]
            treasureText.typeface = fonts["body"]
            backButton.typeface = fonts["body"]
            headerChangeButton.typeface = fonts["body"]
            // Set all button and title fonts

            backButton.setOnClickListener {
                // When the back button is pressed
                val backToStats = Intent(context, StatisticsMenu::class.java)
                backToStats.putExtra("from", "Stats Page")
                startActivity(context, backToStats, null)
                // Go back to the statistics page
            }
        }

        private fun convertInstances(
            gameInstances: List<GameInstance>, coopBool: Boolean
        ): Array<GameInstance> {
            var instanceList = emptyArray<GameInstance>()
            gameInstances.forEach { instance ->
                if (instance.solo == coopBool) {
                    instanceList += instance
                }
            }
            return instanceList
        }

        private fun convertToGameInstance(onlineInstance: OnlineGameInstance): GameInstance {
            return GameInstance(
                -1,
                onlineInstance.gameID,
                onlineInstance.playerName,
                onlineInstance.charName,
                onlineInstance.eternal,
                onlineInstance.souls,
                onlineInstance.winner,
                onlineInstance.solo
            )
        }

        private fun convertToGame(onlineInstance: OnlineGameInstance): Game {
            return Game(
                onlineInstance.gameID,
                onlineInstance.gameSize,
                onlineInstance.treasureNum,
                true,
                onlineInstance.solo,
                onlineInstance.turnsLeft
            )
        }

        fun pageSetup(
            tableData: StatsTable,
            filterText: TextView,
            playerText: TextView,
            playerSlider: RangeSlider,
            treasureText: TextView,
            treasureSlider: RangeSlider
        ) {
            var gamesList = emptyArray<Game>()
            tableData.rows.forEach { row ->
                gamesList += row.storedGames.values
            }
            val treasures = gamesList.map { game -> game.treasureNo }
            // Get the list of treasure numbers
            val players = gamesList.map { game -> game.playerNo }
            // Get the list of player numbers

            val minTreasure = treasures.minOrNull()!!.toFloat()
            val maxTreasure = treasures.maxOrNull()!!.toFloat()
            // Get the range of treasure values

            treasureSlider.valueFrom = minTreasure
            treasureSlider.valueTo = maxTreasure
            // Set the slider limits
            treasureSlider.values = listOf(minTreasure, maxTreasure)
            // Set the current settings to the limits
            if (minTreasure == maxTreasure) {
                treasureText.visibility = View.GONE
                treasureSlider.visibility = View.GONE
            } else {
                treasureText.visibility = View.VISIBLE
                treasureSlider.visibility = View.VISIBLE
            }
            // If there is no range, don't allow this to be modified

            val minPlayer = players.minOrNull()!!.toFloat()
            val maxPlayer = players.maxOrNull()!!.toFloat()
            // Get the minimum and maximum number of players
            playerSlider.valueFrom = minPlayer
            playerSlider.valueTo = maxPlayer
            // Set the limits of the slider
            playerSlider.values = listOf(minPlayer, maxPlayer)
            // Set the current slider values to the limit
            if (minPlayer == maxPlayer) {
                playerText.visibility = View.GONE
                playerSlider.visibility = View.GONE
            } else {
                playerText.visibility = View.VISIBLE
                playerSlider.visibility = View.VISIBLE
            }
            // If there is no range, don't allow this to be modified

            if ((minPlayer == maxPlayer) and (minTreasure == maxTreasure)) {
                filterText.visibility = View.GONE
            } else {
                filterText.visibility = View.VISIBLE
            }
            // If there is nothing to filter, don't show filter text
        }
    }
}