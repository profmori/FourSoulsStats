package com.example.foursoulsstatistics.database

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foursoulsstatistics.R
import de.codecrafters.tableview.TableDataAdapter
import de.codecrafters.tableview.TableHeaderAdapter

class CharacterTable(var charName: String, var winrate: Double, var soulsAvg: Double, var playedGames: Int, var adjustedSouls: Double){
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

class CharacterTableAdapter(context: Context, data: Array<CharacterTable>) : TableDataAdapter<CharacterTable>(context, data) {

    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup): View {
        val character = getRowData(rowIndex)
        val renderedView = TextView(context)
        renderedView.gravity = Gravity.CENTER

        when(columnIndex){
            0 -> renderedView.text = character.charName
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

class CharacterTableHeaderAdapter
/**
 * Creates a new SimpleTableHeaderAdapter.
 *
 * @param context The context to use inside this [TableHeaderAdapter].
 * @param headers The header labels that shall be rendered.
 */(context: Context, vararg headers: String) : TableHeaderAdapter(context) {
    private val headers: Array<String?> = headers as Array<String?>
    private var paddingLeft = 20
    private var paddingTop = 30
    private var paddingRight = 20
    private var paddingBottom = 30
    private var textSize = 14
    private var typeface = Typeface.BOLD
    private var textColor = R.color.black
    private var gravity = Gravity.CENTER_HORIZONTAL

    /**
     * Sets the padding that will be used for all table headers.
     *
     * @param left   The padding on the left side.
     * @param top    The padding on the top side.
     * @param right  The padding on the right side.
     * @param bottom The padding on the bottom side.
     */
    fun setPaddings(left: Int, top: Int, right: Int, bottom: Int) {
        paddingLeft = left
        paddingTop = top
        paddingRight = right
        paddingBottom = bottom
    }

    /**
     * Sets the gravity of the text inside the header cell.
     * @param gravity The gravity of the text inside the header cell.
     */
    fun setGravity(gravity: Int) {
        this.gravity = gravity
    }

    /**
     * Sets the padding that will be used on the left side for all table headers.
     *
     * @param paddingLeft The padding on the left side.
     */
    fun setPaddingLeft(paddingLeft: Int) {
        this.paddingLeft = paddingLeft
    }

    /**
     * Sets the padding that will be used on the top side for all table headers.
     *
     * @param paddingTop The padding on the top side.
     */
    fun setPaddingTop(paddingTop: Int) {
        this.paddingTop = paddingTop
    }

    /**
     * Sets the padding that will be used on the right side for all table headers.
     *
     * @param paddingRight The padding on the right side.
     */
    fun setPaddingRight(paddingRight: Int) {
        this.paddingRight = paddingRight
    }

    /**
     * Sets the padding that will be used on the bottom side for all table headers.
     *
     * @param paddingBottom The padding on the bottom side.
     */
    fun setPaddingBottom(paddingBottom: Int) {
        this.paddingBottom = paddingBottom
    }

    /**
     * Sets the text size that will be used for all table headers.
     *
     * @param textSize The text size that shall be used.
     */
    fun setTextSize(textSize: Int) {
        this.textSize = textSize
    }

    /**
     * Sets the typeface that will be used for all table headers.
     *
     * @param typeface The type face that shall be used.
     */
    fun setTypeface(typeface: Int) {
        this.typeface = typeface
    }

    /**
     * Sets the text color that will be used for all table headers.
     *
     * @param textColor The text color that shall be used.
     */
    fun setTextColor(textColor: Int) {
        this.textColor = textColor
    }

    override fun getHeaderView(columnIndex: Int, parentView: ViewGroup): View {
        val textView = TextView(context)
        if (columnIndex < headers.size) {
            textView.text = headers[columnIndex]
            textView.gravity = gravity
        }
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        textView.setTypeface(textView.typeface, typeface)
        textView.textSize = textSize.toFloat()
        //textView.setTextColor(textColor)
        return textView
    }
}