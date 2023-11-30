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
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.database.GameInstance
import com.profmori.foursoulsstatistics.online_database.OnlineGameInstance
import de.codecrafters.tableview.TableDataAdapter
import de.codecrafters.tableview.TableHeaderAdapter

class StatsTable(
    var name: String,
    var winrate: Double,
    var soulsAvg: Double,
    var playedGames: Int,
    var adjustedSouls: Double,
    var turnsAvg: Double
) {
    private fun generateMetrics(wins: Double, souls: Double, adjSouls: Double, turns: Double) {
        winrate = wins / playedGames
        // Calculates the winrate
        soulsAvg = souls / playedGames
        // Calculates the average souls
        adjustedSouls = adjSouls / playedGames
        // Calculates the average adjusted souls
        turnsAvg = turns / playedGames
        // Calculates the average number of turns taken
        if (playedGames == 0) {
            // If the item is unplayed
            winrate = -1.0
            soulsAvg = -1.0
            adjustedSouls = -1.0
            turnsAvg = -1.0
            // Set all values to -1
        }
    }

    fun setData(rowName: String, instances: List<GameInstance>, games: Array<Game>) {
        // Function to set the data in the character table
        name = rowName
        // Gets name from the input
        val gamesList = games.map { game -> game.gameID }
        // Get the list of selected games


        playedGames = 0
        // Initialises played games counter
        var wins = 0.0
        // Initialise the number of wins
        var souls = 0.0
        // Initialise the number of souls
        var adjSouls = 0.0
        // Initialise the adjusted number of souls
        var turnsPlayed = 0.0
        // Initialise the total number of turns played

        instances.forEach {
            if (gamesList.contains(it.gameID)) {
                playedGames += 1
                if (it.winner) {
                    wins += 1
                }
                // If the character won the instance increment their win counter
                souls += it.souls
                // Increment the soul counter
                val currGame = games.map { game -> game.gameID }.indexOf(it.gameID)
                // Gets the current game id
                val gameSize = games[currGame].playerNo
                // Gets the size of the game based on the game ID
                adjSouls += it.souls * gameSize
                // Increments the adjusted number of souls
                turnsPlayed += games[currGame].turnsLeft
                // Initialise the total number of turns played
            }
        }
        generateMetrics(wins, souls, adjSouls, turnsPlayed)
        // Generates all the winrates and averages from the data
    }

    fun setData(rowName: String, tableType: String, instances: Array<OnlineGameInstance>) {
        name = rowName
        // Get the name from the input
        val gamesList = when (tableType) {
            "Character" -> instances.filter { instance -> instance.charName == name }
            "Eternal" -> instances.filter { instance -> instance.eternal == name }
            else -> instances.filter { false }
        }
        // Filter all the games to only include those with the correct item
        playedGames = 0
        // Initialises played games counter
        var wins = 0.0
        // Initialise the number of wins
        var souls = 0.0
        // Initialise the number of souls
        var adjSouls = 0.0
        // Initialise the adjusted number of souls
        var turnsPlayed = 0.0
        // Initialise the total number of turns played
        gamesList.forEach {
            playedGames += 1
            if (it.winner) {
                wins += 1
            }
            // If the character won the instance increment their win counter
            souls += it.souls
            // Increment the soul counter
            adjSouls += it.souls * it.gameSize
            // Increments the adjusted number of souls
            turnsPlayed += it.turnsLeft
            // Initialise the total number of turns played
        }
        generateMetrics(wins, souls, adjSouls, turnsPlayed)
        // Generates all the winrates and averages from the data
    }
}

