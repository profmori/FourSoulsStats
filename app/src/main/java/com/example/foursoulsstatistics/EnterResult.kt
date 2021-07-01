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

class EnterResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_result)

        val playerList = intent.getParcelableArrayListExtra<PlayerHandler>("players") as ArrayList<PlayerHandler>
        // Pull the player list from the extra pass in the intent - casting as an array list rather than a java array list

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

    private fun saveData(gameId: Long, playerList: ArrayList<PlayerHandler>, treasureCount: String) {
        val gameDatabase: GameDataBase = GameDataBase.getDataBase(this)
        val gameDAO = gameDatabase.gameDAO
        var instanceArray = emptyArray<GameInstance>()

        for (p in playerList){
            val newGameInstance = GameInstance(0,gameId,p.playerName, p.charName, p.soulsNum, p.winner)
            instanceArray += arrayOf(newGameInstance)
        }

        val game = Game(gameId, playerList.size, treasureCount.toInt())
        lifecycleScope.launch {
            gameDAO.addGame(game)
            instanceArray.forEach { gameDAO.addGameInstance(it)}
        }
    }
}