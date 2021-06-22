package com.example.foursoulsstatistics

import android.content.res.Resources
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

    private val baseArray = arrayOf<String>("Blue Baby", "Cain", "Eden", "Eve", "The Forgotten",
            "Isaac", "Judas", "Lazarus", "Lilith", "Maggy", "Samson")
    // List of the base game characters
    private val goldArray = arrayOf("Apollyon", "Azazel", "Keeper", "The Lost")
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

    private var charList = baseArray + goldArray + plusArray + taintedArray + warpArray

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val messageButton = itemView.findViewById<Button>(R.id.message_button)
        val charImage = itemView.findViewById<ImageView>(R.id.charImage)
        // Allows the background image to be set in code
        val charEntry = itemView.findViewById<AutoCompleteTextView>(R.id.charTextEntry)
        // Access the character selection entry in code
        //val charSpinner = itemView.findViewById<Spinner>(R.id.charSpinner)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): charListAdaptor.ViewHolder {
        val context = parent.context
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

        val entry = viewHolder.charEntry
        // Gets the character entry input
        charList.sort()
        // Sort the character list alphabetically
        val adapter = ArrayAdapter(entry.context, android.R.layout.simple_spinner_item, charList)
        // Creates an array adapter to hold the character list
        entry.setAdapter(adapter)
        // Sets the adapter for this list
        entry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (entry.adapter.count < 3) {
                    // If the number of entries shown does not fill the
                    entry.dropDownHeight = entry.adapter.count * entry.lineHeight
                } else {
                    entry.dropDownHeight = 3 * entry.lineHeight
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        entry.setOnFocusChangeListener(OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (entry.text.toString() !in charList) {
                    val newText = findClosest(entry.text)
                    entry.setText(newText)
                }
                player.updateCharacter(entry.text.toString())
                println(player.charName)
                background.setImageResource(R.drawable.eden)

            }
        })
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return playerList.size
    }

    fun findClosest(wrongText: CharSequence):String {
        val spaceWrong = " $wrongText"
        // Creates a version of the wrong text preceded by a space
        val wrongLen = wrongText.length
        for (element in charList){
            if(element.substring(0, wrongLen).contains(wrongText, ignoreCase = true) or element.contains(spaceWrong, ignoreCase = true)){
                return element
            }
        }
        return ""
    }
}