class StatsTableDataAdapter(
    context: Context,
    private val tableFont: Typeface,
    data: Array<StatsTable>,
    private val coopBoolean: Boolean
) : TableDataAdapter<StatsTable>(context, data) {

    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup): View {
        val rowData = getRowData(rowIndex)
        // Gets the current character
        val renderedView = TextView(context)
        // Gets the cell as a text view
        renderedView.gravity = Gravity.CENTER
        // Centres the text
        renderedView.typeface = tableFont
        // Sets the table typeface
        if (tableFont == ResourcesCompat.getFont(context, R.font.four_souls_body)) {
            // If it is the four souls font
            renderedView.textSize = 17f
            // Changes the text size
        } else {
            // If it is the plain font
            renderedView.textSize = 17f
            // Changes the text size
        }

        val row3 = if (coopBoolean) {
            rowData.turnsAvg
        } else {
            rowData.adjustedSouls
        }

        when (columnIndex) {
            0 -> {
                if (rowData.name.contains("I Can't Believe", true)) {
                    // If the entry is "I Can't Believe It's Not Butter Bean"
                    renderedView.textSize = 10f
                    // Changes the text size
                    renderedView.text =
                        TextHandler.capitalise("\"I Can't Believe It's Not Butter Bean\"")
                    // Sets the text since the overall string is too long
                } else {
                    renderedView.textSize =
                        19f - (TextHandler.wordLength(rowData.name)) * 0.5f
                    renderedView.text = TextHandler.capitalise(rowData.name)
                    // Set the row title to the capitalised name
                }

            }

            1 -> {
                if (rowData.winrate >= 0) {
                    // If the row data is valid
                    renderedView.text =
                        context.getString(R.string.stats_table_entry).format(rowData.winrate)
                    // Print the winrate
                } else {
                    // If data is invalid / -1
                    renderedView.text = ""
                    // print a blank box
                }
            }

            2 -> {
                if (rowData.soulsAvg >= 0) {
                    // If the row data is valid
                    renderedView.text =
                        context.getString(R.string.stats_table_entry).format(rowData.soulsAvg)
                    // Print the average souls
                } else {
                    // If data is invalid / -1
                    renderedView.text = ""
                    // print a blank box
                }
            }

            3 -> {
                if (row3 >= 0) {
                    // If the row data is valid
                    renderedView.text =
                        context.getString(R.string.stats_table_entry).format(row3)
                    // Print the average adjusted souls / turns taken
                } else {
                    // If data is invalid / -1
                    renderedView.text = ""
                    // print a blank box
                }
            }
            // Sets the column value based on its column - if the value is -1 blanks the entry
        }
        return renderedView
    }
}

class StatsTableHeaderAdapter(
    context: Context,
    headerFont: Typeface,
    private val headers: Array<String>
) : TableHeaderAdapter(context) {
    private var paddingLeft = 20
    private var paddingTop = 30
    private var paddingRight = 20
    private var paddingBottom = 30

    // Set outside cell padding
    private var textSize = 14f

    // Initialise text size
    private var typeface = headerFont

    // Set the font for the headers
    private var gravity = Gravity.CENTER_VERTICAL
    // Align the headers vertically centred

    override fun getHeaderView(columnIndex: Int, parentView: ViewGroup): View {
        val textView = TextView(context)
        // Gets the text view of the table cell
        textSize = if (typeface == ResourcesCompat.getFont(context, R.font.four_souls_title)) {
            // If the font is the four souls font
            13f
        } else {
            13f
        }
        textSize -= (TextHandler.wordLength(headers[columnIndex])) * 0.4f
        // If the font is the readable font

        textView.text = headers[columnIndex]
        textView.gravity = gravity
        // Sets the text and centres it

        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        textView.typeface = typeface
        textView.textSize = textSize
        // Sets the padding, font and text size
        return textView
    }
}

class NameComparator : Comparator<StatsTable> {
    override fun compare(row1: StatsTable, row2: StatsTable): Int {
        val name1 = row1.name
        val name2 = row2.name
        // Compares characters by name
        return name1.compareTo(name2)
    }
}

class WinrateComparator : Comparator<StatsTable> {
    override fun compare(row1: StatsTable, row2: StatsTable): Int {
        val winrate1 = row1.winrate
        val winrate2 = row2.winrate
        // Compare characters by winrate
        return winrate2.compareTo(winrate1)
    }
}

class SoulsComparator : Comparator<StatsTable> {
    override fun compare(row1: StatsTable, row2: StatsTable): Int {
        val souls1 = row1.soulsAvg
        val souls2 = row2.soulsAvg
        // Compare characters by souls
        return souls2.compareTo(souls1)
    }
}

class AdjustedSoulsComparator : Comparator<StatsTable> {
    override fun compare(row1: StatsTable, row2: StatsTable): Int {
        val souls1 = row1.adjustedSouls
        val souls2 = row2.adjustedSouls
        // Compare characters by average souls
        return souls2.compareTo(souls1)
    }
}

class TurnsComparator : Comparator<StatsTable> {
    override fun compare(row1: StatsTable, row2: StatsTable): Int {
        val souls1 = row1.turnsAvg
        val souls2 = row2.turnsAvg
        // Compare characters by average souls
        return souls2.compareTo(souls1)
    }
}

class GamesPlayedComparator : Comparator<StatsTable> {
    override fun compare(row1: StatsTable, row2: StatsTable): Int {
        val played1 = row1.playedGames
        val played2 = row2.playedGames
        // Compare characters by number of games played
        return played1.compareTo(played2)
    }
}