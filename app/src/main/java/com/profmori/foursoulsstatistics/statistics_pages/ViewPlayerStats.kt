package com.profmori.foursoulsstatistics.statistics_pages

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.RangeSlider
import com.profmori.foursoulsstatistics.R
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
        setContentView(R.layout.activity_view_stats)
        val fonts = TextHandler.setFont(this)
        // Get the fonts from the text handler

        val characterTitle = findViewById<TextView>(R.id.viewStatsTitle)
        // Gets the title
        characterTitle.text = resources.getString(R.string.player_stats_title)

        val filterText = findViewById<TextView>(R.id.filtersHeader)
        // Get the filter text

        val treasureText = findViewById<TextView>(R.id.treasureTitle)
        val playerText = findViewById<TextView>(R.id.playerTitle)
        // Get the treasure texts

        val backButton = findViewById<Button>(R.id.backToStats)
        val buttonBG = ImageHandler.setButtonImage()
        backButton.setBackgroundResource(buttonBG)
        // Get the button and set the image

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image

        SettingsHandler.updateBackground(this, background)
        // Set the background image

        characterTitle.typeface = fonts["title"]
        filterText.typeface = fonts["body"]
        playerText.typeface = fonts["body"]
        treasureText.typeface = fonts["body"]
        backButton.typeface = fonts["body"]
        // Set all button and title fonts

        backButton.setOnClickListener {
            val backToStats = Intent(this, StatisticsMenu::class.java)
            startActivity(backToStats)
        }

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
        val playerTable = findViewById<SortableTableView<StatsTable>>(R.id.statsTable)
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

        val playerHeaderAdapter = StatsTableHeaderAdapter(this, fonts["title"]!!, playerHeader)
        // Creates the header adapter
        playerTable.headerAdapter = playerHeaderAdapter
        // Applies the header adapter

        playerTable.setColumnComparator(0, NameComparator())
        playerTable.setColumnComparator(1, WinrateComparator())
        playerTable.setColumnComparator(2, SoulsComparator())
        playerTable.setColumnComparator(3, AdjustedSoulsComparator())
        // Allows all the columns to be sorted correctly

        playerTable.setHeaderBackgroundColor(ContextCompat.getColor(this, R.color.darker))
        playerTable.setBackgroundColor(ContextCompat.getColor(this, R.color.lighter))
        // Sets the table backgrounds as tints

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

            val playerDataAdapter =
                StatsTableDataAdapter(this@ViewPlayerStats, fonts["body"]!!, playerData)
            // Create the player table data adapter
            playerTable.dataAdapter = playerDataAdapter
            // Attach it to the table
            playerTable.sort(NameComparator())
            // Set the default sort: Alphabetical
        }
    }
}