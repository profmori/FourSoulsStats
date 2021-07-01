package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foursoulsstatistics.database.CharEntity
import com.example.foursoulsstatistics.database.GameDAO
import com.example.foursoulsstatistics.database.GameDataBase
import com.example.foursoulsstatistics.database.Player
import kotlinx.coroutines.launch

class EnterData : AppCompatActivity() {

    private lateinit var gameDatabase: GameDataBase
    // Initialise the database
    private lateinit var gameDao: GameDAO
    // Initialise the access object

    private var characterList: Array<CharEntity> = emptyArray<CharEntity>()
    // Initialises character names

    private var playerList: Array<Player> = emptyArray<Player>()
    // Initialises player names

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

        var playerHandlerList = PlayerHandler.makePlayerList(playerCount)
        // Create adapter passing in the number of players
        var adapter = CharListAdaptor(playerHandlerList)
        // Attach the adapter to the recyclerview to populate items
        charList.adapter = adapter
        // Set layout manager to position the items
        charList.layoutManager = GridLayoutManager(this, 2)
        // Lay the recycler out as a grid

        gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        gameDao = gameDatabase.gameDAO
        // Get the database access object

        val edition = arrayOf("base", "gold")

        lifecycleScope.launch {
            edition.forEach{characterList += gameDao.getCharacterList(it)}
            playerList = gameDao.getPlayers()
        }

        adapter.addData(characterList, playerList)

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
                    playerHandlerList = PlayerHandler.updatePlayerList(playerHandlerList, playerCount)
                    // Makes a player list based on the number of players
                    adapter = CharListAdaptor(playerHandlerList)
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
            tryMoveOn(playerHandlerList,gameTreasures)
            // Try to move to the next screen
        }
    }

    private fun tryMoveOn(playerHandlerList: ArrayList<PlayerHandler>, gameTreasures: String){
        var moveOn = true
        // Say you can move on
        for (p in playerHandlerList) {
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
            enterResult.putExtra("players", playerHandlerList)
            // Create an extra parameter which passes the player list into the results page
            enterResult.putExtra("treasures",gameTreasures)
            // Creates an extra parameter which passes the number of treasures to the results page
            val handler = playerHandlerList.map{playerHandler -> playerHandler.charName}
            val list = playerList.map{player -> player.playerName }
            for (h in handler) {
                if (!list.contains(h)) {
                    lifecycleScope.launch {
                        val player = Player(h)
                        gameDao.addPlayer(player)
                    }
                }
            }
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