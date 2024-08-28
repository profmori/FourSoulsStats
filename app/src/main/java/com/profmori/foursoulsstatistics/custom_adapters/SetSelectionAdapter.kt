package com.profmori.foursoulsstatistics.custom_adapters

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler

class SetSelectionAdapter(
    private val iconList: Array<String>,
    private val textFont: Typeface,
    private val pixelLine: ConstraintLayout,
    private val customButton: Button
) : RecyclerView.Adapter<SetSelectionAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val icon: ImageView = itemView.findViewById(R.id.mainIcon)

        // Allows the background image to be set in code
        val iconText: TextView = itemView.findViewById(R.id.iconText)
        // Access the icon description line
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.select_icon, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position
        val currIcon = iconList[position]
        // Get the current icon name
        viewHolder.icon.setImageResource(ImageHandler.getIcon(currIcon))
        // Set the associated icon
        var settings = SettingsHandler.readSettings(viewHolder.itemView.context)
        if (settings[currIcon].toBoolean()) {
            // If the icon is currently selected
            viewHolder.icon.setColorFilter(Color.argb(0, 255, 255, 255))
            // Make the tint clear so it has its associated colour
        } else {
            if (currIcon != "base") {
                // If not the base icon then it can be tinted
                viewHolder.icon.setColorFilter(Color.argb(255, 255, 255, 255))
                // Tint the icon white
                if (currIcon == "retro") {
                    viewHolder.icon.setImageResource(ImageHandler.getIcon("retro_off"))
                } else if (currIcon == "summer") {
                    viewHolder.icon.setImageResource(ImageHandler.getIcon("summer_off"))
                }
            } else {
                // If the base icon returns false then the setting is incorrect
                viewHolder.icon.setColorFilter(Color.argb(0, 255, 255, 255))
                // Clear tint
                settings[currIcon] = "true"
                // Correct the setting
                SettingsHandler.saveSettings(viewHolder.itemView.context, settings)
            }
        }

        viewHolder.icon.setOnClickListener {
            // When one of the icons is clicked
            if (currIcon != "base") {
                // If it isn't the base game which cannot be turned off
                settings = SettingsHandler.readSettings(viewHolder.itemView.context)
                val newSetting = !settings[currIcon].toBoolean()
                // Get the new setting as the inverse of the old
                settings[currIcon] = newSetting.toString()
                // Update the settings list
                notifyItemChanged(position)
                // Update the view with the new setting
                if (currIcon == "custom") {
                    // If the custom icon has been clicked
                    customButton.visibility = if (newSetting) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                    // Match the visibility of the custom button
                } else if (currIcon == "retro") {
                    pixelLine.visibility = if (newSetting) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }
            SettingsHandler.saveSettings(viewHolder.itemView.context, settings)
        }
        viewHolder.iconText.setText(TextHandler.getIconText(currIcon))
        // Get the appropriate text from the text handler
        viewHolder.iconText.typeface = textFont
        // Set the font correctly
        val nameSize =
            20.0 - 0.7 * TextHandler.wordLength(viewHolder.iconText.text.toString()).toDouble()
        // Vary the name size based on word length to fit
        viewHolder.iconText.textSize = nameSize.toFloat()
        // Update the text size
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return iconList.size
        // Returns the icon list size element
    }
}