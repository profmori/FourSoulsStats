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
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TableHandler
import com.profmori.foursoulsstatistics.database.*
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.launch

class ViewCommunityCharacterStats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stats)

        val characterTitle = findViewById<TextView>(R.id.viewStatsTitle)
        // Gets the title
        characterTitle.text = resources.getString(R.string.char_stats_title)
        // Set the title

        val filterText = findViewById<TextView>(R.id.filtersHeader)
        // Get the filter text

        val treasureText = findViewById<TextView>(R.id.treasureTitle)
        val playerText = findViewById<TextView>(R.id.playerTitle)
        // Get the slider texts

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
            val gamesList = OnlineDataHandler.getAllGames(this@ViewCommunityCharacterStats)

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
        val charTable = findViewById<SortableTableView<StatsTable>>(R.id.statsTable)
        // Finds the player stats table

        val charHeaders = arrayOf(
            resources.getString(R.string.character_table_header),
            resources.getString(R.string.stats_table_winrate),
            resources.getString(R.string.stats_table_souls),
            resources.getString(R.string.stats_table_adjusted_souls)
        )
        // Sets all the header strings

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        var characterData = emptyArray<StatsTable>()
        // Empty array to store character data

        var characters = emptyArray<CharEntity>()
        // Create an empty list of all characters

        val edition = SettingsHandler.getEditions(this)
        // Get the current editions from settings

        val editionList = edition.filter { it != "custom" }
        // Removes any custom characters from being shown in the community stats

        lifecycleScope.launch {
            // As a coroutine

            editionList.forEach { characters += gameDao.getCharacterList(it) }
            // Create a list of all characters in the used editions

            val games = OnlineDataHandler.getAllGames(this@ViewCommunityCharacterStats)
            // Get all games

            val filteredPlayers = games.filter {
                (it.gameSize >= playerRange[0].toInt()) and (it.gameSize <= playerRange[1].toInt())
            }
            // Get the games with the right number of players

            val selectedGames = filteredPlayers.filter {
                (it.treasureNum >= treasureRange[0].toInt()) and (it.treasureNum <= treasureRange[1].toInt())
            }.toTypedArray()
            // Get the games with the right number of treasures

            characters.forEach {
                // For every player
                val newCharTable = StatsTable(it.charName,0.0,0.0,0,0.0)
                // Create a new row
                newCharTable.setData(it.charName, selectedGames)
                // Update the data
                characterData += arrayOf(newCharTable)
                // Add it to the table
            }

            TableHandler.createTable(this@ViewCommunityCharacterStats, charTable, charHeaders, characterData)
            // Creates the table using the headers and data
        }
    }
}