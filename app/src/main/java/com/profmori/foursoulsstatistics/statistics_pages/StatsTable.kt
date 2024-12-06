package com.profmori.foursoulsstatistics.statistics_pages

import android.annotation.SuppressLint
import android.content.Context
import com.profmori.foursoulsstatistics.R

class StatsTable(
    var headers: Array<TableHeader>,
    private val allRows: Array<TableRow>,
    private val context: Context
) {
    var rows = emptyArray<TableRow>()
    var head: TableHeaderAdapter? = null
    var body: TableBodyAdaptor? = null
    private var coopBool = false

    init {
        if (headers.isNotEmpty()) {
            coopBool =
                context.resources.getString(R.string.stats_table_turns_remaining) in headers.map { header -> header.headerName }
            getValidRows()
            sortData()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun sortData() {
        val headerMap = mutableMapOf<Int, TableHeader>()
        headers.forEach { header ->
            headerMap.put(header.sortPriority, header)
        }

        arrayOf(4, 3, 2, 1).forEach { headerPriority ->
            rows.sortWith(RowComparator(headerMap[headerPriority]!!, context))
        }
        head?.notifyItemRangeChanged(1,3)
        body?.notifyDataSetChanged()
    }


    fun filterData(playerRange: List<Float>, treasureRange: List<Float>) {
        allRows.forEach { row ->
            row.filterData(playerRange, treasureRange)
        }
        getValidRows()
        sortData()
//        body!!.notifyItemRangeChanged(0,rows.size)
    }

    fun getCellText(row: Int, column: Int): String {
        val rawValue = rows[row].getDataFromHeader(headers[column].headerName, context)
        return if (rawValue is Float) {
            if (rawValue.isNaN()) {
                ""
            } else {
                context.resources.getString(R.string.stats_table_entry).format(rawValue)
            }
        } else {
            rawValue.toString()
        }
    }

    private fun getValidRows() {
        var validRows = emptyArray<TableRow>()
        allRows.forEach { row ->
            var validRow = true
            if (headers[0].headerName != context.resources.getString(R.string.player_table_header)) {
                for (column in 1..3) {
                    val cellVal = row.getDataFromHeader(headers[column].headerName, context)
                    validRow = (cellVal is String) || !(cellVal as Float).isNaN()
                    if (!validRow) {
                        break
                    }
                }
            }
            if (validRow) {
                validRows += row
            }
        }
        rows = validRows
    }

    fun getAllHeaders(): Array<String> {
        var stringArray = arrayOf(
            context.resources.getString(R.string.stats_table_winrate),
            context.resources.getString(R.string.stats_table_soulrate)
        )
        if (coopBool) {
            stringArray += context.resources.getString(R.string.stats_table_turns_remaining)
        } else {
            stringArray += arrayOf(
                context.resources.getString(R.string.stats_table_adjusted_soulrate),
                context.resources.getString(R.string.stats_table_adjusted_souls)
            )
        }
        stringArray += arrayOf(
            context.resources.getString(R.string.stats_table_wins),
            context.resources.getString(R.string.stats_table_souls),
            context.resources.getString(R.string.stats_table_played)
        )
        return stringArray
    }

    fun headerIncluded(headerName: String): Boolean {
        return headerName in headers.map { header -> header.headerName }
    }
}