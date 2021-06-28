package com.example.foursoulsstatistics

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView


class CharListAdaptor(private val playerList: List<Player>) : RecyclerView.Adapter<CharListAdaptor.ViewHolder>() {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access

    /*
    private val baseArray = arrayOf<String>("Blue Baby", "Cain", "Eden", "Eve", "The Forgotten",
            "Isaac", "Judas", "Lazarus", "Lilith", "Maggy", "Samson")
    // List of the base game characters
    private val goldArray = arrayOf("Apollyon", "Azazel", "The Keeper", "The Lost")
    // List of the gold box characters
    private val plusArray = arrayOf<String>("Bum-bo", "Dark Judas", "Guppy", "Whore of Babylon")
    // List of the FS+ characters
    private val requiemArray = arrayOf<String>("Bethany", "Jacob & Esau")
    // List of Requiem characters
    private val taintedArray = arrayOf<String>("The Baleful", "The Benighted", "The Broken",
            "The Capricious", "The Curdled", "The Dauntless", "The Deceiver", "The Deserter",
            "The Empty", "The Enigma", "The Fettered", "The Harlot", "The Hoarder", "The Miser",
            "The Savage", "The Soiled", "The Zealot")
    // List of tainted characters
    private val warpArray = arrayOf<String>("Ash", "Blind Johnny", "Blue Archer", "Boyfriend",
            "Captain Viridian", "Guy Spelunky", "Pink Knight", "Psycho Goreman", "Quote",
            "Salad Fingers", "The Knight", "The Silent", "Yung Venuz")
    // List of warp zone characters

    private var charList = baseArray + goldArray + plusArray + requiemArray + taintedArray + warpArray
     */

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

        val player: Player = playerList[position]
        // Set item views based on your player

        val background = viewHolder.charImage
        // Get the background image
        background.setImageResource(player.charImage)
        // Set the image to the stored player

        val charEntry = viewHolder.charEntry
        // Gets the character entry input
        val charList = player.imageMap.keys.toTypedArray()
        // Get the characters from the keys of the image map
        charList.sort()
        // Sort the character list alphabetically
        val charAdapter = ArrayAdapter(charEntry.context, android.R.layout.simple_spinner_item, charList)
        // Creates an array adapter to hold the character list
        charEntry.setAdapter(charAdapter)
        // Sets the adapter for this list
        charEntry.setText(player.charName)
        // Fill in the character name if this is an update


        val playerEntry = viewHolder.playerEntry
        // Gets the player entry box
        var fullPlayerList = player.playerNameList
        // Gets the list of players from the player class and makes it a string array
        fullPlayerList.sort()
        // Sort the player list alphabetically
        var playerAdaptor = ArrayAdapter(playerEntry.context, android.R.layout.simple_spinner_item, fullPlayerList)
        // Creates an array adapter to hold the player name list
        playerEntry.setAdapter(playerAdaptor)
        // Sets the adapter for this list
        playerEntry.setText(player.playerName)
        // Fill in the player name if this is an update

        charEntry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Before text is changed check this
                adaptDropDown(charEntry)
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        charEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
        // When the character entry box loses or gains focus
            if (!hasFocus) {
            // If it has just lost focus
                val charInput = charEntry.text.toString()
                if (!charList.contains(charInput) and (charInput != "")) {
                // If the entered value is not valid
                    val newChar = findClosest(charInput, charList)
                    // Find the closest text value
                    charEntry.setText(newChar)
                    // Set the text to a correct value
                }
                player.updateCharacter(charEntry.text.toString())
                // Update the player to store their new character
                background.setImageResource(player.charImage)
                // Set the background image to the character card
            }
        }

    playerEntry.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Before text is changed check this
            adaptDropDown(playerEntry)
            // Adapts the drop down length
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
        playerEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
        // When the character entry box loses or gains focus
        if (!hasFocus) {
            // If it has just lost focus
            val input = playerEntry.text.toString()
            if ((input !in player.playerNameList) and (input != "")) {
                // If the entered value is not valid
                createPlayerPopup(playerEntry, player, input)
                // Gives the option to add a new player
            }
            player.updatePlayer(playerEntry.text.toString())
            // Update the player to store their new character
            playerEntry.setText(player.playerName)
            // Fill in the player name if this is an update
        }
        else if ((!player.playerNameList.contentEquals(fullPlayerList)) and hasFocus){
        // If the player list has been updated since this was first run, and this is now being focused (text entered)
            fullPlayerList = player.playerNameList
            // Recreate the full player list
            playerAdaptor = ArrayAdapter(playerEntry.context, android.R.layout.simple_spinner_item, fullPlayerList)
            // Creates an array adapter to hold the player name list
            playerEntry.setAdapter(playerAdaptor)
            // Sets the adapter for this list
            playerEntry.setText(player.playerName)
            // Fill in the player name if this is an update

        }
    }
}

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return playerList.size
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

    fun adaptDropDown(view: AutoCompleteTextView){
        if (view.adapter.count < 3) {
            // If the number of entries shown is less than 3
            view.dropDownHeight = view.adapter.count * view.lineHeight
            // Make the dropdown fit the number of lines
        } else {
            view.dropDownHeight = 3 * view.lineHeight
            // If there are 3 or more entries, just shown 3 in the dropdown
        }
    }

    private fun createPlayerPopup(parentView: AutoCompleteTextView, player: Player, inputText : String) {
    // When the player does not match an existing player this is called
        var nameAdded = false
        val newPlayerPopup = PopupMenu(parentView.context,parentView)
        // Creates a new pop-up menu for adding a player
        newPlayerPopup.menuInflater.inflate(R.menu.new_player_menu, newPlayerPopup.menu)
        // Inflates the menu from the provided menu layout
        val changeName = newPlayerPopup.menu.findItem(R.id.addName)
        // Gets the menu item which corresponds to adding the name (currently the only one)
        var titleString: String = parentView.context.getString(R.string.add_player_1)
        println(titleString)
        titleString += " " + inputText + " " + parentView.context.getString(R.string.add_player_2)
        println(titleString)

        changeName.title = titleString
        // Changes the menu item text

        newPlayerPopup.setOnMenuItemClickListener {
        // When a menu item is clicked - can only be the change name item
            for (p in playerList) {
            // Goes through every player
                p.addPlayer(inputText)
                // Adds the new player to this player's list of possible player names
            }
            player.updatePlayer(inputText)
            // Updates the current player to the new player
            nameAdded  = true
            true
            // Needed to return a unit? Not sure what this does
        }

        newPlayerPopup.setOnDismissListener {
        // When the new player popup is dismissed
            if (!nameAdded) {
            // If no name was added by this menu
                val newPlayer = findClosest(inputText, player.playerNameList)
                // Find the closest text value
                parentView.setText(newPlayer)
                // Set the text to a "correct" value
                player.updatePlayer(newPlayer)
                // Update the stored player to the new value
            }
         }
        newPlayerPopup.show()
        // Show the pop-up menu
    }

}