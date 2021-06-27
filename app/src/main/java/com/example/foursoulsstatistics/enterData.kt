package com.example.foursoulsstatistics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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

        val continueButton = findViewById<Button>(R.id.inputContinueButton)
        // Gets the button to continue

        var playerList = Player.makePlayerList(playerCount)
        // Create adapter passing in the number of players
        var adapter = charListAdaptor(playerList)
        // Attach the adapter to the recyclerview to populate items
        charList.adapter = adapter
        // Set layout manager to position the items
        charList.layoutManager = GridLayoutManager(this, 2)
        // Lay the recycler out as a grid


        playerNo.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // After the text in the player number field is changed
                try {
                    playerCount = playerNo.text.toString().toInt()
                    // Try and make the value in the text into an integer
                } catch (e: NumberFormatException) {
                    playerCount = 0
                    // If an error is thrown from no text set the number of players to 0
                } finally {
                    if (playerCount < 1) {
                        // If the user tries to input something invalid
                        playerCount = 1
                        // Set the player count to 1
                        playerNo.setText(playerCount.toString())
                        // Rewrite the text field to show this
                    }
                    playerList = Player.updatePlayerList(playerList, playerCount)
                    // Makes a player list based on the number of players
                    adapter = charListAdaptor(playerList)
                    // Creates the recycler view adapter for this
                    charList.adapter = adapter
                    // Creates the recycler view
                }
            }
        }

        continueButton.setOnClickListener {
            tryMoveOn(playerList)
            // Try to move to the next screen
        }
    }

    fun tryMoveOn(playerList: kotlin.collections.ArrayList<Player>){
        var moveOn = true
        // Say you can move on
        for (p in playerList) {
        // Iterate through all players
            if ((p.playerName == "") or (p.charName == "")) {
            // If something is not entered
                moveOn = false
                // You can no longer move on
                break
                // No need to check the rest
            }
        }

        if (moveOn) {
        // If you can move on
            val enterResult = Intent(this, enterResult::class.java)
            // Create a new intent to go to the result entry page
            enterResult.putExtra("players", playerList)
            // Create an extra parameter which passes the player list into the results page
            startActivity(enterResult)
            // Move to the result entry page
        } else {
        // If you cannot move on
            val errorToast = Toast.makeText(this, R.string.input_too_few, Toast.LENGTH_LONG)
            // Create the error message toast
            errorToast.show()
            // Show the error toast
        }
    }
}