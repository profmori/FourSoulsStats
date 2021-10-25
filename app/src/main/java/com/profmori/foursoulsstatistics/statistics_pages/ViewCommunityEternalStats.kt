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
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.launch

class ViewCommunityEternalStats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stats)

        val characterTitle = findViewById<TextView>(R.id.viewStatsTitle)
        // Gets the title
        characterTitle.text = resources.getString(R.string.eternal_stats_title)

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
            val gamesList = OnlineDataHandler.getAllEternals(this@ViewCommunityEternalStats)

            TableHandler.onlineDataSetup(gamesList, filterText, playerText, playerSlider, treasureText, treasureSlider)
            // Setup the data from an online source

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
        val eternalTable = findViewById<SortableTableView<StatsTable>>(R.id.statsTable)
        // Finds the player stats table

        val eternalHeaders = arrayOf(
            resources.getString(R.string.eternal_table_header),
            resources.getString(R.string.stats_table_winrate),
            resources.getString(R.string.stats_table_souls),
            resources.getString(R.string.stats_table_adjusted_souls)
        )
        // Sets all the header strings

        var eternalData = emptyArray<StatsTable>()
        // Empty array to store character data

        lifecycleScope.launch {
            // As a coroutine

            val games = OnlineDataHandler.getAllEternals(this@ViewCommunityEternalStats)
            // Get all games

            val onlineEternals = games.map{game -> game.eternal}.distinct()
            // Gets all the eternals with recorded data

            val indexList = emptyList<Int>().toMutableList()
            // Creates a list of all the indices of chosen elements of the list
            ItemList.getItems(this@ViewCommunityEternalStats).forEach {
                // Iterate through all the selected eternal items
                val index = onlineEternals.indexOf(it.lowercase())
                // Get the index of the eternal in the list of online eternals
                if (index >= 0){
                    // If it is in the list
                    indexList += index
                    // Add its index to the list
                }
                else if (it == "\"I Can't Believe It's Not Butter Bean\""){
                    // "I Can't Believe It's Not Butter Bean" - the cause of every special case in the app
                    indexList += onlineEternals.indexOf("\"i can't believe it's not")
                    // Add the cut off version's index to the list
                }
                // Add the index to the list
            }

            val tableEternals = onlineEternals.slice(indexList)
            // Gets the intersection between the currently selected treasures and the treasures with data
            val filteredPlayers = games.filter {
                (it.gameSize >= playerRange[0].toInt()) and (it.gameSize <= playerRange[1].toInt())
            }
            // Get the games with the right number of players

            val selectedGames = filteredPlayers.filter {
                (it.treasureNum >= treasureRange[0].toInt()) and (it.treasureNum <= treasureRange[1].toInt())
            }.toTypedArray()
            // Get the games with the right number of treasures

            tableEternals.forEach {
                // For every eternal
                if(!it.isNullOrBlank()) {
                    // Make sure all inputs are safe
                    val newEternalTable = StatsTable(it, 0.0, 0.0, 0, 0.0)
                    // Create a new row
                    newEternalTable.setEternalData(it, selectedGames)
                    // Update the data
                    eternalData += arrayOf(newEternalTable)
                    // Add it to the table
                }
            }

            TableHandler.createTable(this@ViewCommunityEternalStats, eternalTable, eternalHeaders, eternalData)
            // Creates the table using the headers and data
        }
    }
}