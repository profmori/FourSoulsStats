package com.profmori.foursoulsstatistics.custom_adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.TextHandler


class CustomItemListAdapter(private var cardList: Array<String>, private val font: Typeface) : RecyclerView.Adapter<CustomItemListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val textItem: TextView = itemView.findViewById(R.id.customListText)
        // Allows the text to be set in code
        val closeButton: Button = itemView.findViewById(R.id.deleteCross)
        // Allows the button to be accessed in code
        val line: ConstraintLayout = itemView.findViewById(R.id.customListLine)
        // Allows the line to be accessed in code
        val dark = listItemView.resources.getDrawable(R.color.darker)
        val light = listItemView.resources.getDrawable(R.color.lighter)
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.custom_item_entry, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position

        val textItem = viewHolder.textItem
        // Get the text entry item

        val closeButton = viewHolder.closeButton
        // Get the close button

        val line = viewHolder.line
        // Get the line of the view

        when (position % 2){
            0 -> line.background = viewHolder.dark
            1 -> line.background = viewHolder.light
        }

        val entry = TextHandler.capitalise(cardList[position])

        textItem.text = entry

        textItem.typeface = font
        closeButton.typeface = font

        closeButton.setOnClickListener {
            val newList = cardList.toMutableList()
            newList.removeAt(position)
            cardList = newList.toTypedArray()
            notifyItemRemoved(position)

        }

    }

    fun getItems(): Array<String>{
        return cardList
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return cardList.size
        // Returns the player list size element
    }
}