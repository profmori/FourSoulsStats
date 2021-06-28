package com.example.foursoulsstatistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView


class ResultsListAdaptor(private val playerList: ArrayList<Player>) : RecyclerView.Adapter<ResultsListAdaptor.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val charImage: ImageView = itemView.findViewById(R.id.winCharImage)!!
        // Allows the background image to be set in code
        val playerName: TextView = itemView.findViewById(R.id.winPlayerName)
        // Access the player name
        val soulsCount: EditText = itemView.findViewById(R.id.winSoulNumber)
        // Access the player selection entry in code
        val winnerCheck: CheckBox = itemView.findViewById(R.id.winnerCheckbox)
        // Access the winner checkbox in code
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsListAdaptor.ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.win_entry_layout, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ResultsListAdaptor.ViewHolder, position: Int) {
        // Get the data model based on position

        val player: Player = playerList[position]
        // Set item views based on your player

        val background = viewHolder.charImage
        // Get the background image
        background.setImageResource(player.charImage)
        // Set the image to the stored player image

        val playerEntry = viewHolder.playerName
        // Gets the character entry input box
        val playerText = "${playerEntry.context.getString(R.string.win_card_player)} ${player.playerName}"
        // Create a text string which adds the player name
        playerEntry.text = playerText
        // Set the player text entry to have the player name

        val soulsBox = viewHolder.soulsCount
        // Gets the soul input box

        var soulNumber = soulsBox.text.toString().toInt()
        val winnerTick = viewHolder.winnerCheck
        // Gets the winner tick box

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
                    soulsBox.setText(soulNumber)
                    // Fill the text box back in
                }
                finally {
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
            player.winner = winnerTick.isChecked
            // Sets the player variable to this person's winner status
        }
}

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return playerList.size
        // Returns the player list size element
    }

}