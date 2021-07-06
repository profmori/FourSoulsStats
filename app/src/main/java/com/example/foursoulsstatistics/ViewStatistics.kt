package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foursoulsstatistics.database.*
import de.codecrafters.tableview.SortableTableView
import de.codecrafters.tableview.model.TableColumnWeightModel
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

        val playerColumnModel = TableColumnWeightModel(4)
        playerColumnModel.setColumnWeight(0, 5)
        playerColumnModel.setColumnWeight(1,4)
        playerColumnModel.setColumnWeight(2,6)
        playerColumnModel.setColumnWeight(3,6)
        // Sets the column width weightings

        playerTable.columnModel = playerColumnModel
        // Adds the weightings to the table

        val charTable = findViewById<SortableTableView<CharacterTable>>(R.id.characterTable)
        // Finds the character stats table

        charTable.columnCount = 4
        // Sets the number of columns

        val charHeader = arrayOf(resources.getString(R.string.character_table_header),
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

        val charColumnModel = TableColumnWeightModel(4)
        charColumnModel.setColumnWeight(0, 7)
        charColumnModel.setColumnWeight(1,4)
        charColumnModel.setColumnWeight(2,6)
        charColumnModel.setColumnWeight(3,6)
        // Sets the column width weightings

        charTable.columnModel = charColumnModel
        // Applies the weightings

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        var playerData = emptyArray<PlayerTable>()
        // Empty array to store player data

        var characterData = emptyArray<CharacterTable>()
        // Empty array to store chraracter data

        lifecycleScope.launch {
        // As a coroutine
            val players = gameDao.getPlayers()
            // Get all players
            val characters = gameDao.getFullCharacterList()
            // Get all characters
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
            finish()
            // Go back to the last page
        }

    }
}