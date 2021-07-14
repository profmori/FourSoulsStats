package com.example.foursoulsstatistics.database

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foursoulsstatistics.R
import com.example.foursoulsstatistics.TextHandler
import de.codecrafters.tableview.TableDataAdapter
import de.codecrafters.tableview.TableHeaderAdapter

class CharacterTable(var charName: String, var winrate: Double, var soulsAvg: Double, private var playedGames: Int, var adjustedSouls: Double){
    fun setData(data: Array<CharacterWithInstance>, games: Array<Game>){
        charName = data[0].character.charName
        val instances = data[0].gameInstances
        playedGames = instances.size
        var wins = 0.0
        var souls = 0.0
        var adjSouls = 0.0
        instances.forEach {
            if(it.winner){ wins += 1}
            souls += it.souls
            val currGame = games.map{game -> game.gameID}.indexOf(it.gameID)
            val gameSize = games[currGame].playerNo
            adjSouls += it.souls * gameSize
        }
        winrate = wins / playedGames
        soulsAvg = souls / playedGames
        adjustedSouls = adjSouls / playedGames
        if( playedGames == 0){
            winrate = -1.0
            soulsAvg = -1.0
            adjustedSouls = -1.0
        }
    }

}

class CharacterTableAdapter(context: Context, private val tableFont: Typeface, data: Array<CharacterTable>) : TableDataAdapter<CharacterTable>(context, data) {

    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup): View {
        val character = getRowData(rowIndex)
        val renderedView = TextView(context)
        renderedView.gravity = Gravity.CENTER
        renderedView.typeface = tableFont
        if (tableFont == context.resources.getFont(R.font.four_souls_body)) {
            renderedView.textSize = 17f
        }
        else{
            renderedView.textSize = 18f
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
        }
        return renderedView
    }
}

class CharacterTableHeaderAdapter(context: Context, headerFont: Typeface, vararg headers: String) : TableHeaderAdapter(context) {
    private val headers: Array<String?> = headers as Array<String?>
    private var paddingLeft = 20
    private var paddingTop = 30
    private var paddingRight = 20
    private var paddingBottom = 30
    private var textSize = 14
    private var typeface = headerFont
    private var gravity = Gravity.CENTER_HORIZONTAL

    override fun getHeaderView(columnIndex: Int, parentView: ViewGroup): View {
        val textView = TextView(context)
        textSize = if (typeface == context.resources.getFont(R.font.four_souls_title)) {
            10
        } else{ 12 }
        if (columnIndex < headers.size) {
            textView.text = headers[columnIndex]
            textView.gravity = gravity
        }
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        textView.typeface = typeface
        textView.textSize = textSize.toFloat()
        //textView.setTextColor(textColor)
        return textView
    }
}

class CharacterComparator: Comparator<CharacterTable>{
    override fun compare(char1: CharacterTable, char2: CharacterTable): Int {
        val name1 = char1.charName
        val name2 = char2.charName
        return name1.compareTo(name2)
    }
}

class CharWinrateComparator: Comparator<CharacterTable>{
    override fun compare(char1: CharacterTable, char2: CharacterTable): Int {
        val winrate1 = char1.winrate
        val winrate2 = char2.winrate
        return winrate2.compareTo(winrate1)
    }
}

class CharSoulsComparator: Comparator<CharacterTable>{
    override fun compare(char1: CharacterTable, char2: CharacterTable): Int {
        val souls1 = char1.soulsAvg
        val souls2 = char2.soulsAvg
        return souls2.compareTo(souls1)
    }
}

class CharAdjustedSoulsComparator: Comparator<CharacterTable>{
    override fun compare(char1: CharacterTable, char2: CharacterTable): Int {
        val souls1 = char1.adjustedSouls
        val souls2 = char2.adjustedSouls
        return souls2.compareTo(souls1)
    }
}