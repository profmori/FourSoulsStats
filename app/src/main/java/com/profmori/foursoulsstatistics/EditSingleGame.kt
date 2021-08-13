package com.profmori.foursoulsstatistics

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.custom_adapters.DeleteGameEdit
import com.profmori.foursoulsstatistics.custom_adapters.EditGameAdapter
import com.profmori.foursoulsstatistics.custom_adapters.ExitGameEdit
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.PlayerHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.GameInstance
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EditSingleGame : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_single_game)

        val gameID = intent.getStringExtra("gameID")
        // Get the game id

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not)

        val titleText = findViewById<TextView>(R.id.adjustTitle)
        val playerPrompt = findViewById<TextView>(R.id.adjustPlayerPrompt)
        val playerNo = findViewById<EditText>(R.id.adjustPlayerNumber)
        val treasurePrompt = findViewById<TextView>(R.id.adjustTreasurePrompt)
        val treasureNo = findViewById<EditText>(R.id.adjustTreasureNumber)
        val submitButton = findViewById<Button>(R.id.adjustSubmit)
        val deleteButton = findViewById<Button>(R.id.adjustDeleteGame)
        val exitButton = findViewById<Button>(R.id.adjustExitButton)
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

        val playerRecycler = findViewById<RecyclerView>(R.id.adjustCharList)
        // Find the recycler view

        val edition = SettingsHandler.getEditions(this)
        val altArt = SettingsHandler.readSettings(this)["alt_art"].toBoolean()
        val online = SettingsHandler.readSettings(this)["online"].toBoolean()

        var playerAdapter = EditGameAdapter(emptyArray())
        // Create an empty adapter for the results list
        playerRecycler.layoutManager = GridLayoutManager(this, 2)
        // Lay the recycler out as a grid
        playerRecycler.adapter = playerAdapter
        // Attach the adapter to the player recycler


        var playerHandlerList = emptyArray<PlayerHandler>()
        // Creates the empty player handler list

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
            // Create 2 lists for the current and original results
            gameInstances.forEach {
                currResults += it.gameInstances.toTypedArray()
            }
            // Get the list of all instances

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

                exitButton.setOnClickListener {
                    if(checkSame(currResults, playerHandlerList)
                        and (game.treasureNo == treasureNo.text.toString().toInt())){
                    // If the player data and treasure number hasn't changed
                        finish()
                        // Go back to the page before
                    }else{
                        val exitDialog =
                            ExitGameEdit(this@EditSingleGame, fonts["body"]!!)
                        exitDialog.show(supportFragmentManager, "exitGame")
                        // Create and show the confirmation to exit the editing
                    }
                }

                submitButton.setOnClickListener{
                    val players = playerHandlerList.map{player -> player.playerName}
                    val chars = playerHandlerList.map{player -> player.charName}
                    val winners = playerHandlerList.map { player -> player.winner }
                    if ((Collections.frequency(winners,true) == 1)
                        and (!players.contains(""))
                        and (!chars.contains(""))
                            ) {
                        // If there is exactly one winner and all data is entered
                        if (!checkSame(currResults, playerHandlerList)
                            or (game.treasureNo != treasureNo.text.toString().toInt())
                        ) {
                            // If the data has been changed
                            CoroutineScope(Dispatchers.IO).launch {
                                gameDao.clearSingleGame(gameID)
                                gameDao.clearSingleGameInstance(gameID)
                                // Clear the old versions of the data
                                val newGame = Game(
                                    gameID,
                                    playerNo.text.toString().toInt(),
                                    treasureNo.text.toString().toInt(),
                                    game.uploaded
                                )
                                // Create a new game with the new data
                                gameDao.addGame(newGame)
                                // Add the new game
                                if (online) {
                                    OnlineDataHandler.deleteOnlineGameInstances(gameID)
                                }
                                // Delete any online games if online connectivity is enabled
                                playerHandlerList.forEach {
                                    // For all the players in the player list
                                    val newGameInstance = GameInstance(
                                        0,
                                        gameID,
                                        it.playerName,
                                        it.charName,
                                        it.eternal,
                                        it.soulsNum,
                                        it.winner
                                    )
                                    // Create the new game instance
                                    gameDao.addGameInstance(newGameInstance)
                                    // Add the game instance to the database
                                    if (online) {
                                        OnlineDataHandler.saveOnlineGameInstance(
                                            newGame,
                                            newGameInstance
                                        )
                                    }
                                    // If online saving is allowed update the online database as well
                                }
                            }
                        }
                        finish()
                        // Exit the page
                    }else{
                        val errorToast = Toast.makeText(this@EditSingleGame, R.string.adjust_incorrect_data, Toast.LENGTH_LONG)
                        // Create the error message toast
                        errorToast.show()
                        // Show the error toast
                    }
                }

                deleteButton.setOnClickListener {
                    val deleteDialog =
                        DeleteGameEdit(gameID, this@EditSingleGame, fonts["body"]!!)
                    deleteDialog.show(supportFragmentManager, "deleteGame")
                    // Create and show the confirmation to exit the editing
                }
            }
        }
    }

    private fun checkSame(original: Array<GameInstance>, new: Array<PlayerHandler>): Boolean{
        if(original.size == new.size){
        // If the number of players has changed
            val newPlayers = new.map { player -> player.playerName }
            val newChars = new.map { player -> player.charName }
            val newEternals = new.map { player -> player.eternal }
            val newSouls = new.map { player -> player.soulsNum }
            val newWinners = new.map { player -> player.winner }
            // Extract all the relevant information from the data
            original.forEach {
                if(newPlayers.contains(it.playerName)){
                    val currIndex = newPlayers.indexOf(it.playerName)
                    if ((newChars[currIndex] != it.charName)
                        or (newEternals[currIndex] != it.eternal)
                        or (newSouls[currIndex] != it.souls)
                        or (newWinners[currIndex] != it.winner)
                    ){
                    // If something is different
                        return false
                    }
                }else{
                // If the current player isn't in the new game
                    return false
                }
            }
        }else{
        // If the number of players has changed
            return false
        }
        // If none of the if statements have forced a false return, the data hasn't changed
        return true
    }

    override fun onBackPressed() {
        // WHen the back button is pressed
        val exitButton = findViewById<Button>(R.id.adjustExitButton)
        exitButton.performClick()
        // Press the exit button
    }
}