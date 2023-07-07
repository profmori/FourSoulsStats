package com.profmori.foursoulsstatistics.statistics_pages

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.RangeSlider
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.custom_adapters.StatsTable
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TableHandler
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import com.profmori.foursoulsstatistics.online_database.OnlineGameInstance
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.launch

class ViewStatisticsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stats)

        val characterTitle = findViewById<TextView>(R.id.viewStatsTitle)
        // Gets the title

        val tableType = intent.getStringExtra("firstColumn")

        characterTitle.setText(
            when (tableType) {
                "Player" -> R.string.player_stats_title
                "Character" -> R.string.character_stats_title
                "Eternal" -> R.string.eternal_stats_title
                else -> R.string.error_placeholder
            }
        )

        val filterText = findViewById<TextView>(R.id.filtersHeader)
        // Get the filter text

        val treasureText = findViewById<TextView>(R.id.treasureTitle)
        val playerText = findViewById<TextView>(R.id.playerTitle)
        // Get the slider titles

        val playerSlider = findViewById<RangeSlider>(R.id.playerSlider)
        val treasureSlider = findViewById<RangeSlider>(R.id.treasureSlider)
        // Get the sliders

        val backButton = findViewById<Button>(R.id.backToStats)
        // Gets the button to go back to the statistics menu

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image

        TableHandler.pageSetup(
            this,
            backButton,
            background,
            characterTitle,
            filterText,
            playerText,
            treasureText
        )
        // Setup the page correctly

        lifecycleScope.launch {

            var gamesList = emptyArray<Game>()

            val onlineGame = intent.getBooleanExtra("online", false)
            val coopPage = intent.getBooleanExtra("coop", false)

            if (onlineGame) {
                val onlineList = OnlineDataHandler.getAllGames(this@ViewStatisticsPage, coopPage)
                onlineList.forEach {
                    gamesList += makeLocal(it)
                }
            } else {
                val gameDatabase = GameDataBase.getDataBase(this@ViewStatisticsPage)
                val gameDao = gameDatabase.gameDAO
                gamesList = gameDao.getGames(coopPage)
                // Get a list of games
            }

            TableHandler.dataSetup(
                gamesList,
                filterText,
                playerText,
                playerSlider,
                treasureText,
                treasureSlider
            )
            // Set up the table from the local data

            val playerTable = findViewById<SortableTableView<StatsTable>>(R.id.statsTable)
            // Finds the stats table

            if ((playerSlider.visibility == View.GONE) or (treasureSlider.visibility == View.GONE)) {
                println("test")
                playerTable.headerAdapter.notifyDataSetChanged()
            }

            playerSlider.addOnChangeListener { _, _, _ ->
                makeTable(
                    tableType,
                    onlineGame,
                    coopPage,
                    playerSlider.values,
                    treasureSlider.values
                )
                // Create the table with the up to date data
            }
            // When the player slider is changed update the table

            treasureSlider.addOnChangeListener { _, _, _ ->
                makeTable(
                    tableType,
                    onlineGame,
                    coopPage,
                    playerSlider.values,
                    treasureSlider.values
                )
                // Create the table with the up to date data
            }
            // When the treasure slider is changed update the table

            makeTable(tableType, onlineGame, coopPage, playerSlider.values, treasureSlider.values)
            // Create the table with the up to date data
        }
    }

    private fun makeTable(
        tableType: String?,
        online: Boolean,
        coopPage: Boolean,
        playerRange: List<Float>,
        treasureRange: List<Float>
    ) {

        val playerTable = findViewById<SortableTableView<StatsTable>>(R.id.statsTable)
        // Finds the stats table

        val tableHeader = when (tableType) {
            "Player" -> R.string.player_table_header
            "Character" -> R.string.character_table_header
            "Eternal" -> R.string.eternal_table_header
            else -> R.string.error_placeholder
        }

        val tableHeaders = if (coopPage) {
            arrayOf(
                resources.getString(tableHeader),
                resources.getString(R.string.stats_table_winrate),
                resources.getString(R.string.stats_table_souls),
                resources.getString(R.string.stats_table_turns_remaining)
            )
        } else {
            arrayOf(
                resources.getString(tableHeader),
                resources.getString(R.string.stats_table_winrate),
                resources.getString(R.string.stats_table_souls),
                resources.getString(R.string.stats_table_adjusted_souls)
            )
        }
        // Sets all the header strings

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        var tableData = emptyArray<StatsTable>()
        // Empty array to store player data

        lifecycleScope.launch {
            // As a coroutine
            var stats = emptyArray<TableItem>()

            when (tableType) {
                "Player" -> {
                    val playerData = gameDao.getPlayers()
                    stats = TableItem.convert(playerData)
                }

                "Character" -> {
                    var characterData = emptyArray<CharEntity>()
                    // Create an empty list of all characters
                    val edition = SettingsHandler.getEditions(this@ViewStatisticsPage)
                    // Get the current editions from settings
                    edition.forEach { characterData += gameDao.getCharacterList(it) }
                    // Create a list of all characters in the used editions
                    stats = TableItem.convert(characterData)
                }

                else -> {}
            }

            if (online) {
                val onlineGames = when (tableType) {
                    "Character" -> OnlineDataHandler.getAllGames(this@ViewStatisticsPage, coopPage)
                    "Eternal" -> OnlineDataHandler.getAllEternals(this@ViewStatisticsPage, coopPage)
                    else -> emptyArray()
                }

                if (tableType == "Eternal") {
                    val eternalList = onlineGames.map { game -> game.eternal }
                    stats = TableItem.convert(eternalList, this@ViewStatisticsPage)
                }

                val filteredOnlinePlayers = onlineGames.filter {
                    (it.gameSize >= playerRange[0].toInt()) and (it.gameSize <= playerRange[1].toInt())
                }
                // Get the online games with the right number of players

                val selectedOnlineGames = filteredOnlinePlayers.filter {
                    (it.treasureNum >= treasureRange[0].toInt()) and (it.treasureNum <= treasureRange[1].toInt())
                }.toTypedArray()
                // Get the online games with the right number of treasures

                stats.forEach {
                    // For every item in the table

                    val newPlayerTable = StatsTable(it.name, 0.0, 0.0, 0, 0.0, 0.0)
                    // Create a new row

                    newPlayerTable.setData(it.name, tableType!!, selectedOnlineGames)
                    // Update the data
                    tableData += arrayOf(newPlayerTable)
                    // Add it to the array
                }

            } else {
                val games = gameDao.getGames(coopPage)
                // Get all games

                val filteredPlayers = games.filter {
                    (it.playerNo >= playerRange[0].toInt()) and (it.playerNo <= playerRange[1].toInt())
                }
                // Get the games with the right number of players

                val selectedGames = filteredPlayers.filter {
                    (it.treasureNo >= treasureRange[0].toInt()) and (it.treasureNo <= treasureRange[1].toInt())
                }.toTypedArray()
                // Get the games with the right number of treasures

                stats.forEach {
                    // For every item in the table

                    val newPlayerTable = StatsTable(it.name, 0.0, 0.0, 0, 0.0, 0.0)
                    // Create a new row

                    val gameInstances = when (tableType) {
                        "Player" -> gameDao.getPlayerWithInstance(it.name)[0].gameInstances
                        "Character" -> gameDao.getCharacterWithInstance(it.name)[0].gameInstances
                        else -> emptyList()
                    }
                    // Get all game instances that value has played, and extract just the instances

                    newPlayerTable.setData(it.name, gameInstances, selectedGames)
                    // Update the data
                    tableData += arrayOf(newPlayerTable)
                    // Add it to the array
                }
            }

            TableHandler.createTable(
                this@ViewStatisticsPage,
                playerTable,
                tableHeaders,
                tableData,
                coopPage
            )
            // Creates the table using the headers and data
        }
    }

    private fun makeLocal(online: OnlineGameInstance): Game {
        return Game(
            online.gameID,
            online.gameSize,
            online.treasureNum,
            true,
            online.coop,
            online.turnsLeft
        )
    }
}