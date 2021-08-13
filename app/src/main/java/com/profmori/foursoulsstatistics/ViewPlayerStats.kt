package com.profmori.foursoulsstatistics

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.RangeSlider
import com.profmori.foursoulsstatistics.custom_adapters.*
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

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image

        SettingsHandler.updateBackground(this, background)

        playerTitle.typeface = fonts["title"]
        // Set all button and title fonts

        val treasureSlider = findViewById<RangeSlider>(R.id.treasureSlider)

        val playerSlider = findViewById<RangeSlider>(R.id.playerSlider)

        lifecycleScope.launch {
            val gameDatabase = GameDataBase.getDataBase(this@ViewPlayerStats)
            val gameDao = gameDatabase.gameDAO
            val gamesList = gameDao.getGames()

            val treasures = gamesList.map { game -> game.treasureNo }
            val minTreasure = treasures.minOrNull()!!.toFloat()
            val maxTreasure = treasures.minOrNull()!!.toFloat()
            treasureSlider.valueFrom = minTreasure
            treasureSlider.valueTo = maxTreasure
            treasureSlider.values = listOf(minTreasure,maxTreasure)
            if(minTreasure == maxTreasure){
                treasureSlider.visibility = View.GONE
            }

            val players = gamesList.map{game -> game.playerNo }
            val minPlayer = players.minOrNull()!!.toFloat()
            val maxPlayer = players.minOrNull()!!.toFloat()
            playerSlider.valueFrom = minPlayer
            playerSlider.valueTo = maxPlayer
            playerSlider.values = listOf(minPlayer,maxPlayer)
            if(minPlayer == maxPlayer){
                playerSlider.visibility = View.GONE
            }

            if((minPlayer == maxPlayer) and (minTreasure == maxTreasure)){
                filterText.visibility = View.GONE
            }

            createTable(playerSlider.values, treasureSlider.values)
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