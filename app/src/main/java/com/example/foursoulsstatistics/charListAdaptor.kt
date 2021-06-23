package com.example.foursoulsstatistics

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class charListAdaptor(private val playerList: List<Player>) : RecyclerView.Adapter<charListAdaptor.ViewHolder>() {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access

    /*
    private val baseArray = arrayOf<String>("Blue Baby", "Cain", "Eden", "Eve", "The Forgotten",
            "Isaac", "Judas", "Lazarus", "Lilith", "Maggy", "Samson")
    // List of the base game characters
    private val goldArray = arrayOf("Apollyon", "Azazel", "The Keeper", "The Lost")
    // List of the gold box characters
    private val plusArray = arrayOf<String>("Bumbo", "Dark Judas", "Guppy", "Whore of Babylon")
    // List of the FS+ characters
    private val requiemArray = arrayOf<String>("Bethany", "Jacob & Esau")
    // List of Reqium characters
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
        val charImage = itemView.findViewById<ImageView>(R.id.charImage)
        // Allows the background image to be set in code
        val charEntry = itemView.findViewById<AutoCompleteTextView>(R.id.charTextEntry)
        // Access the character selection entry in code
        val playerEntry = itemView.findViewById<AutoCompleteTextView>(R.id.playerTextEntry)
        // Access the player selection entry in code
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): charListAdaptor.ViewHolder {
        val context = parent.context
        // Gets the context of the view
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.char_select_layout, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: charListAdaptor.ViewHolder, position: Int) {
        // Get the data model based on position

        val player: Player = playerList.get(position)
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
        val fullPlayerList = player.playerNameList
        // Gets the list of players from the player class and makes it a string array
        fullPlayerList.sort()
        // Sort the player list alphabetically
        val playerAdaptor = ArrayAdapter(playerEntry.context, android.R.layout.simple_spinner_item, fullPlayerList)
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
                println(charEntry.text.toString())
                println(charList.contains(charEntry.text.toString()))
                if (!charList.contains(charEntry.text.toString()) and (charEntry.text.toString() != "")) {
                // If the entered value is not valid
                    val newChar = findClosest(charEntry.text, charList)
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
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
        playerEntry.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
        // When the character entry box loses or gains focus
        if (!hasFocus) {
            // If it has just lost focus
            if ((playerEntry.text.toString() !in fullPlayerList) and (playerEntry.text.toString() != "")) {
                // If the entered value is not valid
                println(fullPlayerList)
                val newPlayer = findClosest(playerEntry.text,fullPlayerList)
                // Find the closest text value
                playerEntry.setText(newPlayer)
                // Set the text to a correct value
            }
            player.updatePlayer(playerEntry.text.toString())
            // Update the player to store their new character
        }
    }
}

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return playerList.size
        // Returns the player list size element
    }

    fun findClosest(wrongText: CharSequence, correctList: Array<String>):String {
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
            // If there are 3 or more entries, jsut shown 3 in the dropdown
        }
    }
}