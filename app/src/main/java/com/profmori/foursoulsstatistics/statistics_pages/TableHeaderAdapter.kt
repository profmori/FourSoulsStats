package com.profmori.foursoulsstatistics.statistics_pages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import kotlin.math.max
import kotlin.math.min

class TableHeaderAdapter(val statsTable: StatsTable) :
    RecyclerView.Adapter<TableHeaderAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val context = itemView.context
        val sortButton = itemView.findViewById<AppCompatButton>(R.id.tableSortButton)

        // The button to sort the table column
        val headerText = itemView.findViewById<TextView>(R.id.tableHeaderText)
        // The title of the column
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.table_header, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, column: Int) {
        val textFont = TextHandler.setFont(viewHolder.context)["body"]
        val currentColumn = statsTable.headers[column]
        // Get the current header text
        viewHolder.headerText.setText(currentColumn.headerName)
        // Set the text to match the current header
        viewHolder.headerText.post {
            val maxWordLen = currentColumn.headerName.split(' ').map { word -> word.length }.max()
            // Get the length of the longest word in the header
            var headerSize = viewHolder.headerText.width / maxWordLen.toFloat() * 0.6f
            // Set the header size so the longest single word spans the header (roughly)
            if (textFont == ResourcesCompat.getFont(viewHolder.context, R.font.four_souls_body)) {
                headerSize *= 0.9f
            }
            // Scale slightly smaller if the four souls font is being used
            headerSize = max(headerSize, 11f)
            // Floor the header size at 11sp
            headerSize = min(headerSize, 18f)
            // Cap the header size at 18sp
            viewHolder.headerText.textSize = headerSize
            // Set the text size
            viewHolder.headerText.typeface = textFont
            // Set the font correctly
        }
        // Once the header row has been sized, resize the text to fit the width

        viewHolder.sortButton.setText(currentColumn.sortPriority.toString())
        // Set the string to the current priority
        viewHolder.sortButton.typeface = textFont
        // Set the font correctly
        viewHolder.sortButton.setBackgroundResource(currentColumn.getSortButton())
        // Set the background to the inverted cross (descending order)

        viewHolder.sortButton.setOnClickListener {
            if (currentColumn.sortPriority == 1) {
                currentColumn.sortDescending = !currentColumn.sortDescending
                // Invert the sorting order
            }else {
                for (column in statsTable.headers) {
                    if (column.sortPriority < currentColumn.sortPriority) {
                        column.sortPriority += 1
                    }
                }
                currentColumn.sortPriority = 1
            }
            notifyItemRangeChanged(0,4)
            statsTable.sortData()
        }
    }

    override fun getItemCount(): Int {
        return statsTable.headers.size
    }
}