package com.profmori.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.*
import de.codecrafters.tableview.SortableTableView
import kotlinx.coroutines.launch


class ViewStatistics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_statistics)

        val fonts = TextHandler.setFont(this)
        // Get the fonts from the text handler

        val playerTitle = findViewById<TextView>(R.id.statsPlayerTitle)
        // Gets the player title

        val charTitle = findViewById<TextView>(R.id.statsCharTitle)
        // Gets the character title

        val returnButton = findViewById<Button>(R.id.statsBackButton)
        // Gets the back button

        val background = findViewById<ImageView>(R.id.background)

        SettingsHandler.updateBackground(this, background)

        if (playerTitle.typeface != fonts["body"]){
            playerTitle.typeface = fonts["body"]
            charTitle.typeface = fonts["body"]
            returnButton.typeface = fonts["body"]
        }
        // Set all button and title fonts

        val playerTable = findViewById<SortableTableView<PlayerTable>>(R.id.playerTable)
        // Finds the player stats table

        playerTable.columnCount = 4
        // Sets it to 4 columns

        val playerHeader = arrayOf(
            resources.getString(R.string.player_table_header),
            resources.getString(R.string.stats_table_winrate),
            resources.getString(R.string.stats_table_souls),
            resources.getString(R.string.stats_table_adjusted_souls)
        )
        // Sets the table headers for the player table

        val playerHeaderAdapter = PlayerTableHeaderAdapter (this, fonts["title"]!!, *playerHeader)
        // Creates the header adapter
        playerTable.headerAdapter = playerHeaderAdapter
        // Applies the header adapter

        playerTable.setColumnComparator(0, PlayerComparator())
        playerTable.setColumnComparator(1, WinrateComparator())
        playerTable.setColumnComparator(2, SoulsComparator())
        playerTable.setColumnComparator(3, AdjustedSoulsComparator())
        // Allows all the columns to be sorted correctly

        playerTable.setHeaderBackgroundColor(resources.getColor(R.color.darker,theme))
        playerTable.setBackgroundColor(resources.getColor(R.color.lighter,theme))
        // Sets the table to be tints so the background comes through

        val charTable = findViewById<SortableTableView<CharacterTable>>(R.id.characterTable)
        // Finds the character stats table

        charTable.columnCount = 4
        // Sets the number of columns

        val charHeader = arrayOf(
            resources.getString(R.string.character_table_header),
            resources.getString(R.string.stats_table_winrate),
            resources.getString(R.string.stats_table_souls),
            resources.getString(R.string.stats_table_adjusted_souls)
        )
        // Sets all the header strings

        val charHeaderAdapter = CharacterTableHeaderAdapter (this, fonts["title"]!!, *charHeader)
        // Creates the header adapter
        charTable.headerAdapter = charHeaderAdapter

        charTable.setHeaderBackgroundColor(resources.getColor(R.color.darker,theme))
        charTable.setBackgroundColor(resources.getColor(R.color.lighter,theme))
        // Sets the table backgrounds as tints

        charTable.setColumnComparator(0, CharacterComparator())
        charTable.setColumnComparator(1, CharWinrateComparator())
        charTable.setColumnComparator(2, CharSoulsComparator())
        charTable.setColumnComparator(3, CharAdjustedSoulsComparator())
        // Allows all the columns to be sorted

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        var playerData = emptyArray<PlayerTable>()
        // Empty array to store player data

        var characterData = emptyArray<CharacterTable>()
        // Empty array to store chraracter data

        var characters = emptyArray<CharEntity>()
        // Create an empty list of all characters

        val edition = SettingsHandler.getEditions(this)
        // Get the current editions from settings

        lifecycleScope.launch {
        // As a coroutine
            val players = gameDao.getPlayers()
            // Get all players

            edition.forEach { characters += gameDao.getCharacterList(it) }
            // Create a list of all cahracters in the used editions

            val games = gameDao.getGames()
            // Get all games

            players.forEach {
            // For every player
                val newPlayerTable = PlayerTable(it.playerName,0.0,0.0,0,0.0)
                // Create a new row
                val playerInstanceArray = gameDao.getPlayerWithInstance(it.playerName)
                // Get all game instances that player has played
                newPlayerTable.setData(playerInstanceArray,games)
                // Update the data
                playerData += arrayOf(newPlayerTable)
                // Add it to the array
            }

            characters.forEach {
            // For every character
                val newCharTable = CharacterTable(it.charName,0.0,0.0,0,0.0)
                // Create a new row
                val charInstanceArray = gameDao.getCharacterWithInstance(it.charName)
                // Get all instances the character has been in
                newCharTable.setData(charInstanceArray,games)
                // Update the data
                characterData += arrayOf(newCharTable)
                // Add it to the table
            }

            val playerDataAdapter = PlayerTableDataAdapter(this@ViewStatistics, fonts["body"]!!, playerData)
            // Create the player table data adapter
            playerTable.dataAdapter = playerDataAdapter
            // Attach it to the table
            playerTable.sort(PlayerComparator())
            // Set the default sort

            val charDataAdapter = CharacterTableAdapter(this@ViewStatistics, fonts["body"]!!, characterData)
            // Create the character data adapter
            charTable.dataAdapter = charDataAdapter
            // Attach it to the table
            charTable.sort(CharWinrateComparator())
            // Set the default sort
        }

        returnButton.setOnClickListener {
        // When the return button is clicked
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from","statistics")
            startActivity(backToMain)
            // Go back to the main screen
        }
    }

    override fun onBackPressed() {
        val returnButton = findViewById<Button>(R.id.statsBackButton)
        // Get the return button
        returnButton.performClick()
        // Clicks the button
    }
}