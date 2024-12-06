package com.profmori.foursoulsstatistics.statistics_pages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.TextHandler

class HeaderChecklistAdaptor(private val statsTable: StatsTable) :
    RecyclerView.Adapter<HeaderChecklistAdaptor.ViewHolder>() {

    var checkedArray = statsTable.headers.map { header -> header.headerName }.toTypedArray()
    init{ checkedArray = checkedArray.sliceArray(1..3) }

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val context = itemView.context

        val checkBox = itemView.findViewById<CheckBox>(R.id.headerSelectCheckbox)

        // The checkbox of the entry
        val headerName = itemView.findViewById<TextView>(R.id.headerSelectName)
        // The header to be selected
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.choose_header_line, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, row: Int) {

        val textFont = TextHandler.setFont(viewHolder.context)["body"]
        val currCheckbox = viewHolder.checkBox
        val currHeader = viewHolder.headerName
        currHeader.typeface = textFont
        currHeader.text = statsTable.getAllHeaders()[row]
        // Set the text to match the current header name
        currCheckbox.isChecked = statsTable.headerIncluded(statsTable.getAllHeaders()[row])
        // Check the headers that are currently included

        currCheckbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, checked ->
            if (checked) {
                checkedArray = checkedArray.plus(statsTable.getAllHeaders()[row])
            } else {
                checkedArray = checkedArray.filter { element ->
                    element != statsTable.getAllHeaders()[row]
                }.toTypedArray()
            }
        } )
    }

    override fun getItemCount(): Int {
        return statsTable.getAllHeaders().size
    }

}