package com.example.foursoulsstatistics.custom_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foursoulsstatistics.data_handlers.PlayerHandler
import com.example.foursoulsstatistics.R
import com.example.foursoulsstatistics.data_handlers.TextHandler


class ResultsListAdaptor(private val playerList: Array<PlayerHandler>) : RecyclerView.Adapter<ResultsListAdaptor.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val charImage: ImageView = itemView.findViewById(R.id.winCharImage)!!
        // Allows the background image to be set in code
        val playerName: TextView = itemView.findViewById(R.id.winPlayerName)
        // Access the player name
        val soulsText: TextView = itemView.findViewById(R.id.winSoulPrompt)
        // Access the prompt to say souls
        val soulsCount: EditText = itemView.findViewById(R.id.winSoulNumber)
        // Access the player selection entry in code
        val winnerCheck: CheckBox = itemView.findViewById(R.id.winnerCheckbox)
        // Access the winner checkbox in code
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.win_entry_layout, parent, false)
        // Return a new holder instance

        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position

        val playerHandler: PlayerHandler = playerList[position]
        // Set item views based on your player

        val fonts = playerHandler.fonts

        val background = viewHolder.charImage
        // Get the background image

        val charImage = playerHandler.charImage
        // Select a random character image

        background.setImageResource(charImage)
        // Set the image to the stored player

        val playerEntry = viewHolder.playerName
        // Gets the character entry input box
        val playerName = TextHandler.capitalise(playerHandler.playerName.lowercase())
        val playerText = "${playerEntry.context.getString(R.string.win_card_player)} $playerName"
        // Create a text string which adds the player name
        playerEntry.text = playerText
        // Set the player text entry to have the player name

        val soulsBox = viewHolder.soulsCount
        // Gets the soul input box

        var soulNumber = soulsBox.text.toString().toInt()
        // Gets the soul input field

        val soulsText = viewHolder.soulsText

        val winnerTick = viewHolder.winnerCheck
        // Gets the winner tick box

        if(playerEntry.typeface != fonts["body"]){
            playerEntry.typeface = fonts["body"]
            soulsBox.typeface = fonts["body"]
            winnerTick.typeface = fonts["body"]
            soulsText.typeface = fonts["body"]
        }

        soulsBox.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val oldSoulNumber = soulNumber
                try{
                    soulNumber = soulsBox.text.toString().toInt()
                    // Try and set the soul number from the text input
                }
                catch (e: NumberFormatException){
                // If the input cannot be made into a number
                    soulNumber = oldSoulNumber
                    // Use the old soul number
                }
                finally {
                    playerHandler.soulsNum = soulNumber
                    soulsBox.setText(playerHandler.soulsNum.toString())
                    // Fill the text box back in
                    if (soulNumber >= 4) {
                    // If there are more than four souls
                        winnerTick.isChecked = true
                        // Assume this person has won
                    } else if (winnerTick.isChecked) {
                    // If they have less than four souls and have been marked as winner
                        winnerTick.isChecked = false
                        // Remove their winner status
                    }
                }
            }
            else if(soulNumber == 0){
            // If they have no souls on input
                soulsBox.setText("")
                // Clear the souls box
            }
        }

        winnerTick.setOnCheckedChangeListener { _, _ ->
            playerHandler.winner = winnerTick.isChecked
            // Sets the player variable to this person's winner status
        }
}

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return playerList.size
        // Returns the player list size element
    }
}