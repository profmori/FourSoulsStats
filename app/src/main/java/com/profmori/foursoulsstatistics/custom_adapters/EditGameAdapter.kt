package com.profmori.foursoulsstatistics.custom_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.PlayerHandler
import com.profmori.foursoulsstatistics.data_handlers.RecyclerHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.ItemList


class EditGameAdapter(private val playerHandlerList: Array<PlayerHandler>) : RecyclerView.Adapter<EditGameAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val charImage: ImageView = itemView.findViewById(R.id.adjCharImage)
        // Allows the background image to be set in code
        val playerPrompt: TextView = itemView.findViewById(R.id.adjPlayerSelect)
        val playerEntry: AutoCompleteTextView = itemView.findViewById(R.id.adjPlayerEntry)
        // Access the player entry line
        val charPrompt: TextView = itemView.findViewById(R.id.adjCharSelect)
        val charEntry: AutoCompleteTextView = itemView.findViewById(R.id.adjCharEntry)
        // Access the character entry line
        val eternalPrompt: TextView = itemView.findViewById(R.id.adjEternalSelect)
        val eternalEntry: AutoCompleteTextView = itemView.findViewById(R.id.adjEternalEntry)
        // Access the eternal entry line
        val soulsText: TextView = itemView.findViewById(R.id.adjSoulPrompt)
        val soulsCount: EditText = itemView.findViewById(R.id.adjSoulEntry)
        // Access the soul entry line
        val winnerCheck: CheckBox = itemView.findViewById(R.id.adjWinnerCheck)
        // Access the winner checkbox in code
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.adjust_game_char, parent, false)
        // Return a new holder instance

        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position

        val playerHandler = playerHandlerList[position]
        // Set item views based on your position

        val background = viewHolder.charImage
        // Get the background image

        val playerPrompt = viewHolder.playerPrompt
        val playerEntry = viewHolder.playerEntry
        playerEntry.setText(TextHandler.capitalise(playerHandler.playerName))
        // Set the selected player correctly

        val charPrompt = viewHolder.charPrompt
        val charEntry = viewHolder.charEntry
        // Get the character entry line
        charEntry.setText(TextHandler.capitalise(playerHandler.charName))
        // Set the selected character correctly

        val eternalPrompt = viewHolder.eternalPrompt
        val eternalEntry = viewHolder.eternalEntry
        // Get the eternal entry line
        if(playerHandler.eternal.isNullOrBlank()){
        // If the player doesn't have an eternal
            eternalEntry.isEnabled = false
            eternalPrompt.visibility = View.INVISIBLE
            eternalEntry.visibility = View.INVISIBLE
        // Hide everything
        }else{
            eternalEntry.setText(TextHandler.capitalise(playerHandler.eternal!!))
        }
        // Set the eternal visibility and text

        val soulsText = viewHolder.soulsText
        val soulsCount = viewHolder.soulsCount
        // Gets the soul input box

        var soulNumber = playerHandler.soulsNum
        // Gets the soul number
        soulsCount.setText(soulNumber.toString())
        // Sets the soul number to be accurate

        val winnerTick = viewHolder.winnerCheck
        // Gets the winner tick box
        winnerTick.isChecked = playerHandler.winner
        // Tick the current winner

        val itemList = ItemList.getItems(playerEntry.context)
        // Gets the list of items

        RecyclerHandler.updateView(playerHandler,background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)

        val fonts = playerHandler.fonts

        playerPrompt.typeface = fonts["body"]
        playerEntry.typeface = fonts["body"]

        charPrompt.typeface = fonts["body"]
        charEntry.typeface = fonts["body"]

        eternalPrompt.typeface = fonts["body"]
        eternalEntry.typeface = fonts["body"]

        soulsText.typeface = fonts["body"]
        soulsCount.typeface = fonts["body"]

        winnerTick.typeface = fonts["body"]

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

        playerEntry.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
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
                    RecyclerHandler.updateView(
                        playerHandler,
                        background,
                        playerEntry,
                        charEntry,
                        eternalPrompt,
                        eternalEntry,
                        itemList
                    )
                    // Update the shown images
                }
            } else {
                RecyclerHandler.updateView(
                    playerHandler,
                    background,
                    playerEntry,
                    charEntry,
                    eternalPrompt,
                    eternalEntry,
                    itemList
                )
                // Update the view
            }
        }

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

        charEntry.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
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
                RecyclerHandler.updateView(
                    playerHandler,
                    background,
                    playerEntry,
                    charEntry,
                    eternalPrompt,
                    eternalEntry,
                    itemList
                )
                // Update the stored character
            } else {
                RecyclerHandler.updateView(
                    playerHandler,
                    background,
                    playerEntry,
                    charEntry,
                    eternalPrompt,
                    eternalEntry,
                    itemList
                )
                // Update the image shown
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

        eternalEntry.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
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
                RecyclerHandler.updateView(
                    playerHandler,
                    background,
                    playerEntry,
                    charEntry,
                    eternalPrompt,
                    eternalEntry,
                    itemList
                )
                eternalEntry.setText(TextHandler.capitalise(newEternal))
            } else {
                RecyclerHandler.updateView(
                    playerHandler,
                    background,
                    playerEntry,
                    charEntry,
                    eternalPrompt,
                    eternalEntry,
                    itemList
                )
            }
        }

        soulsCount.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // if the soft input is done
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // Get an input method manager
                imm.hideSoftInputFromWindow(view.windowToken,0)
                // Hide the keyboard
                soulsCount.clearFocus()
                // Clear the focus of the edit text
                return@setOnEditorActionListener true
            }
            false
        }

        soulsCount.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val oldSoulNumber = soulNumber
                try{
                    soulNumber = soulsCount.text.toString().toInt()
                    // Try and set the soul number from the text input
                }
                catch (e: NumberFormatException){
                // If the input cannot be made into a number
                    soulNumber = oldSoulNumber
                    // Use the old soul number
                }
                finally {
                    playerHandler.soulsNum = soulNumber
                    soulsCount.setText(playerHandler.soulsNum.toString())
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
                soulsCount.setText("")
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
        return playerHandlerList.size
        // Returns the player list size element
    }
}