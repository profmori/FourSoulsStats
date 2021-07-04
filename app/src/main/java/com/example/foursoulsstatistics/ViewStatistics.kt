package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        val playerTable = findViewById<SortableTableView<PlayerTable>>(R.id.playerTable)
        // Finds the player stats table
        playerTable.columnCount = 4
        // Sets it to 4 columns
        val playerHeader = arrayOf("Player", "Win Rate", "Average Souls", "Adjusted Souls")
        val playerHeaderAdapter = PlayerTableHeaderAdapter (this,*playerHeader)
        playerTable.headerAdapter = playerHeaderAdapter

        playerTable.setColumnComparator(0, PlayerComparator())

        playerTable.setColumnComparator(1, WinrateComparator())

        playerTable.setColumnComparator(2, SoulsComparator())

        playerTable.setColumnComparator(3, AdjustedSoulsComparator())

        val charTable = findViewById<SortableTableView<CharacterTable>>(R.id.characterTable)
        charTable.columnCount = 4
        val charHeader = arrayOf("Character", "Win Rate", "Average Souls", "Adjusted Souls")
        val charHeaderAdapter = CharacterTableHeaderAdapter (this,*charHeader)
        charTable.headerAdapter = charHeaderAdapter

        charTable.setColumnComparator(0, CharacterComparator())
        charTable.setColumnComparator(1, CharWinrateComparator())
        charTable.setColumnComparator(2, CharSoulsComparator())
        charTable.setColumnComparator(3, CharAdjustedSoulsComparator())

        val columnModel = TableColumnWeightModel(4)
        columnModel.setColumnWeight(0, 11)
        columnModel.setColumnWeight(1,10)
        columnModel.setColumnWeight(2,10)
        columnModel.setColumnWeight(3,10)

        playerTable.columnModel = columnModel
        charTable.columnModel = columnModel

        val returnButton = findViewById<Button>(R.id.statsBackButton)

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        var playerData = emptyArray<PlayerTable>()

        var characterData = emptyArray<CharacterTable>()

        lifecycleScope.launch {
            val players = gameDao.getPlayers()
            val characters = gameDao.getFullCharacterList()
            val games = gameDao.getGames()
            players.forEach {
                val newPlayerTable = PlayerTable(it.playerName,0.0,0.0,0,0.0)
                val playerInstanceArray = gameDao.getPlayerWithInstance(it.playerName)
                newPlayerTable.setData(playerInstanceArray,games)
                playerData += arrayOf(newPlayerTable)
            }

            characters.forEach {
                val newCharTable = CharacterTable(it.charName,0.0,0.0,0,0.0)
                val charInstanceArray = gameDao.getCharacterWithInstance(it.charName)
                newCharTable.setData(charInstanceArray,games)
                characterData += arrayOf(newCharTable)
            }
            val charDataAdapter = CharacterTableAdapter(this@ViewStatistics, characterData)
            println(charDataAdapter)
            charTable.dataAdapter = charDataAdapter
            charTable.sort(CharWinrateComparator())

            val playerDataAdapter = PlayerTableDataAdapter(this@ViewStatistics,playerData)
            playerTable.dataAdapter = playerDataAdapter
            playerTable.sort(PlayerComparator())
        }

        returnButton.setOnClickListener {
            val goToMain = Intent(this, MainActivity::class.java)
            startActivity(goToMain)
        }

    }
}