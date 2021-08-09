package com.profmori.foursoulsstatistics.custom_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.PlayerHandler
import com.profmori.foursoulsstatistics.data_handlers.RecyclerHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.ItemList


class CharListAdapter(private val playerHandlerList: Array<PlayerHandler>) : RecyclerView.Adapter<CharListAdapter.ViewHolder>() {

    private var itemList = emptyArray<String>()

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val charImage: ImageView = itemView.findViewById(R.id.inputCharImage)
        // Allows the background image to be set in code
        val charEntry: AutoCompleteTextView = itemView.findViewById(R.id.inputCharEntry)
        // Access the character selection entry in code
        val charPrompt: TextView = itemView.findViewById(R.id.inputCharSelect)
        // Access the character entry prompt
        val playerEntry: AutoCompleteTextView = itemView.findViewById(R.id.inputPlayerEntry)
        // Access the player selection entry in code
        val playerPrompt: TextView = itemView.findViewById(R.id.inputPlayerSelect)
        // Access the player entry prompt
        val eternalEntry: AutoCompleteTextView = itemView.findViewById(R.id.inputEternalEntry)
        // Access the player selection entry in code
        val eternalPrompt: TextView = itemView.findViewById(R.id.inputEternalSelect)
        // Access the eternal entry prompt

    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        // Gets the context of the view

        itemList = ItemList.getItems(context)
        // Gets the list of items

        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.enter_data_char, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position

        val playerHandler: PlayerHandler = playerHandlerList[position]
        // Set item views based on your player handler

        val fonts = playerHandler.fonts

        val background = viewHolder.charImage
        // Get the background image
        val charEntry = viewHolder.charEntry
        // Gets the character entry input
        val charPrompt = viewHolder.charPrompt
        // Gets the character entry prompt

        val playerEntry = viewHolder.playerEntry
        // Gets the player entry box
        val playerPrompt = viewHolder.playerPrompt
        // Gets the player entry prompt

        val eternalEntry = viewHolder.eternalEntry
        // Gets the eternal entry box
        val eternalPrompt = viewHolder.eternalPrompt
        // Gets the eternal entry prompt

        playerPrompt.typeface = fonts["body"]
        playerEntry.typeface = fonts["body"]
        charPrompt.typeface = fonts["body"]
        charEntry.typeface = fonts["body"]
        eternalPrompt.typeface = fonts["body"]
        eternalEntry.typeface = fonts["body"]

        eternalEntry.isEnabled = false
        eternalEntry.visibility = INVISIBLE
        eternalPrompt.visibility = INVISIBLE
        // Hide all the eternal item field

        RecyclerHandler.updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)

        charEntry.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // if the soft input is done
                val imm =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // Get an input method manager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                // Hide the keyboard
                charEntry.clearFocus()
                // Clear the focus of the edit text
                return@setOnEditorActionListener true
            }
            false
        }

        charEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            // When the character entry box loses or gains focus
            if (!hasFocus) {
                // If it has just lost focus
                val charInput = charEntry.text.toString()
                var newChar = charInput
                if ((charInput != "") and !playerHandler.charNames.contains(charInput)) {
                    // If the entered value is not valid
                    newChar = RecyclerHandler.findClosest(charInput, playerHandler.charNames)
                    // Find the closest text value
                }
                playerHandler.updateCharacter(newChar)
                // Update the player to store their new character
                RecyclerHandler.updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
                // Update the stored character
            } else {
                RecyclerHandler.updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
                // Update the image shown
            }
        }

        playerEntry.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // if the soft input is done
                val imm =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // Get an input method manager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                // Hide the keyboard
                playerEntry.clearFocus()
                // Clear the focus of the edit text
                return@setOnEditorActionListener true
            }
            false
        }

        playerEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            // When the player entry box loses or gains focus
            if (!hasFocus) {
                // If it has just lost focus
                val input = playerEntry.text.toString().trim()
                if ((input.lowercase() !in playerHandler.playerNames) and (input != "")) {
                    // If the entered value is not valid
                    RecyclerHandler.createPlayerPopup(
                        playerEntry,
                        playerHandler,
                        playerHandlerList,
                        input,
                        background,
                        charEntry,
                        eternalPrompt,
                        eternalEntry,
                        itemList
                    )
                    // Gives the option to add a new player
                } else {
                    // If an existing player was entered
                    playerHandler.playerName = input.lowercase()
                    // Update the player to store their new character
                    RecyclerHandler.updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
                    // Update the shown images
                }
            } else {
                RecyclerHandler.updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
                // Update the view
            }
        }

        eternalEntry.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // if the soft input is done
                val imm =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // Get an input method manager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                // Hide the keyboard
                eternalEntry.clearFocus()
                // Clear the focus of the edit text
                return@setOnEditorActionListener true
            }
            false
        }

        eternalEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            // When the character entry box loses or gains focus
            if (!hasFocus) {
                // If it has just lost focus
                val eternalInput = eternalEntry.text.toString().lowercase()
                var newEternal = eternalInput
                if ((eternalInput != "") and !itemList.contains(eternalInput)) {
                    // If the entered value is not valid
                    newEternal = RecyclerHandler.findClosest(eternalInput, itemList)
                    // Find the closest text value
                }
                playerHandler.eternal = if (newEternal == "") {
                    // If the new eternal is empty
                    null
                    // The eternal is null
                } else {
                    newEternal.lowercase()
                    // Lowercase the input
                }
                // Update the player to store their new character
                RecyclerHandler.updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
                eternalEntry.setText(TextHandler.capitalise(newEternal))
            } else {
                RecyclerHandler.updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
            }
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return playerHandlerList.size
        // Returns the player list size element
    }
}