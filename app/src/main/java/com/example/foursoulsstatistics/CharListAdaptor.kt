package com.example.foursoulsstatistics

import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CharListAdaptor(private val playerHandlerList: Array<PlayerHandler>) : RecyclerView.Adapter<CharListAdaptor.ViewHolder>() {

    private var nameAdded: Boolean = false
    // Initialises the name added boolean

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val charImage: ImageView = itemView.findViewById(R.id.charSelectImage)
        // Allows the background image to be set in code
        val charEntry: AutoCompleteTextView = itemView.findViewById(R.id.charTextEntry)
        // Access the character selection entry in code
        val charPrompt: TextView = itemView.findViewById(R.id.charSelectText)
        // Access the character entry prompt
        val playerEntry: AutoCompleteTextView = itemView.findViewById(R.id.playerTextEntry)
        // Access the player selection entry in code
        val playerPrompt: TextView = itemView.findViewById(R.id.playerSelectText)
        // Access the player entry prompt

    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharListAdaptor.ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.char_select_layout, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: CharListAdaptor.ViewHolder, position: Int) {
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

        if(playerEntry.typeface != fonts["body"]){
            playerPrompt.typeface = fonts["body"]
            playerEntry.typeface = fonts["body"]
            charPrompt.typeface = fonts["body"]
            charEntry.typeface = fonts["body"]
        }

        updateView(playerHandler,background,playerEntry, charEntry)

        charEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
        // When the character entry box loses or gains focus
            if (!hasFocus) {
            // If it has just lost focus
                val charInput = charEntry.text.toString()
                var newChar = charInput
                if ((charInput != "") and !playerHandler.charNames.contains(charInput)) {
                // If the entered value is not valid
                    newChar = findClosest(charInput, playerHandler.charNames)
                    // Find the closest text value
                }
                playerHandler.updateCharacter(newChar)
                // Update the player to store their new character
                updateView(playerHandler,background,playerEntry,charEntry)
            }
            else{
                val cDropDown = playerHandler.charNames
                cDropDown.sort()
                val charAdapter = DropDownAdapter(charEntry.context, cDropDown, fonts["body"]!!)
                // Creates an array adapter to hold the character list
                charEntry.setAdapter(charAdapter)
                // Sets the adapter for this list
            }
        }

        playerEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
        // When the character entry box loses or gains focus
        if (!hasFocus) {
            // If it has just lost focus
            val input = playerEntry.text.toString()
            if ((input.lowercase() !in playerHandler.playerNames) and (input != "")) {
                // If the entered value is not valid
                createPlayerPopup(playerEntry, playerHandler, input, background, charEntry)
                // Gives the option to add a new player
            }
            else{
                playerHandler.playerName = input.lowercase()
                // Update the player to store their new character
                updateView(playerHandler,background,playerEntry,charEntry)
            }
        }
        else if (hasFocus and nameAdded){
        // If the player list has been updated since this was first run, and this is now being focused (text entered)
            updateView(playerHandler,background, playerEntry,charEntry)
            nameAdded = false
        }
    }
}
    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return playerHandlerList.size
        // Returns the player list size element
    }

    private fun findClosest(wrongText: String, correctList: Array<String>):String {
        val spaceWrong = " $wrongText"
        // Creates a version of the wrong text preceded by a space
        val wrongLen = wrongText.length
        // Gets the length of the incorrect text snippet
        for (element in correctList){
        // Iterates through the list
            try {
                if (element.substring(0, wrongLen).contains(wrongText, ignoreCase = true)
                // If the current list entry starts with the entered string
                        or element.contains(spaceWrong, ignoreCase = true)) {
                        // Or if the entered string is the start of any word in the list entry
                    return element
                    // Return the list entry
                }
            }
            catch(e: StringIndexOutOfBoundsException){}
            // If the character name is too short, do nothing - it cannot be a match
        }
        return ""
        // If no character is found reset the box
    }

    private fun createPlayerPopup(
        playerEntry: AutoCompleteTextView,
        playerHandler: PlayerHandler,
        inputText: String,
        background: ImageView,
        charEntry: AutoCompleteTextView
    ) {
    // When the player does not match an existing player this is called
        nameAdded = false
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
            titleString.setSpan(TypefaceSpan(playerHandler.fonts["body"]!!),0,titleString.length,Spannable.SPAN_INCLUSIVE_INCLUSIVE)
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
            updateView(playerHandler,background,playerEntry,charEntry)
            // Update the recycler view
         }
        newPlayerPopup.show()
        // Show the pop-up menu
    }

    private fun updateView(playerHandler: PlayerHandler, background: ImageView, playerEntry: AutoCompleteTextView, charEntry: AutoCompleteTextView){

        background.setImageResource(playerHandler.charImage)
        // Set the image to the stored player image

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
    }
}