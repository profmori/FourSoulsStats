package com.profmori.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.custom_adapters.ResultsListAdaptor
import com.profmori.foursoulsstatistics.data_handlers.PlayerHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.GameInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.stream.IntStream.range

class EnterResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_result)
        // Set the layout

        val playerNames = intent.getStringArrayExtra("names") as Array<String>
        val charNames = intent.getStringArrayExtra("chars") as Array<String>
        val charImages = intent.getIntArrayExtra("images") as IntArray
        val eternals = intent.getStringArrayExtra("eternals") as Array<String?>
        // Get all the data from the data entry page

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not)

        var playerList = emptyArray<PlayerHandler>()
        // Cretes the empty player handler list

        for (i in range(0,playerNames.size)){
        // Iterates through all players
            playerList += PlayerHandler(playerNames[i],charNames[i],charImages[i],eternals[i],0,false)
            // Adds the player
            playerList.last().fonts = fonts
            // Updates the stored font
        }

        val treasureCount = intent.getStringExtra("treasures") as String
        // Pull the count of treasures from the intent pass as a string

        val timeCode = System.currentTimeMillis()
        // Get the current time for a unique game identifier
        val groupID = SettingsHandler.readSettings(this)["groupID"]
        // Get the unique group identifier
        val gameId = groupID + timeCode.toString()
        // Makes the game id out of the timecode and the unique identifier

        val playerRecycler = findViewById<RecyclerView>(R.id.winPlayerList)
        // Find the recycler view

        val playerAdapter = ResultsListAdaptor(playerList)
        // Create the adapter for the results list

        playerRecycler.adapter = playerAdapter
        // Attach the adapter to the player recycler

        playerRecycler.layoutManager = GridLayoutManager(this, 2)
        // Lay the recycler out as a grid

        val confirmResult: Button = findViewById(R.id.enterResultsButton)
        // Finds the button to confirm the results

        val returnButton = findViewById<Button>(R.id.resultsToEntry)
        // Gets the button to return to the data entry phase

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background view
        SettingsHandler.updateBackground(this, background)
        // Update the background


        if (confirmResult.typeface != fonts["body"]){
            // If the fonts are wrong
            confirmResult.typeface = fonts["body"]
            returnButton.typeface = fonts["body"]
            // Update them
        }

        confirmResult.setOnClickListener {
            // When the button is pressed
            var count = 0
            // Zero the winner count
            for (p in playerList){
            // Iterate through all players
                if(p.winner){
                // If someone won
                    count += 1
                    // Add 1 to the winner count
                }
            }
            if (count == 1){
            // If there is exactly 1 winner
                saveData(gameId, playerList, treasureCount)
                // Save the game
                val backToMain = Intent(this, MainActivity::class.java)
                // Create an intent back to the main screen
                backToMain.putExtra("from", "enter_result")
                startActivity(backToMain)
                // Go back to the main screen
                val passToast = Toast.makeText(this, R.string.result_pass_on, Toast.LENGTH_LONG)
                // Create the error message toast
                passToast.show()
                // Show the error toast
            }
            else{
            // If you cannot move on
                val errorToast = Toast.makeText(this, R.string.result_wrong_count, Toast.LENGTH_LONG)
                // Create the error message toast
                errorToast.show()
                // Show the error toast
            }
        }

        returnButton.setOnClickListener {
        // If the back button is clicked
            finish()
            // Just go back
        }

    }

    private fun saveData(gameId: String, playerList: Array<PlayerHandler>, treasureCount: String) {
    // Function to save the game
        val gameDatabase: GameDataBase = GameDataBase.getDataBase(this)
        // Gets the game database
        val gameDao = gameDatabase.gameDAO
        // Gets the database access object

        val game = Game(gameId, playerList.size, treasureCount.toInt(), false)
        // Creates a game variable to store this game

        CoroutineScope(Dispatchers.IO).launch {
        // Runs a coroutine which will block upload of data
            gameDao.addGame(game)
            // Adds the game to the database
            for (p in playerList){
                // Iterates through the player data list
                val newGameInstance = GameInstance(0,gameId,p.playerName, p.charName, p.eternal, p.soulsNum, p.winner)
                // Creates a new game instance to record each player
                gameDao.addGameInstance(newGameInstance)
                // Adds the new game instance
            }
        }
    }
}