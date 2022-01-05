package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.slider.RangeSlider
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.custom_adapters.*
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.online_database.OnlineGameInstance
import com.profmori.foursoulsstatistics.statistics_pages.StatisticsMenu
import de.codecrafters.tableview.SortableTableView

class TableHandler {
    companion object {
        fun pageSetup(
            context: Context, backButton: Button, background: ImageView, characterTitle: TextView,
            filterText: TextView, playerText: TextView, treasureText: TextView
        ) {
            val buttonBG = ImageHandler.setButtonImage()
            backButton.setBackgroundResource(buttonBG)
            // Get the button and set the image

            SettingsHandler.updateBackground(context, background)
            // Set the background image

            val fonts = TextHandler.setFont(context)
            // Get the fonts from the text handler

            characterTitle.typeface = fonts["title"]
            filterText.typeface = fonts["body"]
            playerText.typeface = fonts["body"]
            treasureText.typeface = fonts["body"]
            backButton.typeface = fonts["body"]
            // Set all button and title fonts

            backButton.setOnClickListener {
                // When the back button is pressed
                val backToStats = Intent(context, StatisticsMenu::class.java)
                startActivity(context, backToStats, null)
                // Go back to the statistics page
            }
        }

        fun onlineDataSetup(
            gamesList: Array<OnlineGameInstance>,
            filterText: TextView,
            playerText: TextView,
            playerSlider: RangeSlider,
            treasureText: TextView,
            treasureSlider: RangeSlider
        ) {
            val treasures = gamesList.map { game -> game.treasureNum }
            // Get the list of treasure numbers
            val players = gamesList.map { game -> game.gameSize }
            // Get the list of player numbers
            dataSetup(
                treasures,
                players,
                filterText,
                playerText,
                playerSlider,
                treasureText,
                treasureSlider
            )
            // Set up the data properly
        }

        fun localDataSetup(
            gamesList: Array<Game>,
            filterText: TextView,
            playerText: TextView,
            playerSlider: RangeSlider,
            treasureText: TextView,
            treasureSlider: RangeSlider
        ) {
            val treasures = gamesList.map { game -> game.treasureNo }
            // Get the list of treasure numbers
            val players = gamesList.map { game -> game.playerNo }
            // Get the list of player numbers
            dataSetup(
                treasures,
                players,
                filterText,
                playerText,
                playerSlider,
                treasureText,
                treasureSlider
            )
            // Set up the data properly
        }

        private fun dataSetup(
            treasures: List<Int>,
            players: List<Int>,
            filterText: TextView,
            playerText: TextView,
            playerSlider: RangeSlider,
            treasureText: TextView,
            treasureSlider: RangeSlider
        ) {
            val minTreasure = treasures.minOrNull()!!.toFloat()
            val maxTreasure = treasures.maxOrNull()!!.toFloat()
            // Get the range of treasure values
            treasureSlider.valueFrom = minTreasure
            treasureSlider.valueTo = maxTreasure
            // Set the slider limits
            treasureSlider.values = listOf(minTreasure, maxTreasure)
            // Set the current settings to the limits
            if (minTreasure == maxTreasure) {
                treasureText.visibility = View.GONE
                treasureSlider.visibility = View.GONE
            }
            // If there is no range, don't allow this to be modified

            val minPlayer = players.minOrNull()!!.toFloat()
            val maxPlayer = players.maxOrNull()!!.toFloat()
            // Get the minimum and maximum number of players
            playerSlider.valueFrom = minPlayer
            playerSlider.valueTo = maxPlayer
            // Set the limits of the slider
            playerSlider.values = listOf(minPlayer, maxPlayer)
            // Set the current slider values to the limit
            if (minPlayer == maxPlayer) {
                playerText.visibility = View.GONE
                playerSlider.visibility = View.GONE
            }
            // If there is no range, don't allow this to be modified

            if ((minPlayer == maxPlayer) and (minTreasure == maxTreasure)) {
                filterText.visibility = View.GONE
            }
            // If there is nothing to filter, don't show filter text
        }

        fun createTable(
            context: Context,
            table: SortableTableView<StatsTable>,
            headerList: Array<String>,
            tableData: Array<StatsTable>
        ) {
            val fonts = TextHandler.setFont(context)
            // Get the fonts from the text handler

            table.columnCount = 4
            // Sets the number of columns

            val headerAdapter = StatsTableHeaderAdapter(context, fonts["title"]!!, headerList)
            // Creates the header adapter
            table.headerAdapter = headerAdapter

            table.setHeaderBackgroundColor(ContextCompat.getColor(context, R.color.darker))
            table.setBackgroundColor(ContextCompat.getColor(context, R.color.lighter))
            // Sets the table backgrounds as tints

            table.setColumnComparator(
                0,
                NameComparator()
            )
            table.setColumnComparator(
                1,
                WinrateComparator()
            )
            table.setColumnComparator(
                2,
                SoulsComparator()
            )
            table.setColumnComparator(
                3,
                AdjustedSoulsComparator()
            )
            // Allows all the columns to be sorted

            val charDataAdapter = StatsTableDataAdapter(context, fonts["body"]!!, tableData)
            // Create the character data adapter
            table.dataAdapter = charDataAdapter
            // Attach it to the table
            table.sort(NameComparator())
            table.sort(GamesPlayedComparator())
            table.sort(AdjustedSoulsComparator())
            table.sort(SoulsComparator())
            table.sort(WinrateComparator())
            // Set the default sort: Winrate -> Avg Souls -> Adjusted Souls
            // -> Games Played -> Alphabetical
        }
    }
}