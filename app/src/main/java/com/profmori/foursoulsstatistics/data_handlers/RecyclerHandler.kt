package com.profmori.foursoulsstatistics.data_handlers

import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.view.View
import android.widget.*
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.custom_adapters.DropDownAdapter

class RecyclerHandler {
    companion object{
        private fun createPlayerPopup(
            playerEntry: AutoCompleteTextView,
            playerHandler: PlayerHandler,
            playerHandlerList: Array<PlayerHandler>,
            inputText: String,
            background: ImageView,
            charEntry: AutoCompleteTextView,
            eternalPrompt: TextView,
            eternalEntry: AutoCompleteTextView,
            itemList: Array<String>
        ) {
            // When the player does not match an existing player this is called
            var nameAdded = false
            // Stores whether or not a new name has been added - false by default
            val newPlayerPopup = PopupMenu(playerEntry.context,playerEntry)
            // Creates a new pop-up menu for adding a player
            newPlayerPopup.menuInflater.inflate(R.menu.new_player_menu, newPlayerPopup.menu)
            // Inflates the menu from the provided menu layout
            val changeName = newPlayerPopup.menu.findItem(R.id.addName)
            // Gets the menu item which corresponds to adding the name (currently the only one)
            var addPlayer: String = playerEntry.context.getString(R.string.add_player_1)
            addPlayer += " $inputText"
            addPlayer += " " + playerEntry.context.getString(R.string.add_player_2)
            // Add the strings together to make the prompt
            changeName.title = TextHandler.capitalise(addPlayer.lowercase())
            // Changes the menu item text

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // If the version is after P
                val titleString = SpannableString(changeName.title)
                // Create a spannable string for the title text
                titleString.setSpan(
                    TypefaceSpan(playerHandler.fonts["body"]!!),0,titleString.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                // Set the title string with font
                changeName.title = titleString
                // Set the title
            }

            newPlayerPopup.setOnMenuItemClickListener {
                // When a menu item is clicked - can only be the change name item
                playerHandlerList.forEach {
                    // For every player handler in the list
                    it.playerNames+= arrayOf(inputText.lowercase())
                    // Add the new text to the players name list
                    it.playerNames.sort()
                    // Sort the player names alphabetically again
                }
                playerHandler.playerName = inputText.lowercase()
                // Update the stored player to the new value
                nameAdded  = true
                true
                // Needed to return a unit? Not sure what this does
            }

            newPlayerPopup.setOnDismissListener {
                // When the new player popup is dismissed
                if (!nameAdded) {
                    // If no name was added by this menu
                    val newText = findClosest(inputText, playerHandler.playerNames)
                    // Find the closest text value
                    playerHandler.playerName = newText.lowercase()
                    // Update the stored player to the new value
                }
                updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
                // Update the recycler view
            }
            newPlayerPopup.show()
            // Show the pop-up menu
        }

        fun enterChar(
            hasFocus: Boolean,
            playerEntry: AutoCompleteTextView,
            playerHandler: PlayerHandler,
            background: ImageView,
            charEntry: AutoCompleteTextView,
            eternalPrompt: TextView,
            eternalEntry: AutoCompleteTextView,
            itemList: Array<String>
        ){
            if (!hasFocus) {
                // If it has just lost focus
                val charInput = charEntry.text.toString()
                // Get the input to the character field
                var newChar = charInput
                // Store the character input in a mutable variable
                if ((charInput != "") and !playerHandler.charNames.contains(charInput)) {
                    // If the entered value is not valid
                    newChar = findClosest(charInput, playerHandler.charNames)
                    // Find the closest text value
                }
                playerHandler.updateCharacter(newChar)
                // Update the player to store their new character
            }
            updateView(
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

        fun enterEternal(hasFocus: Boolean,
                         playerEntry: AutoCompleteTextView,
                         playerHandler: PlayerHandler,
                         background: ImageView,
                         charEntry: AutoCompleteTextView,
                         eternalPrompt: TextView,
                         eternalEntry: AutoCompleteTextView,
                         itemList: Array<String>){
            if (!hasFocus) {
                // If it has just lost focus
                val eternalInput = eternalEntry.text.toString().lowercase()
                // Get the input to the eternal field
                var newEternal = eternalInput
                // Store the eternal input in a mutable variable
                if ((eternalInput != "") and !itemList.contains(eternalInput)) {
                    // If the entered value is not valid
                    newEternal = findClosest(eternalInput, itemList)
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
                eternalEntry.setText(TextHandler.capitalise(newEternal))
                // Set the text of the eternal entry correctly
            }
            updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
            // Update the view to include all correct data
        }

        fun enterPlayer(
            hasFocus: Boolean,
            playerEntry: AutoCompleteTextView,
            playerHandler: PlayerHandler,
            playerHandlerList: Array<PlayerHandler>,
            background: ImageView,
            charEntry: AutoCompleteTextView,
            eternalPrompt: TextView,
            eternalEntry: AutoCompleteTextView,
            itemList: Array<String>
        ){
            if (!hasFocus) {
                // If it has just lost focus
                val input = playerEntry.text.toString().trim()
                // Remove leading and trailing spaces to get the input
                if ((input.lowercase() !in playerHandler.playerNames) and (input != "")) {
                    // If the entered value is not valid
                    createPlayerPopup(
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
                    updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
                    // Update the shown images
                }
            } else {
                updateView(playerHandler, background, playerEntry, charEntry, eternalPrompt, eternalEntry, itemList)
                // Update the view
            }
        }

        fun enterSouls(hasFocus: Boolean, soulsBox: EditText, winnerTick: CheckBox, playerHandler: PlayerHandler){
            var soulNumber = soulsBox.text.toString().toInt()
            // Gets the soul input field
            if (!hasFocus) {
                // If the soul box has just lost focus
                val oldSoulNumber = soulNumber
                // Store the old number of souls
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
                    // Set the soul number to be the stored soul number
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
            } else if(soulNumber == 0){
                // If they have no souls on input
                soulsBox.setText("")
                // Clear the souls box
            }
        }

        private fun findClosest(wrongText: String, correctList: Array<String>):String {
            val spaceWrong = " $wrongText"
            // Creates a version of the wrong text preceded by a space
            val wrongLen = wrongText.length
            // Gets the length of the incorrect text snippet
            var returnString = ""
            // Sets the default for no matches as a blank string
            for (element in correctList) {
                // Iterates through the list
                try {
                    if (element.substring(0, wrongLen).contains(wrongText, ignoreCase = true)){
                        // If the current list entry starts with the entered string
                        return element
                        // Return the entry immediately
                    }
                    else if (element.contains(spaceWrong, ignoreCase = true)) {
                        // Or if the entered string is the start of any word in the list entry
                        if (returnString == ""){
                            // If this is the first time that it has matched a substring with a space
                            returnString = element
                            // Set the return value to the matching element
                        }

                    }
                } catch (e: StringIndexOutOfBoundsException) {
                }
                // If the character name is too short, do nothing - it cannot be a match
            }
            return returnString
            // Return the corrected value if the starting character was not matched
        }

        fun updateView(
            playerHandler: PlayerHandler,
            background: ImageView,
            playerEntry: AutoCompleteTextView,
            charEntry: AutoCompleteTextView,
            eternalPrompt: TextView,
            eternalEntry: AutoCompleteTextView,
            itemList: Array<String>
        ){

            if (playerHandler.charImage > -1) {
                background.setImageResource(playerHandler.charImage)
                // Set the image to the stored player image if there is one
            }else{
                background.setImageBitmap(ImageHandler.returnImage(background.context, playerHandler.charName))
                // Set the image to a custom bitmap if it is custom and an image is provided
            }
            val pDropDown = playerHandler.playerNames
            // Gets a list of player names
            val playerAdaptor = DropDownAdapter(playerEntry.context, pDropDown, playerHandler.fonts["body"]!!)
            // Creates an array adapter to hold the player name list
            playerEntry.setAdapter(playerAdaptor)
            // Sets the adapter for this list
            playerEntry.setText(TextHandler.capitalise(playerHandler.playerName))
            // Fill in the player name if this is an update

            val cDropDown = playerHandler.charNames
            // Gets the list of character names
            val charAdapter = DropDownAdapter(charEntry.context, cDropDown, playerHandler.fonts["body"]!!)
            // Creates an array adapter to hold the character list
            charEntry.setAdapter(charAdapter)
            // Sets the adapter for this list
            charEntry.setText(TextHandler.capitalise(playerHandler.charName))
            // Fill in the character name if this is an update

            if (playerHandler.charName == "eden"){
                // If eden is selected
                eternalEntry.isEnabled = true
                eternalEntry.visibility = View.VISIBLE
                eternalPrompt.visibility = View.VISIBLE
                // Show the eternal entry box

                val eternalAdapter = DropDownAdapter(charEntry.context, itemList, playerHandler.fonts["body"]!!)
                // Creates an array adapter to hold the item list
                eternalEntry.setAdapter(eternalAdapter)
                // Sets the adapter for this list
                var eternal = playerHandler.eternal
                // Sets the eternal to the current stored eternal
                if (eternal.isNullOrEmpty()){eternal = ""}
                // If the eternal doesn't exist set it to be blank
                eternalEntry.setText(TextHandler.capitalise(eternal))
                // Fill out the input box
            }
            else{
                // If Eden is not being played
                eternalEntry.isEnabled = false
                eternalEntry.visibility = View.INVISIBLE
                eternalPrompt.visibility = View.INVISIBLE
                // Hide all the eternal prompt box
                playerHandler.eternal = null
                // Nulls out the stored eternal
            }
        }
    }
}