package com.profmori.foursoulsstatistics.custom_adapters

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.EditSingleGame
import com.profmori.foursoulsstatistics.R
import java.text.SimpleDateFormat
import java.util.*


class GamesListAdapter(private var gameList: Array<String>, private val buttonBG: Int, private val font: Typeface) : RecyclerView.Adapter<GamesListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val gameButton: TextView = itemView.findViewById(R.id.editGameButton)
        // Allows the text to be set in code
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.edit_game_list, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position

        val gameButton = viewHolder.gameButton
        // Get the text entry item

        val shownGame = gameList[position]

        val timeCode = shownGame.substring(6).toLong()
        // Extract the timecode from the gameID

        val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm",Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        val time = formatter.format(timeCode)
        // Format it as a date and time

        gameButton.text = time
        // Write the date and time on the button

        gameButton.typeface = font
        // Set the typeface for the button

        gameButton.setBackgroundResource(buttonBG)
        // Set the image button

        gameButton.setOnClickListener {
            val editGame = Intent(gameButton.context, EditSingleGame::class.java)
            editGame.putExtra("gameID",shownGame)
            // Pass the selected game id to the new activity
            gameButton.context.startActivity(editGame)
            // Start the new activity
        }

    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return gameList.size
        // Returns the card list size element
    }
}