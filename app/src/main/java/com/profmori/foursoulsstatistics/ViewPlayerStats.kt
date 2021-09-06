package com.profmori.foursoulsstatistics

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.RangeSlider
import com.profmori.foursoulsstatistics.custom_adapters.*
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.*
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.launch

class ViewPlayerStats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_player_stats)
        val fonts = TextHandler.setFont(this)
        // Get the fonts from the text handler

        val playerTitle = findViewById<TextView>(R.id.playerStatsTitle)
        // Gets the player title

        val filterText = findViewById<TextView>(R.id.filtersHeader)
        // Get the filter text

        val treasureText = findViewById<TextView>(R.id.treasureTitle)
        val playerText = findViewById<TextView>(R.id.playerTitle)
        // Get the treasure texts

        val backButton = findViewById<Button>(R.id.playerToStats)
        val buttonBG = ImageHandler.setButtonImage()
        backButton.setBackgroundResource(buttonBG)
        // Get the button and set the image

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image

        SettingsHandler.updateBackground(this, background)
        // Set the background image

        playerTitle.typeface = fonts["title"]
        filterText.typeface = fonts["body"]
        playerText.typeface = fonts["body"]
        treasureText.typeface = fonts["body"]
        backButton.typeface = fonts["body"]
        // Set all button and title fonts

        val playerSlider = findViewById<RangeSlider>(R.id.playerSlider)
        val treasureSlider = findViewById<RangeSlider>(R.id.treasureSlider)
        // Get the sliders

        lifecycleScope.launch {
            val gameDatabase = GameDataBase.getDataBase(this@ViewPlayerStats)
            val gameDao = gameDatabase.gameDAO
            val gamesList = gameDao.getGames()
            // Get a list of games

            val treasures = gamesList.map { game -> game.treasureNo }
            // Get the list of treasure numbers
            val minTreasure = treasures.minOrNull()!!.toFloat()
            val maxTreasure = treasures.maxOrNull()!!.toFloat()
            // Get the range of treasure values
            treasureSlider.valueFrom = minTreasure
            treasureSlider.valueTo = maxTreasure
            // Set the slider limits
            treasureSlider.values = listOf(minTreasure,maxTreasure)
            // Set the current settings to the limits
            if(minTreasure == maxTreasure){
                treasureText.visibility = View.GONE
                treasureSlider.visibility = View.GONE
            }
            // If there is no range, don't allow this to be modified

            val players = gamesList.map{game -> game.playerNo }
            // Get the list of player numbers
            val minPlayer = players.minOrNull()!!.toFloat()
            val maxPlayer = players.maxOrNull()!!.toFloat()
            // Get the minimum and maximum number of players
            playerSlider.valueFrom = minPlayer
            playerSlider.valueTo = maxPlayer
            // Set the limits of the slider
            playerSlider.values = listOf(minPlayer,maxPlayer)
            // Set the current slider values to the limit
            if(minPlayer == maxPlayer){
                playerText.visibility = View.GONE
                playerSlider.visibility = View.GONE
            }
            // If there is no range, don't allow this to be modified

            if((minPlayer == maxPlayer) and (minTreasure == maxTreasure)){
                filterText.visibility = View.GONE
            }
            // If there is nothing to filter, don't show filter text

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
        val playerTable = findViewById<SortableTableView<PlayerTable>>(R.id.playerTable)
        // Finds the player stats table

        val fonts = TextHandler.setFont(this)
        // Get the fonts from the text handler

        playerTable.columnCount = 4
        // Sets it to 4 columns

        val playerHeader = arrayOf(
            resources.getString(R.string.player_table_header),
            resources.getString(R.string.stats_table_winrate),
            resources.getString(R.string.stats_table_souls),
            resources.getString(R.string.stats_table_adjusted_souls)
        )
        // Sets the table headers for the player table

        val playerHeaderAdapter = PlayerTableHeaderAdapter (this, fonts["title"]!!, playerHeader)
        // Creates the header adapter
        playerTable.headerAdapter = playerHeaderAdapter
        // Applies the header adapter

        playerTable.setColumnComparator(0, PlayerComparator())
        playerTable.setColumnComparator(1, WinrateComparator())
        playerTable.setColumnComparator(2, SoulsComparator())
        playerTable.setColumnComparator(3, AdjustedSoulsComparator())
        // Allows all the columns to be sorted correctly

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            playerTable.setHeaderBackgroundColor(resources.getColor(R.color.darker,theme))
            playerTable.setBackgroundColor(resources.getColor(R.color.lighter,theme))
            // Sets the table to be tints so the background comes through
        }

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        var playerData = emptyArray<PlayerTable>()
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
                val newPlayerTable = PlayerTable(it.playerName, 0.0, 0.0, 0, 0.0)
                // Create a new row
                val playerInstanceArray = gameDao.getPlayerWithInstance(it.playerName)
                // Get all game instances that player has played
                newPlayerTable.setData(playerInstanceArray, selectedGames)
                // Update the data
                if(!newPlayerTable.winrate.isNaN()){
                    // If the data is valid
                    playerData += arrayOf(newPlayerTable)
                    // Add it to the array
                }
            }

            val playerDataAdapter =
                PlayerTableDataAdapter(this@ViewPlayerStats, fonts["body"]!!, playerData)
            // Create the player table data adapter
            playerTable.dataAdapter = playerDataAdapter
            // Attach it to the table
            playerTable.sort(PlayerComparator())
            // Set the default sort
        }
    }
}