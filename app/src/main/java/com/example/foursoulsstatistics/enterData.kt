package com.example.foursoulsstatistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class enterData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_data)

        val charList = findViewById<RecyclerView>(R.id.playerList)
        // Find the recycler view
        val playerNo = findViewById<EditText>(R.id.playerNumber)
        // Get the player number input box
        var playerCount = playerNo.text.toString().toInt()
        // Get the number of players in the game from the player number
        var playerList = Player.makePlayerList(playerCount)
        // Create adapter passing in the number of players
        var adapter = charListAdaptor(playerList)
        // Attach the adapter to the recyclerview to populate items
        charList.adapter = adapter
        // Set layout manager to position the items
        charList.layoutManager = GridLayoutManager(this,2)
        // Lay the recycler out as a grid

        playerNo.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // After the text in the player number field is changed
                try {
                    playerCount = playerNo.text.toString().toInt()
                    // Try and make the value in the text into an integer
                }
                catch(e: NumberFormatException){
                    playerCount = 1
                    // If an error is thrown from no text set the number of players to 1
                }
                finally {
                    if(playerCount < 1){
                    // If the user tries to input something invalid
                        playerCount = 1
                        // Set the player count to 1
                        playerNo.setText(playerCount.toString())
                        // Rewrite the text field to show this
                    }
                    playerList = Player.makePlayerList(playerCount)
                    // Makes a player list based on the number of players
                    adapter = charListAdaptor(playerList)
                    // Creates the recycler view adapter for this
                    charList.adapter = adapter
                    // Creates the recycler view
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        }
    }