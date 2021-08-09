package com.profmori.foursoulsstatistics.data_handlers

import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.custom_adapters.DropDownAdapter

class RecyclerHandler {
    companion object{
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
                // Set the image to the stored player image
            }else{
                background.setImageBitmap(ImageHandler.returnImage(background.context, playerHandler.charName))
                // Set the image to a custom bitmap
            }
            val pDropDown = playerHandler.playerNames
            pDropDown.sort()
            val playerAdaptor = DropDownAdapter(playerEntry.context, pDropDown, playerHandler.fonts["body"]!!)
            // Creates an array adapter to hold the player name list
            playerEntry.setAdapter(playerAdaptor)
            // Sets the adapter for this list
            playerEntry.setText(TextHandler.capitalise(playerHandler.playerName))
            // Fill in the player name if this is an update

            val cDropDown = playerHandler.charNames
            cDropDown.sort()
            val charAdapter = DropDownAdapter(charEntry.context, cDropDown, playerHandler.fonts["body"]!!)
            // Creates an array adapter to hold the character list
            charEntry.setAdapter(charAdapter)
            // Sets the adapter for this list
            charEntry.setText(TextHandler.capitalise(playerHandler.charName))
            // Fill in the character name if this is an update

            if (playerHandler.charName == "eden"){
                eternalEntry.isEnabled = true
                eternalEntry.visibility = View.VISIBLE
                eternalPrompt.visibility = View.VISIBLE
                // Show the eternal entry box

                val eternalAdapter = DropDownAdapter(charEntry.context, itemList, playerHandler.fonts["body"]!!)
                // Creates an array adapter to hold the item list
                eternalEntry.setAdapter(eternalAdapter)
                // Sets the adapter for this list
                var eternal = playerHandler.eternal
                if (eternal.isNullOrEmpty()){eternal = ""}
                eternalEntry.setText(TextHandler.capitalise(eternal))
            }
            else{
                eternalEntry.isEnabled = false
                eternalEntry.visibility = View.INVISIBLE
                eternalPrompt.visibility = View.INVISIBLE
                playerHandler.eternal = null
            }
        }

        fun findClosest(wrongText: String, correctList: Array<String>):String {
            val spaceWrong = " $wrongText"
            // Creates a version of the wrong text preceded by a space
            val wrongLen = wrongText.length
            // Gets the length of the incorrect text snippet
            for (element in correctList) {
                // Iterates through the list
                try {
                    if (element.substring(0, wrongLen).contains(wrongText, ignoreCase = true)
                        // If the current list entry starts with the entered string
                        or element.contains(spaceWrong, ignoreCase = true)
                    ) {
                        // Or if the entered string is the start of any word in the list entry
                        return element
                        // Return the list entry
                    }
                } catch (e: StringIndexOutOfBoundsException) {
                }
                // If the character name is too short, do nothing - it cannot be a match
            }
            return ""
            // If no character is found reset the box
        }
        
        fun createPlayerPopup(
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
                val titleString = SpannableString(changeName.title)
                titleString.setSpan(
                    TypefaceSpan(playerHandler.fonts["body"]!!),0,titleString.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                changeName.title = titleString
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
    }
}