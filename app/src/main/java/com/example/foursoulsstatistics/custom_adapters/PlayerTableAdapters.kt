package com.example.foursoulsstatistics.database

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foursoulsstatistics.R
import com.example.foursoulsstatistics.data_handlers.TextHandler
import de.codecrafters.tableview.TableDataAdapter
import de.codecrafters.tableview.TableHeaderAdapter

class PlayerTable(var playerName: String, var winrate: Double, var soulsAvg: Double, private var playedGames: Int, var adjustedSouls: Double){
    fun setData(data: Array<PlayerWithInstance>, games: Array<Game>){
        playerName = data[0].player.playerName
        // Gets name from the data array
        val instances = data[0].gameInstances
        // Gets valid game instances from the array
        playedGames = instances.size
        // Gets the number of played games from the instances
        var wins = 0.0
        // Initialise the number of wins
        var souls = 0.0
        // Initialise the number of souls
        var adjSouls = 0.0
        // Initialise the adjusted number of souls
        instances.forEach {
            if(it.winner){ wins += 1}
            // If the player won the instance increment their win counter
            souls += it.souls
            // Increment the soul counter
            val currGame = games.map{game -> game.gameID}.indexOf(it.gameID)
            // Gets the current game id
            val gameSize = games[currGame].playerNo
            // Gets the size of the game based on the game ID
            adjSouls += it.souls * gameSize
            // Increments the adjusted number of souls
        }
        winrate = wins / playedGames
        // Calculates the winrate
        soulsAvg = souls / playedGames
        // Calculates the average souls
        adjustedSouls = adjSouls / playedGames
        // Calculates the average adjusted souls
    }

}

class PlayerTableDataAdapter(context: Context, private val tableFont: Typeface, data: Array<PlayerTable>) : TableDataAdapter<PlayerTable>(context, data) {

    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup): View {
        val player = getRowData(rowIndex)
        // Gets the current player
        val renderedView = TextView(context)
        // Gets the cell as a text view
        renderedView.gravity = Gravity.CENTER
        // Centres the text
        renderedView.typeface = tableFont
        // Sets the table typeface
        if (tableFont == context.resources.getFont(R.font.four_souls_body)) {
            // If it is the four souls font
            renderedView.textSize = 18f
            // Changes the text size
        }
        else{
            // If it is the plain font
            renderedView.textSize = 20f
            // Changes the text size
        }
        when(columnIndex){
            0 -> renderedView.text = TextHandler.capitalise(player.playerName)
            1 -> renderedView.text = context.getString(R.string.stats_table_entry).format(player.winrate)
            2 -> renderedView.text = context.getString(R.string.stats_table_entry).format(player.soulsAvg)
            3 -> renderedView.text = context.getString(R.string.stats_table_entry).format(player.adjustedSouls)
            // Sets the column value based on its column
        }
        return renderedView
    }
}

class PlayerTableHeaderAdapter(context: Context, headerFont: Typeface, vararg headers: String) : TableHeaderAdapter(context) {
    private val headers: Array<String?> = headers as Array<String?>
    private var paddingLeft = 20
    private var paddingTop = 30
    private var paddingRight = 20
    private var paddingBottom = 30
    private var textSize = 14
    private var typeface = headerFont
    private var gravity = Gravity.CENTER_HORIZONTAL
    // Sets basic parameters

    override fun getHeaderView(columnIndex: Int, parentView: ViewGroup): View {
        val textView = TextView(context)
        textSize = if (typeface == context.resources.getFont(R.font.four_souls_title)) {
        // If the font is the four souls font
            11
        } else{ 13 }
        // If the font is the readable font

        if (columnIndex < headers.size) {
            textView.text = headers[columnIndex]
            textView.gravity = gravity
            // Sets the text and centres it
        }
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        textView.typeface = typeface
        textView.textSize = textSize.toFloat()
        // Sets the padding, font and text size
        return textView
    }
}

class PlayerComparator: Comparator<PlayerTable>{
    override fun compare(player1: PlayerTable, player2: PlayerTable): Int {
        val name1 = player1.playerName
        val name2 = player2.playerName
        return name1.compareTo(name2)
    }
}

class WinrateComparator: Comparator<PlayerTable>{
    override fun compare(player1: PlayerTable, player2: PlayerTable): Int {
        val winrate1 = player1.winrate
        val winrate2 = player2.winrate
        return winrate2.compareTo(winrate1)
    }
}

class SoulsComparator: Comparator<PlayerTable>{
    override fun compare(player1: PlayerTable, player2: PlayerTable): Int {
        val souls1 = player1.soulsAvg
        val souls2 = player2.soulsAvg
        return souls2.compareTo(souls1)
    }
}

class AdjustedSoulsComparator: Comparator<PlayerTable>{
    override fun compare(player1: PlayerTable, player2: PlayerTable): Int {
        val souls1 = player1.adjustedSouls
        val souls2 = player2.adjustedSouls
        return souls2.compareTo(souls1)
    }
}