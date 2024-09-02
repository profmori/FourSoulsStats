package com.profmori.foursoulsstatistics.statistics_pages

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.TableHandler
import com.profmori.foursoulsstatistics.data_handlers.TableHandler.Companion.gatherData
import kotlinx.coroutines.launch

class ViewStatisticsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stats)

        val characterTitle = findViewById<TextView>(R.id.viewStatsTitle)
        // Gets the title

        val filterText = findViewById<TextView>(R.id.filtersHeader)
        // Get the filter text

        val treasureText = findViewById<TextView>(R.id.treasureTitle)
        val playerText = findViewById<TextView>(R.id.playerTitle)
        // Get the slider titles

        val playerSlider = findViewById<RangeSlider>(R.id.playerSlider)
        val treasureSlider = findViewById<RangeSlider>(R.id.treasureSlider)
        // Get the sliders

        val backButton = findViewById<Button>(R.id.backToStats)
        // Gets the button to go back to the statistics menu

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image

        val tableType = intent.getStringExtra("firstColumn")
        val coopPage = intent.getBooleanExtra("coop", false)
        val onlineData = intent.getBooleanExtra("online", false)
        // Get the details of the required table

        characterTitle.setText(
            when (tableType) {
                "Player" -> R.string.player_stats_title
                "Character" -> R.string.character_stats_title
                "Eternal" -> R.string.eternal_stats_title
                else -> R.string.error_placeholder
            }
        )
        var tableRows = emptyArray<TableRow>()
        lifecycleScope.launch {
            tableRows =
                gatherData(tableType.toString(), coopPage, onlineData, this@ViewStatisticsPage)

            val tableData = makeTable(
                tableType,
                coopPage,
                tableRows
            )
            TableHandler.pageSetup(
                tableData, filterText, playerText, playerSlider, treasureText, treasureSlider
            )
            // Set up the table page with sliders
            playerSlider.addOnChangeListener { _, _, _ ->
                tableData.filterData(playerSlider.values, treasureSlider.values)
            }
            treasureSlider.addOnChangeListener { _, _, _ ->
                tableData.filterData(playerSlider.values, treasureSlider.values)
            }
        }

        TableHandler.pageSetup(
            this, backButton, background, characterTitle, filterText, playerText, treasureText
        )
        // Setup the page correctly
    }

    private fun makeTable(
        tableType: String?,
        coopPage: Boolean,
        tableRows: Array<TableRow>
    ): StatsTable {
        val tableHeader = when (tableType) {
            "Player" -> R.string.player_table_header
            "Character" -> R.string.character_table_header
            "Eternal" -> R.string.eternal_table_header
            else -> R.string.error_placeholder
        }

        val tableHeaders = if (coopPage) {
            arrayOf(
                TableHeader(resources.getString(tableHeader), 4),
                TableHeader(resources.getString(R.string.stats_table_winrate), 1),
                TableHeader(resources.getString(R.string.stats_table_souls), 2),
                TableHeader(resources.getString(R.string.stats_table_turns_remaining), 3)
            )
        } else {
            arrayOf(
                TableHeader(resources.getString(tableHeader), 4),
                TableHeader(resources.getString(R.string.stats_table_winrate), 1),
                TableHeader(resources.getString(R.string.stats_table_souls), 2),
                TableHeader(resources.getString(R.string.stats_table_adjusted_souls), 3)
            )
        }
        // Sets all the table columns

        val tableData = StatsTable(tableHeaders, tableRows, this)
        // Empty array to store player data

        val tableHeaderRow = findViewById<RecyclerView>(R.id.statsTableHeader)
        // Finds the stats table
        val headerAdapter = TableHeaderAdapter(tableData)
        tableHeaderRow.adapter = headerAdapter
        // Sets the adaptor for the header row
        tableHeaderRow.layoutManager = GridLayoutManager(this, 4)
        // Create the header row as a 4x1 grid

        val tableBody = findViewById<RecyclerView>(R.id.statsTableBody)
        val bodyAdapter = TableBodyAdaptor(tableData)
        tableBody.adapter = bodyAdapter
        // Sets the adaptor for the rest of the table
        tableBody.layoutManager = LinearLayoutManager(this)
        // Create the table as a list of rows

        return tableData
    }
}

