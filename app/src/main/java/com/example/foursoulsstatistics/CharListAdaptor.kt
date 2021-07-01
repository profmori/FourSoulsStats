package com.example.foursoulsstatistics

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.foursoulsstatistics.database.CharEntity
import com.example.foursoulsstatistics.database.GameDAO
import com.example.foursoulsstatistics.database.GameDataBase
import com.example.foursoulsstatistics.database.Player

class CharListAdaptor(private val playerHandlerList: List<PlayerHandler>) : RecyclerView.Adapter<CharListAdaptor.ViewHolder>() {

    private var nameAdded: Boolean = false
    // Initialises the name added boolean
    private var charList: Array<CharEntity> = emptyArray()
    private var playerList: Array<Player> = emptyArray()
    private var charNames: Array<String> = emptyArray()
    private var playerNames: Array<String> = emptyArray()

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val charImage: ImageView = itemView.findViewById(R.id.charSelectImage)
        // Allows the background image to be set in code
        val charEntry: AutoCompleteTextView = itemView.findViewById(R.id.charTextEntry)
        // Access the character selection entry in code
        val playerEntry: AutoCompleteTextView = itemView.findViewById(R.id.playerTextEntry)
        // Access the player selection entry in code
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

        val background = viewHolder.charImage
        // Get the background image
        val charEntry = viewHolder.charEntry
        // Gets the character entry input

        val playerEntry = viewHolder.playerEntry
        // Gets the player entry box

        updateView(playerHandler,background,playerEntry, charEntry)

        charEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
        // When the character entry box loses or gains focus
            if (!hasFocus) {
            // If it has just lost focus
                val charInput = charEntry.text.toString()
                var newChar = charInput
                if ((charInput != "") and !charNames.contains(charInput)) {
                // If the entered value is not valid
                    newChar = findClosest(charInput, charNames)
                    // Find the closest text value
                }
                playerHandler.charName = newChar
                // Update the player to store their new character
                updateView(playerHandler,background,playerEntry,charEntry)
            }
        }

        playerEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
        // When the character entry box loses or gains focus
        if (!hasFocus) {
            // If it has just lost focus
            val input = playerEntry.text.toString()
            if ((input !in playerNames) and (input != "")) {
                // If the entered value is not valid
                createPlayerPopup(playerEntry, playerHandler, input, background, charEntry)
                // Gives the option to add a new player
            }
            else{
                playerHandler.playerName = input
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
        var titleString: String = playerEntry.context.getString(R.string.add_player_1)
        titleString += " $inputText"
        titleString += " " + playerEntry.context.getString(R.string.add_player_2)

        changeName.title = titleString
        // Changes the menu item text

        newPlayerPopup.setOnMenuItemClickListener {
        // When a menu item is clicked - can only be the change name item
            playerHandler.playerName = inputText
            // Update the stored player to the new value
            nameAdded  = true
            true
            // Needed to return a unit? Not sure what this does
        }

        newPlayerPopup.setOnDismissListener {
        // When the new player popup is dismissed
            if (!nameAdded) {
            // If no name was added by this menu
                val newText = findClosest(inputText, playerNames)
                // Find the closest text value
                playerHandler.playerName = newText
                // Update the stored player to the new value
            }
            updateView(playerHandler,background,playerEntry,charEntry)
         }
        newPlayerPopup.show()
        // Show the pop-up menu
    }

    private fun updateView(playerHandler: PlayerHandler, background: ImageView, playerEntry: AutoCompleteTextView, charEntry: AutoCompleteTextView){

        val pos = charNames.indexOf(playerHandler.charName)
        if (pos >= 0) {
            val currentChar = charList[pos]
            val charImage = arrayOf(currentChar.image, currentChar.imageAlt).random()
            // Select a random character image
            playerHandler.charImage = charImage
        }
        background.setImageResource(playerHandler.charImage)
        // Set the image to the stored player image


        val playerAdaptor = ArrayAdapter(playerEntry.context, android.R.layout.simple_spinner_item, playerNames)
        // Creates an array adapter to hold the player name list
        playerEntry.setAdapter(playerAdaptor)
        // Sets the adapter for this list
        playerEntry.setText(playerHandler.playerName)
        // Fill in the player name if this is an update

        val charAdapter = ArrayAdapter(charEntry.context, android.R.layout.simple_spinner_item, charNames)
        // Creates an array adapter to hold the character list
        charEntry.setAdapter(charAdapter)
        // Sets the adapter for this list
        charEntry.setText(playerHandler.charName)
        // Fill in the character name if this is an update
    }

    fun addData(chars: Array<CharEntity>, players: Array<Player>){
        charList = chars
        playerList = players
        charNames = charList.map{charEntity -> charEntity.charName }.toTypedArray()
        // Store the names from the list of characters
        playerNames = playerList.map{player -> player.playerName }.toTypedArray()
    }
}