package com.profmori.foursoulsstatistics.custom_adapters

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharacterWithInstance
import com.profmori.foursoulsstatistics.database.Game
import de.codecrafters.tableview.TableDataAdapter
import de.codecrafters.tableview.TableHeaderAdapter

class CharacterTable(var charName: String, var winrate: Double, var soulsAvg: Double, private var playedGames: Int, var adjustedSouls: Double){
    fun setData(data: Array<CharacterWithInstance>, games: Array<Game>){
    // Function to set the data in the character table
        charName = data[0].character.charName
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
            // If the character won the instance increment their win counter
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
        if( playedGames == 0){
            // If the character is unplayed
            winrate = -1.0
            soulsAvg = -1.0
            adjustedSouls = -1.0
            // Set all values to -1
        }
    }

}

class CharacterTableAdapter(context: Context, private val tableFont: Typeface, data: Array<CharacterTable>) : TableDataAdapter<CharacterTable>(context, data) {

    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup): View {
        val character = getRowData(rowIndex)
        // Gets the current character
        val renderedView = TextView(context)
        // Gets the cell as a text view
        renderedView.gravity = Gravity.CENTER
        // Centres the text
        renderedView.typeface = tableFont
        // Sets the table typeface
        if (tableFont == ResourcesCompat.getFont(context, R.font.four_souls_body)){
        // If it is the four souls font
            renderedView.textSize = 17f
            // Changes the text size
        }
        else{
        // If it is the plain font
            renderedView.textSize = 18f
            // Changes the text size
        }

        when(columnIndex){
            0 -> renderedView.text = TextHandler.capitalise(character.charName)
            1 ->{
                if (character.winrate >= 0){
                    renderedView.text = context.getString(R.string.stats_table_entry).format(character.winrate)
                }
                else{
                    renderedView.text = ""
                }
            }
            2 -> {
                if (character.soulsAvg >= 0){
                    renderedView.text = context.getString(R.string.stats_table_entry).format(character.soulsAvg)
                }
                else{
                    renderedView.text = ""
                }
            }
            3 -> {
                if (character.adjustedSouls >= 0){
                    renderedView.text = context.getString(R.string.stats_table_entry).format(character.adjustedSouls)
                }
                else{
                    renderedView.text = ""
                }
            }
            // Sets the column value based on its column - if the value is -1 blanks the entry
        }
        return renderedView
    }
}

class CharacterTableHeaderAdapter(context: Context, headerFont: Typeface, private val headers: Array<String>) : TableHeaderAdapter(context) {
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
        // Gets the text view of the table cell
        textSize = if (typeface == ResourcesCompat.getFont(context, R.font.four_souls_title)) {
        // If the font is the four souls font
            10
        } else{ 12 }
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

class CharacterComparator: Comparator<CharacterTable>{
    override fun compare(char1: CharacterTable, char2: CharacterTable): Int {
        val name1 = char1.charName
        val name2 = char2.charName
        // Compares characters by name
        return name1.compareTo(name2)
    }
}

class CharWinrateComparator: Comparator<CharacterTable>{
    override fun compare(char1: CharacterTable, char2: CharacterTable): Int {
        val winrate1 = char1.winrate
        val winrate2 = char2.winrate
        // Compare characters by winrate
        return winrate2.compareTo(winrate1)
    }
}

class CharSoulsComparator: Comparator<CharacterTable>{
    override fun compare(char1: CharacterTable, char2: CharacterTable): Int {
        val souls1 = char1.soulsAvg
        val souls2 = char2.soulsAvg
        // Compare characters by souls
        return souls2.compareTo(souls1)
    }
}

class CharAdjustedSoulsComparator: Comparator<CharacterTable>{
    override fun compare(char1: CharacterTable, char2: CharacterTable): Int {
        val souls1 = char1.adjustedSouls
        val souls2 = char2.adjustedSouls
        // Compare characters by average souls
        return souls2.compareTo(souls1)
    }
}