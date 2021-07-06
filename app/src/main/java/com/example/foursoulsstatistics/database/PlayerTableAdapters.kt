package com.example.foursoulsstatistics.database

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foursoulsstatistics.R
import com.example.foursoulsstatistics.TextHandler
import de.codecrafters.tableview.TableDataAdapter
import de.codecrafters.tableview.TableHeaderAdapter

class PlayerTable(var playerName: String, var winrate: Double, var soulsAvg: Double, private var playedGames: Int, var adjustedSouls: Double){
    fun setData(data: Array<PlayerWithInstance>, games: Array<Game>){
        playerName = data[0].player.playerName
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
    }

}

class PlayerTableDataAdapter(context: Context, private val tableFont: Typeface, data: Array<PlayerTable>) : TableDataAdapter<PlayerTable>(context, data) {

    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup): View {
        val player = getRowData(rowIndex)
        val renderedView = TextView(context)
        renderedView.gravity = Gravity.CENTER
        renderedView.typeface = tableFont
        when(columnIndex){
            0 -> renderedView.text = TextHandler.capitalise(player.playerName)
            1 -> renderedView.text = context.getString(R.string.stats_table_entry).format(player.winrate)
            2 -> renderedView.text = context.getString(R.string.stats_table_entry).format(player.soulsAvg)
            3 -> renderedView.text = context.getString(R.string.stats_table_entry).format(player.adjustedSouls)
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
    private var textSize = 15
    private var typeface = headerFont
    private var gravity = Gravity.CENTER_HORIZONTAL
    private var color = context.resources.getColor(R.color.darker,context.theme)

    override fun getHeaderView(columnIndex: Int, parentView: ViewGroup): View {
        val textView = TextView(context)
        if (columnIndex < headers.size) {
            textView.text = headers[columnIndex]
            textView.gravity = gravity
        }
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        textView.typeface = typeface
        textView.textSize = textSize.toFloat()
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