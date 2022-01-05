package com.profmori.foursoulsstatistics.custom_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.changeFont
import com.profmori.foursoulsstatistics.data_handlers.TextHandler

class RerollSelectionAdapter(
    private val keys: Array<Int>,
    private val rerollPrefs: MutableMap<Int, Boolean>,
    private val context: Context
) : RecyclerView.Adapter<RerollSelectionAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val rerollIcon: ImageView = itemView.findViewById(R.id.mainReroll)

        // Allows the background image to be set in code
        val checkIcon: ImageView = itemView.findViewById(R.id.rerollCheck)
        // Access the icon description line
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.select_reroll, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val currRerollIcon = keys[position]
        // Get the current icon number
        viewHolder.rerollIcon.setImageResource(currRerollIcon)
        // Set the associated icon
        if (rerollPrefs[currRerollIcon] == true) {
            // If the icon is in use
            viewHolder.checkIcon.setImageResource(R.drawable.green_tick)
            // Draw the green tick
        } else {
            viewHolder.checkIcon.setImageResource(R.drawable.red_cross)
            // Otherwise draw the red cross (not in use)
        }

        viewHolder.rerollIcon.setOnClickListener {
            // When the icon is clicked
            rerollPrefs[currRerollIcon] = !rerollPrefs[currRerollIcon]!!
            // Invert the choice of the user
            if (rerollPrefs.values.contains(true)) {
                // If at least one icon is selected
                notifyItemChanged(position)
                // Update the view to match the new settings
            } else {
                rerollPrefs[currRerollIcon] = true
                // Keep the current icon selected
                val noRerollSnackbar = Snackbar.make(
                    viewHolder.itemView,
                    R.string.reroll_no_icons,
                    Snackbar.LENGTH_LONG
                )
                // Create the snackbar to say one must be chosen
                noRerollSnackbar.changeFont(TextHandler.setFont(context)["body"]!!)
                // Set the font of the snackbar
                noRerollSnackbar.show()
                // Show the snackbar
            }
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return keys.size
        // Returns the icon list size element
    }
}