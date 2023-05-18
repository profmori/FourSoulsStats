package com.profmori.foursoulsstatistics.custom_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.OnFocusChangeListener
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
import com.profmori.foursoulsstatistics.database.ItemList
import kotlin.math.abs


class CharListAdapter(private val playerHandlerList: Array<PlayerHandler>) :
    RecyclerView.Adapter<CharListAdapter.ViewHolder>() {

    private var itemList = emptyArray<String>()
    // Create an empty array to hold the list of all items in the editions selected

    inner class ViewHolder(listItemView: View, parentGroup: ViewGroup) : RecyclerView.ViewHolder(listItemView) {
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

        val parent = parentGroup
        // Stores the parent viewgroup
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

        return ViewHolder(contactView, parent)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get the data model based on position

        val playerHandler: PlayerHandler = playerHandlerList[position]
        // Set item views based on your player handler

        val fonts = playerHandler.fonts
        // Get the fonts the user is using

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
        // Set all the fonts correctly

        eternalEntry.isEnabled = false
        eternalEntry.visibility = INVISIBLE
        eternalPrompt.visibility = INVISIBLE
        // Hide all the eternal item field

        RecyclerHandler.updateView(
            playerHandler,
            background,
            playerPrompt,
            playerEntry,
            charPrompt,
            charEntry,
            eternalPrompt,
            eternalEntry,
            itemList
        )
        // Use the recycler handler class to update the all the correct views to match the list

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
            RecyclerHandler.enterChar(
                hasFocus,
                playerPrompt,
                playerEntry,
                playerHandler,
                background,
                charPrompt,
                charEntry,
                eternalPrompt,
                eternalEntry,
                itemList
            )
            // Run all the logic for the character data
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

            RecyclerHandler.enterPlayer(
                hasFocus,
                playerPrompt,
                playerEntry,
                playerHandler,
                playerHandlerList,
                background,
                charPrompt,
                charEntry,
                eternalPrompt,
                eternalEntry,
                itemList)
            // Run all the logic for entering the player data
            if (playerHandler.solo){
                val otherPosition = abs(position-1)
                val otherPlayer = playerHandlerList[otherPosition]

                if (otherPlayer.playerName != playerHandler.playerName) {
                    otherPlayer.playerName = playerEntry.text.toString().trim().lowercase()
                    notifyItemChanged(otherPosition)
                }
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
            RecyclerHandler.enterEternal(
                hasFocus,
                playerPrompt,
                playerEntry,
                playerHandler,
                background,
                charPrompt,
                charEntry,
                eternalPrompt,
                eternalEntry,
                itemList
            )
            // Run all the logic for the eternal data
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return playerHandlerList.size
        // Returns the player list size element
    }
}