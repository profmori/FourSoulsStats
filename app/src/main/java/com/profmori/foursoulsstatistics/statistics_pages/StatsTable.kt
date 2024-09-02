package com.profmori.foursoulsstatistics.statistics_pages

import android.annotation.SuppressLint
import android.content.Context
import com.profmori.foursoulsstatistics.R

class StatsTable(
    val headers: Array<TableHeader>,
    private val allRows: Array<TableRow>,
    private val context: Context
) {
    var rows = emptyArray<TableRow>()
    var body: TableBodyAdaptor? = null

    init {
        getValidRows()
        sortData()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun sortData() {
        val headerMap = mutableMapOf<Int, TableHeader>()
        headers.forEach { header ->
            headerMap.put(header.sortPriority, header)
        }

        arrayOf(4, 3, 2, 1).forEach { headerPriority ->
            println(headerPriority)
            rows.sortWith(RowComparator(headerMap[headerPriority]!!, context))
        }

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
            context.resources.getString(R.string.stats_table_entry).format(rawValue)
        } else {
            rawValue.toString()
        }
    }

    private fun getValidRows() {
        var validRows = emptyArray<TableRow>()
        allRows.forEach { row ->
            var validRow = true
            for (column in 1..3) {
                val cellVal = row.getDataFromHeader(headers[column].headerName, context)
                validRow = (cellVal is String) || !(cellVal as Float).isNaN()
                if (!validRow) {
                    break
                }
            }
            if (validRow) {
                validRows += row
            }
        }
        rows = validRows
    }
}