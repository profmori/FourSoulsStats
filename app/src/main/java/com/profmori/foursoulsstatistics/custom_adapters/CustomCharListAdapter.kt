package com.profmori.foursoulsstatistics.custom_adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R

import com.profmori.foursoulsstatistics.CustomCardEntry
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharEntity


class CustomCharListAdapter(private var cardList: Array<CharEntity>, private val font: Typeface, private val activity: CustomCardEntry) : RecyclerView.Adapter<CustomCharListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val textItem: TextView = itemView.findViewById(R.id.customListText)
        // Allows the text to be set in code
        val imageButton: Button = itemView.findViewById(R.id.changeImageButton)
        // Allows the button to be shown in code
        val closeButton: Button = itemView.findViewById(R.id.deleteCross)
        // Allows the button to be accessed in code
        val line: ConstraintLayout = itemView.findViewById(R.id.customListLine)
        // Allows the line to be accessed in code
        val dark = ContextCompat.getDrawable(listItemView.context, R.color.dark)
        val light = ContextCompat.getDrawable(listItemView.context, R.color.lighter)
        // Get the dark and light colours for rows
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.custom_char_entry, parent, false)
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

        closeButton.typeface = ResourcesCompat.getFont(textItem.context, R.font.four_souls_title)!!

        val imageButton = viewHolder.imageButton
        // Get the image selection button

        val scale = 0.4F
        imageButton.scaleX = scale
        imageButton.scaleY = scale
        // Scale the image so it's a reasonable size

        val line = viewHolder.line
        // Get the line of the view

        when (position % 2){
            0 -> line.background = viewHolder.dark
            1 -> line.background = viewHolder.light
        }
        // Set lines to alternate colours

        val entry = TextHandler.capitalise(cardList[position].charName)
        // Get the text entry

        textItem.text = entry
        // Set the text

        textItem.typeface = font
        // Set the typeface for the text

        closeButton.setOnClickListener {
            // When the close button is clicked
            val newList = cardList.toMutableList()
            newList.removeAt(position)
            cardList = newList.toTypedArray()
            // Remove the item from the card list
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,cardList.size)
            // Update the recycler view
        }
        imageButton.setOnClickListener {
            // When the image button is pressed
            activity.currentChar = position
            // Set a variable of this position in the parent
            activity.imageChangeButton.performClick()
            // Press and invisible button to open the image selection from the parent
            // This is a hacky workaround but it gets the job done
        }
    }

    fun getItems(): Array<CharEntity>{
        return cardList
        // Return the list of characters
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return cardList.size
        // Returns the character list size element
    }
}