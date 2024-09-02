package com.profmori.foursoulsstatistics.statistics_pages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import kotlin.math.max
import kotlin.math.min

class TableBodyAdaptor(val statsTable: StatsTable) :
    RecyclerView.Adapter<TableBodyAdaptor.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val context = itemView.context

        val cellList = arrayOf(
            itemView.findViewById<TextView>(R.id.cell1Text),
            itemView.findViewById<TextView>(R.id.cell2Text),
            itemView.findViewById<TextView>(R.id.cell3Text),
            itemView.findViewById<TextView>(R.id.cell4Text)
        )
        // The body of the row
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        statsTable.body = this
        val inflater = LayoutInflater.from(parent.context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.table_row, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, row: Int) {

        val textFont = TextHandler.setFont(viewHolder.context)["body"]
        for (column in arrayOf(0, 1, 2, 3)) {
            val currCell = viewHolder.cellList[column]
            currCell.typeface = textFont
            currCell.text = statsTable.getCellText(row, column)
            currCell.post {
                val maxWordLen = statsTable.getCellText(row, column).split(' ')
                    .map { word -> word.length }.max()
                // Get the length of the longest word in the header
                var headerSize = currCell.width / maxWordLen.toFloat() * 0.6f
                // Set the header size so the longest single word spans the header (roughly)
                if (textFont == ResourcesCompat.getFont(
                        viewHolder.context,
                        R.font.four_souls_body
                    )
                ) {
                    headerSize *= 0.9f
                }
                // Scale slightly smaller if the four souls font is being used
                headerSize = max(headerSize, 9f)
                // Floor the cell font size at 9sp
                headerSize = min(headerSize, 15f)
                // Cap the cell font size at 15sp

                currCell.textSize = headerSize
                // Set the text size
                currCell.typeface = textFont
                // Set the font correctly
            }
        }

        // Set the text to match the current header


    }

    override fun getItemCount(): Int {
        return statsTable.rows.size
    }

}