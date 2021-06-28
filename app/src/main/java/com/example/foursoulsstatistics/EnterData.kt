package com.example.foursoulsstatistics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EnterData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_data)

        val charList = findViewById<RecyclerView>(R.id.playerList)
        // Find the recycler view

        val playerNo = findViewById<EditText>(R.id.playerNumber)
        // Get the player number input box

        val treasureNo = findViewById<EditText>(R.id.treasureNumber)
        // Get the treasure number input box

        var gameTreasures = treasureNo.text.toString()

        var playerCount = playerNo.text.toString().toInt()
        // Get the number of players in the game from the player number

        val continueButton = findViewById<Button>(R.id.inputContinueButton)
        // Gets the button to continue

        var playerList = Player.makePlayerList(playerCount)
        // Create adapter passing in the number of players
        var adapter = CharListAdaptor(playerList)
        // Attach the adapter to the recyclerview to populate items
        charList.adapter = adapter
        // Set layout manager to position the items
        charList.layoutManager = GridLayoutManager(this, 2)
        // Lay the recycler out as a grid


        playerNo.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // After the text in the player number field is changed
                val oldPlayerCount = playerCount
                // Stores the player count for if an error occurs
                try {
                    playerCount = playerNo.text.toString().toInt()
                    // Try and make the value in the text into an integer
                } catch (e: NumberFormatException) {
                    playerCount = oldPlayerCount
                    // If an error is thrown from no text set the number of players back to what it was
                    playerNo.setText(playerCount.toString())
                    // Rewrite the text field to show this
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
                    adapter = CharListAdaptor(playerList)
                    // Creates the recycler view adapter for this
                    charList.adapter = adapter
                    // Creates the recycler view
                }
            }
            else{
            // If the user has just entered the text field
                playerNo.setText("")
                // Clear the input
            }
        }

        treasureNo.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
            // After the number of treasures has been input
                if (treasureNo.text.toString() == "") {
                // If the field is blank
                    treasureNo.setText(gameTreasures)
                    // Set the field to the existing number of treasures
                }
                else{
                    gameTreasures = treasureNo.text.toString()
                    // Otherwise store the new number of treasures
                }
            }
            else{
            // If the user has just entered the text field
                treasureNo.setText("")
                // Clear the text field
            }
        }

        continueButton.setOnClickListener {
            tryMoveOn(playerList,gameTreasures)
            // Try to move to the next screen
        }
    }

    private fun tryMoveOn(playerList: ArrayList<Player>, gameTreasures: String){
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
            val enterResult = Intent(this, EnterResult::class.java)
            // Create a new intent to go to the result entry page
            enterResult.putExtra("players", playerList)
            // Create an extra parameter which passes the player list into the results page
            enterResult.putExtra("treasures",gameTreasures)
            // Creates an extra parameter which passes the number of treasures to the results page
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