package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foursoulsstatistics.database.Game
import com.example.foursoulsstatistics.database.GameDataBase
import com.example.foursoulsstatistics.database.GameInstance
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

        var playerList = emptyArray<PlayerHandler>()

        for (i in range(0,playerNames.size)){
            playerList += PlayerHandler(playerNames[i],charNames[i],charImages[i],0,false)
        }

        val treasureCount = intent.getStringExtra("treasures") as String
        // Pull the count of treasures from the intent pass as a string

        val gameId = System.currentTimeMillis()
        // Get the current time for a unique game identifier

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

    }

    private fun saveData(gameId: Long, playerList: Array<PlayerHandler>, treasureCount: String) {
    // Function to save the game
        val gameDatabase: GameDataBase = GameDataBase.getDataBase(this)
        // Gets the game database
        val gameDao = gameDatabase.gameDAO
        // Gets the database access object
        var instanceArray = emptyArray<GameInstance>()
        // Creates an empty array of game instances

        for (p in playerList){
        // Iterates through the player data list
            val newGameInstance = GameInstance(0,gameId,p.playerName, p.charName, p.soulsNum, p.winner)
            // Creates a new game instance to record each player
            instanceArray += arrayOf(newGameInstance)
            // Adds it to the instance array
        }

        val game = Game(gameId, playerList.size, treasureCount.toInt())
        // Creates a game variable to store this game
        lifecycleScope.launch {
        // Runs a coroutine
            gameDao.addGame(game)
            // Adds the game to the database
            instanceArray.forEach { gameDao.addGameInstance(it)}
            // Adds every instance to the database
            val absList = gameDao.getPlayerWithInstance("abs")
            absList.forEach { println(it) }
        }
    }
}