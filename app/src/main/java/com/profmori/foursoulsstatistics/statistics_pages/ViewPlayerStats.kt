package com.profmori.foursoulsstatistics.statistics_pages

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.RangeSlider
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.custom_adapters.*
import com.profmori.foursoulsstatistics.data_handlers.TableHandler
import com.profmori.foursoulsstatistics.database.*
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.launch

class ViewPlayerStats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stats)

        val characterTitle = findViewById<TextView>(R.id.viewStatsTitle)
        // Gets the title
        characterTitle.text = resources.getString(R.string.player_stats_title)

        val filterText = findViewById<TextView>(R.id.filtersHeader)
        // Get the filter text

        val treasureText = findViewById<TextView>(R.id.treasureTitle)
        val playerText = findViewById<TextView>(R.id.playerTitle)
        // Get the treasure texts

        val playerSlider = findViewById<RangeSlider>(R.id.playerSlider)
        val treasureSlider = findViewById<RangeSlider>(R.id.treasureSlider)
        // Get the sliders

        val backButton = findViewById<Button>(R.id.backToStats)
        // Gets the button to go back to the statistics menu

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image

        TableHandler.pageSetup(this,backButton, background, characterTitle, filterText, playerText, treasureText)
        // Setup the page correctly

        lifecycleScope.launch {
            val gameDatabase = GameDataBase.getDataBase(this@ViewPlayerStats)
            val gameDao = gameDatabase.gameDAO
            val gamesList = gameDao.getGames()
            // Get a list of games

            TableHandler.localDataSetup(gamesList, filterText, playerText, playerSlider, treasureText, treasureSlider)
            // Set up the table from the local data

            playerSlider.addOnChangeListener { _, _, _ ->
                createTable(playerSlider.values, treasureSlider.values)
                // Create the table with the up to date data
            }
            // When the player slider is changed update the table

            treasureSlider.addOnChangeListener { _, _, _ ->
                createTable(playerSlider.values, treasureSlider.values)
                // Create the table with the up to date data
            }
            // When the treasure slider is changed update the table

            createTable(playerSlider.values, treasureSlider.values)
            // Create the table with the up to date data
        }
    }

    private fun createTable(playerRange: List<Float>, treasureRange: List<Float>){
        val playerTable = findViewById<SortableTableView<StatsTable>>(R.id.statsTable)
        // Finds the player stats table

        val playerHeaders = arrayOf(
            resources.getString(R.string.player_table_header),
            resources.getString(R.string.stats_table_winrate),
            resources.getString(R.string.stats_table_souls),
            resources.getString(R.string.stats_table_adjusted_souls)
        )
        // Sets the table headers for the player table

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        var playerData = emptyArray<StatsTable>()
        // Empty array to store player data

        lifecycleScope.launch {
            // As a coroutine
            val players = gameDao.getPlayers()
            // Get all players

            val games = gameDao.getGames()
            // Get all games

            val filteredPlayers = games.filter {
                (it.playerNo >= playerRange[0].toInt()) and (it.playerNo <= playerRange[1].toInt())
            }
            // Get the games with the right number of players

            val selectedGames = filteredPlayers.filter {
                (it.treasureNo >= treasureRange[0].toInt()) and (it.treasureNo <= treasureRange[1].toInt())
            }.toTypedArray()
            // Get the games with the right number of treasures

            players.forEach {
                // For every player
                val newPlayerTable = StatsTable(it.playerName, 0.0, 0.0, 0, 0.0)
                // Create a new row
                val playerInstanceArray = gameDao.getPlayerWithInstance(it.playerName)
                // Get all game instances that player has played
                val gameInstances = playerInstanceArray[0].gameInstances
                // Extract just the instances
                newPlayerTable.setData(it.playerName, gameInstances, selectedGames)
                // Update the data
                playerData += arrayOf(newPlayerTable)
                // Add it to the array
            }

            TableHandler.createTable(this@ViewPlayerStats, playerTable, playerHeaders, playerData)
            // Creates the table using the headers and data
        }
    }
}