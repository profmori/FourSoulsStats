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
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.launch

class ViewCommunityCharacterStats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stats)
        val fonts = TextHandler.setFont(this)
        // Get the fonts from the text handler

        val characterTitle = findViewById<TextView>(R.id.viewStatsTitle)
        // Gets the title
        characterTitle.text = resources.getString(R.string.char_stats_title)

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
            val gamesList = OnlineDataHandler.getAllGames(this@ViewCommunityCharacterStats)

            val treasures = gamesList.map { game -> game.treasureNum }
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

            val players = gamesList.map{game -> game.gameSize }
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
        val charTable = findViewById<SortableTableView<StatsTable>>(R.id.statsTable)
        // Finds the player stats table

        val fonts = TextHandler.setFont(this)
        // Get the fonts from the text handler

        charTable.columnCount = 4
        // Sets the number of columns

        val charHeader = arrayOf(
            resources.getString(R.string.character_table_header),
            resources.getString(R.string.stats_table_winrate),
            resources.getString(R.string.stats_table_souls),
            resources.getString(R.string.stats_table_adjusted_souls)
        )
        // Sets all the header strings

        val charHeaderAdapter = StatsTableHeaderAdapter(this, fonts["title"]!!, charHeader)
        // Creates the header adapter
        charTable.headerAdapter = charHeaderAdapter

        charTable.setHeaderBackgroundColor(ContextCompat.getColor(this, R.color.darker))
        charTable.setBackgroundColor(ContextCompat.getColor(this, R.color.lighter))
        // Sets the table backgrounds as tints

        charTable.setColumnComparator(0,
            NameComparator()
        )
        charTable.setColumnComparator(1,
            WinrateComparator()
        )
        charTable.setColumnComparator(2,
            SoulsComparator()
        )
        charTable.setColumnComparator(3,
            AdjustedSoulsComparator()
        )
        // Allows all the columns to be sorted

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

        lifecycleScope.launch {
            // As a coroutine

            edition.forEach { characters += gameDao.getCharacterList(it) }
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

            val charDataAdapter = StatsTableDataAdapter(this@ViewCommunityCharacterStats, fonts["body"]!!, characterData)
            // Create the character data adapter
            charTable.dataAdapter = charDataAdapter
            // Attach it to the table
            charTable.sort(AdjustedSoulsComparator())
            charTable.sort(SoulsComparator())
            charTable.sort(WinrateComparator())
            // Set the default sort: Winrate -> Avg Souls -> Adjusted Souls
        }
    }
}