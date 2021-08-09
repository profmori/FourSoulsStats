package com.profmori.foursoulsstatistics

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.custom_adapters.EditGameAdapter
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.PlayerHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.GameInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditSingleGame : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_single_game)

        val gameID = intent.getStringExtra("gameID")
        // Get the game id

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not)

        val titleText = findViewById<TextView>(R.id.inputTitle)
        val playerPrompt = findViewById<TextView>(R.id.inputPlayerPrompt)
        val playerNo = findViewById<EditText>(R.id.adjustPlayerNumber)
        val treasurePrompt = findViewById<TextView>(R.id.inputTreasurePrompt)
        val treasureNo = findViewById<EditText>(R.id.inputTreasureNumber)
        val submitButton = findViewById<Button>(R.id.inputToMain)
        val deleteButton = findViewById<Button>(R.id.inputGameResults)
        // Get all the text based elements

        titleText.typeface = fonts["title"]
        playerPrompt.typeface = fonts["body"]
        playerNo.typeface = fonts["body"]
        treasurePrompt.typeface = fonts["body"]
        treasureNo.typeface = fonts["body"]
        submitButton.typeface = fonts["body"]
        deleteButton.typeface = fonts["body"]
        // Set all the correct fonts

        val buttonBG = ImageHandler.setButtonImage()
        // Get the random background
        submitButton.setBackgroundResource(buttonBG)
        deleteButton.setBackgroundResource(buttonBG)
        // Set both button backgrounds

        val background = findViewById<ImageView>(R.id.background)
        // Get the background image
        SettingsHandler.updateBackground(this, background)
        // Set it to the right background

        val gameDatabase: GameDataBase = GameDataBase.getDataBase(this)
        // Gets the game database
        val gameDao = gameDatabase.gameDAO
        // Gets the database access object

        val playerRecycler = findViewById<RecyclerView>(R.id.inputCharList)
        // Find the recycler view

        val edition = SettingsHandler.getEditions(this)
        val altArt = SettingsHandler.readSettings(this)["alt_art"].toBoolean()

        var playerAdapter = EditGameAdapter(emptyArray())
        // Create an empty adapter for the results list
        playerRecycler.layoutManager = GridLayoutManager(this, 2)
        // Lay the recycler out as a grid
        playerRecycler.adapter = playerAdapter
        // Attach the adapter to the player recycler

        CoroutineScope(Dispatchers.IO).launch{
            val game = gameDao.getGame(gameID!!)
            // Get the game data

            var gameTreasures = game.treasureNo
            // Get the number of treasures

            var playerCount = game.playerNo
            // Get the number of players in the game from the player number

            playerNo.setText(playerCount.toString())
            treasureNo.setText(gameTreasures.toString())
            // Set the player and treasure numbers

            var characterList = emptyArray<CharEntity>()
            edition.forEach{characterList += gameDao.getCharacterList(it)}
            // For each edition you want characters for get all characters

            val gameInstances = gameDao.getGameWithInstance(gameID)
            // Get the game instance
            var currResults = emptyArray<GameInstance>()
            gameInstances.forEach {
                currResults += it.gameInstances.toTypedArray()
            }
            // Get the list of all instances

            var playerHandlerList = emptyArray<PlayerHandler>()
            // Creates the empty player handler list
            val playerList = gameDao.getPlayers()
            // Get the full list of players

            currResults.forEach{gameInstance ->
                // Iterates through all game instances
                playerHandlerList += PlayerHandler(
                    gameInstance.playerName,
                    gameInstance.charName,
                    0,
                    gameInstance.eternal,
                    gameInstance.souls,
                    gameInstance.winner
                )
                // Adds the player handler
            }

            playerHandlerList.forEach {
                it.addData(characterList, playerList)
                // Add the player and character list to all player handlers
                it.useAlts = altArt
                // Set the player handler flag for using alt art correctly
                it.fonts = fonts
                // Updates the stored font
                val charName = it.charName
                it.updateCharacter("")
                it.updateCharacter(charName)
                // Updates the image
            }

            withContext(Dispatchers.Main){
                playerAdapter = EditGameAdapter(playerHandlerList)
                // Create the adapter for the results list

                playerRecycler.adapter = playerAdapter
                // Attach the adapter to the player recycler


                playerRecycler.adapter = playerAdapter
                // Attach the adapter to the player recycler

                playerNo.setOnEditorActionListener { view, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // if the soft input is done
                        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        // Get an input method manager
                        imm.hideSoftInputFromWindow(view.windowToken,0)
                        // Hide the keyboard
                        playerNo.clearFocus()
                        // Clear the focus of the edit text
                        return@setOnEditorActionListener true
                    }
                    false
                }

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
                            if (playerCount < 2) {
                                // If the user tries to input something invalid
                                playerCount = 2
                                // Set the player count to 1
                                playerNo.setText(playerCount.toString())
                                // Rewrite the text field to show this
                            }
                            playerHandlerList = PlayerHandler.updatePlayerList(playerHandlerList, playerCount)
                            // Makes a player list based on the number of players
                            playerAdapter = EditGameAdapter(playerHandlerList)
                            // Create the adapter for the results list

                            playerRecycler.adapter = playerAdapter
                            // Attach the adapter to the player recycler
                        }
                    }
                    else{
                        // If the user has just entered the text field
                        playerNo.setText("")
                        // Clear the input
                    }
                }

                treasureNo.setOnEditorActionListener { view, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // if the soft input is done
                        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        // Get an input method manager
                        imm.hideSoftInputFromWindow(view.windowToken,0)
                        // Hide the keyboard
                        treasureNo.clearFocus()
                        // Clear the focus of the edit text
                        return@setOnEditorActionListener true
                    }
                    false
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
                            gameTreasures = treasureNo.text.toString().toInt()
                            // Otherwise store the new number of treasures
                        }
                    }
                    else{
                        // If the user has just entered the text field
                        treasureNo.setText("")
                        // Clear the text field
                    }
                }
            }

        }

    }
}