package com.profmori.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.custom_adapters.GamesListAdapter
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.launch

class EditGames : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_games)

        val editTitle = findViewById<TextView>(R.id.editGamesTitle)
        // Gets the title text

        val editPrompt = findViewById<TextView>(R.id.editSelectGame)
        // Get the text prompt

        val returnButton = findViewById<Button>(R.id.editBackButton)
        // Gets the back button

        val gameList = findViewById<RecyclerView>(R.id.editGameList)
        // Gets the handler for the list of played games

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        val background = findViewById<ImageView>(R.id.background)
        // Get the background image view
        SettingsHandler.updateBackground(this, background)
        // Update the background

        val fonts = TextHandler.setFont(this)
        // Get the user selected fonts

        editTitle.typeface = fonts["title"]
        editPrompt.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        // Set all button and title fonts

        var listAdapter = GamesListAdapter(emptyArray(), buttonBG, fonts["body"]!!)
        // Create the list adapter
        gameList.adapter = listAdapter
        // Attach the adapter to the game list
        gameList.layoutManager = LinearLayoutManager(this@EditGames)
        // Lay it out as a list

        returnButton.setOnClickListener {
            // When the return button is clicked
            val backToMain = Intent(this, MainActivity::class.java)
            backToMain.putExtra("from", "edit_data")
            startActivity(backToMain)
            // Go back to the main page activity
        }

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        lifecycleScope.launch {
            // As a coroutine
            if (SettingsHandler.readSettings(this@EditGames)["online"].toBoolean()) {
                OnlineDataHandler.getGroupGames(this@EditGames)
                // Get any new online saved games
            }
            val games = gameDao.getGames()
            // Get all games
            if (games.isNotEmpty()) {
                val idList = games.map { game -> game.gameID }.toTypedArray()
                // Create a list of all game ids
                idList.sort()
                // Sort the game ids so they are in chronological order
                listAdapter = GamesListAdapter(idList, buttonBG, fonts["body"]!!)
                // Create the list adapter
                gameList.adapter = listAdapter
                // Attach the adapter to the game list
                gameList.layoutManager = LinearLayoutManager(this@EditGames)
                // Lay it out as a list
            } else {
                val passToast =
                    Toast.makeText(this@EditGames, R.string.edit_no_games, Toast.LENGTH_LONG)
                // Create the error message toast
                passToast.show()
                // Show the error toast
                returnButton.performClick()
                // Press the back button
            }
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // When the back arrow is pressed
                returnButton.performClick()
                // Do everything that would happen from pressing the actual button
            }
        })
    }
}