package com.profmori.foursoulsstatistics.custom_adapters

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.PlayerHandler
import com.profmori.foursoulsstatistics.data_handlers.RecyclerHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import kotlin.math.abs


class ResultsListAdapter(
    private val playerList: Array<PlayerHandler>,
    private val coOpGame: Boolean
) :
    RecyclerView.Adapter<ResultsListAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val charImage: ImageView = itemView.findViewById(R.id.winCharImage)

        // Allows the background image to be set in code
        val itemText: TextView = itemView.findViewById(R.id.winEdenItemText)

        // Access the starting item text
        val eternalText: TextView = itemView.findViewById(R.id.winEdenItem)

        // Access the eternal text
        val playerName: TextView = itemView.findViewById(R.id.winPlayerName)

        // Access the player name
        val soulsText: TextView = itemView.findViewById(R.id.winSoulPrompt)

        // Access the prompt to say souls
        val soulsCount: EditText = itemView.findViewById(R.id.winSoulEntry)

        // Access the player selection entry in code
        val winnerCheck: CheckBox = itemView.findViewById(R.id.winWinnerCheck)
        // Access the winner checkbox in code
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.enter_result_char, parent, false)
        // Return a new holder instance

        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position

        val playerHandler = playerList[position]
        // Set item views based on your player

        val background = viewHolder.charImage
        // Get the background image

        if (playerHandler.charImage > -1) {
            background.setImageResource(playerHandler.charImage)
            // Set the image to the stored player image
        } else {
            background.setImageBitmap(
                ImageHandler.returnImage(
                    background.context,
                    playerHandler.charName
                )
            )
            // Set the image to a custom bitmap
        }

        val playerEntry = viewHolder.playerName
        // Gets the character entry input box
        val playerName = TextHandler.capitalise(playerHandler.playerName.lowercase())
        val playerText = "${playerEntry.context.getString(R.string.win_card_player)} $playerName"
        // Create a text string which adds the player name
        playerEntry.text = playerText
        // Set the player text entry to have the player name

        val soulsBox = viewHolder.soulsCount
        // Gets the soul input box

        val soulsText = viewHolder.soulsText
        // Gets the soul prompt for the user

        val winnerTick = viewHolder.winnerCheck
        // Gets the winner tick box

        val itemText = viewHolder.itemText
        // Get the item text box

        val eternalText = viewHolder.eternalText
        // Get the eternal text box

        var fonts = playerHandler.fonts
        var fontSize = 20f

        if (playerHandler.eternal.isNullOrBlank()) {
            // If there is no eternal
            itemText.visibility = View.INVISIBLE
            eternalText.visibility = View.INVISIBLE
            // Hide the item and eternal text
        } else {
            eternalText.text = playerHandler.eternal
            // Set the eternal to the correct string
            if (playerHandler.charImage == R.drawable.ret_eden) {
                fonts = TextHandler.updateRetroFont(background.context, playerHandler.fonts)
                // Get the fonts used for the character
                fontSize = 15f
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    eternalText,
                    7,
                    12,
                    1,
                    TypedValue.COMPLEX_UNIT_SP
                )
                // Autoscale the eternal text
                eternalText.typeface = fonts["body"]
                itemText.textSize = 8f
                itemText.typeface = fonts["body"]
            } else {
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    eternalText,
                    10,
                    15,
                    1,
                    TypedValue.COMPLEX_UNIT_SP
                )
                // Autoscale the eternal text
            }
        }

        playerEntry.typeface = fonts["body"]
        playerEntry.textSize = fontSize
        soulsBox.typeface = fonts["body"]
        soulsBox.textSize = fontSize
        winnerTick.typeface = fonts["body"]
        winnerTick.textSize = fontSize
        soulsText.typeface = fonts["body"]
        soulsText.textSize = fontSize
        // Set all the fonts for the entries correctly

        if (coOpGame) {
            soulsBox.visibility = View.GONE
            soulsText.visibility = View.GONE
            playerHandler.soulsNum = 0
        }

        soulsBox.setText(playerHandler.soulsNum.toString())

        winnerTick.isChecked = playerHandler.winner

        soulsBox.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // if the soft input is done
                val imm =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // Get an input method manager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                // Hide the keyboard
                soulsBox.clearFocus()
                // Clear the focus of the edit text
                return@setOnEditorActionListener true
            }
            false
        }

        soulsBox.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            // When the soul input box changes focus
            RecyclerHandler.enterSouls(hasFocus, soulsBox, winnerTick, playerHandler)
            // Handles all the logic for the souls entry box
        }

        winnerTick.setOnCheckedChangeListener { _, _ ->
            playerHandler.winner = winnerTick.isChecked
            // Sets the player variable to this person's winner status
            if (coOpGame) {
                val otherPosition = abs(position - 1)
                val otherPlayer = playerList[otherPosition]
                otherPlayer.winner = winnerTick.isChecked
                notifyItemChanged(otherPosition)
            }
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return playerList.size
        // Returns the player list size element
    }
}