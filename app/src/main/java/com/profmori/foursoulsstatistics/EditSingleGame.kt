package com.profmori.foursoulsstatistics

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.profmori.foursoulsstatistics.custom_adapters.EditGameAdapter
import com.profmori.foursoulsstatistics.custom_adapters.dialogs.DeleteGameEdit
import com.profmori.foursoulsstatistics.custom_adapters.dialogs.ExitGameEdit
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
import java.util.Collections

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
        val coOpBox = findViewById<CheckBox>(R.id.adjustCoOpBox)
        val coOpPrompt = findViewById<TextView>(R.id.adjustCoOpPrompt)
        val turnPrompt = findViewById<TextView>(R.id.adjustTurnsPrompt)
        val turnNo = findViewById<EditText>(R.id.adjustTurnsNumber)
        // Get all the text based elements

        titleText.typeface = fonts["title"]
        playerPrompt.typeface = fonts["body"]
        playerNo.typeface = fonts["body"]
        treasurePrompt.typeface = fonts["body"]
        treasureNo.typeface = fonts["body"]
        submitButton.typeface = fonts["body"]
        deleteButton.typeface = fonts["body"]
        coOpPrompt.typeface = fonts["body"]
        turnPrompt.typeface = fonts["body"]
        turnNo.typeface = fonts["body"]
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

        val edition = SettingsHandler.getSets()
        val online = SettingsHandler.readSettings(this)["online"].toBoolean()
        // Get all the settings currently in use by the play group

        var playerAdapter = EditGameAdapter(emptyArray(), false)
        // Create an empty adapter for the results list
        playerRecycler.layoutManager = GridLayoutManager(this, 2)
        // Lay the recycler out as a grid
        playerRecycler.adapter = playerAdapter
        // Attach the adapter to the player recycler

        var playerHandlerList = emptyArray<PlayerHandler>()
        // Creates the empty player handler list

        CoroutineScope(Dispatchers.IO).launch {
            val game = gameDao.getGame(gameID!!)
            // Get the game data

            var gameTreasures = game.treasureNo
            // Get the number of treasures

            var playerCount = game.playerNo
            // Get the number of players in the game from the player number

            var coOpGame = game.coop

            var turnsLeft = game.turnsLeft

            coOpBox.isChecked = coOpGame

            if (playerCount == 2) {
                coOpBox.visibility = View.VISIBLE
                coOpPrompt.visibility = View.VISIBLE
            } else {
                coOpBox.visibility = View.GONE
                coOpPrompt.visibility = View.GONE
            }

            if (coOpGame) {
                turnPrompt.visibility = View.VISIBLE
                turnNo.visibility = View.VISIBLE
            } else {
                turnPrompt.visibility = View.GONE
                turnNo.visibility = View.GONE
            }

            playerNo.setText(playerCount.toString())
            treasureNo.setText(gameTreasures.toString())
            turnNo.setText(turnsLeft.toString())
            // Set the input numbers

            var characterList = emptyArray<CharEntity>()
            edition.forEach { characterList += gameDao.getCharacterList(it) }
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

            currResults.forEach { gameInstance ->
                // Iterates through all game instances
                playerHandlerList += PlayerHandler(
                    gameInstance.playerName,
                    gameInstance.charName,
                    0,
                    gameInstance.eternal,
                    gameInstance.souls,
                    gameInstance.winner,
                    gameInstance.solo
                )
                // Adds the player handler
            }

            playerHandlerList.forEach {
                it.addData(characterList, playerList, this@EditSingleGame)
                // Add the player and character list to all player handler
                it.fonts = fonts
                // Updates the stored font
                val charName = it.charName
                it.updateCharacter("")
                it.updateCharacter(charName)
                // Updates the image
            }

            withContext(Dispatchers.Main) {
                playerAdapter = EditGameAdapter(playerHandlerList, coOpGame)
                // Create the adapter for the results list

                playerRecycler.adapter = playerAdapter
                // Attach the adapter to the player recycler


                playerRecycler.adapter = playerAdapter
                // Attach the adapter to the player recycler

                playerNo.setOnEditorActionListener { view, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // if the soft input is done
                        val imm =
                            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        // Get an input method manager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
                            if (playerCount < 1) {
                                // If the user tries to input something invalid
                                playerCount = 1
                                // Set the player count to 1
                            }
                            playerNo.setText(playerCount.toString())
                            // Rewrite the text field to show this

                            if (playerCount < 1) {
                                // If the user tries to input something invalid
                                playerCount = 1
                                // Set the player count to 1
                            }
                            playerNo.setText(playerCount.toString())
                            // Rewrite the text field to show this

                            if ((playerCount == 2) && (oldPlayerCount > 2)) {
                                // If they have just changed to 2 player game
                                gameTreasures = 2
                                // Set the number of treasures to 2
                            } else if ((oldPlayerCount == 2) && (playerCount != 2)) {
                                // If they have just changed from 2 players
                                gameTreasures = 0
                                // Set the number of treasures to 0
                            }

                            if (playerCount == 2) {
                                coOpBox.visibility = View.VISIBLE
                                coOpPrompt.visibility = View.VISIBLE
                            } else {
                                coOpBox.visibility = View.GONE
                                coOpPrompt.visibility = View.GONE
                                coOpGame = playerCount == 1
                                coOpBox.isChecked = coOpGame
                            }
                            treasureNo.setText(gameTreasures.toString())
                            // Set the treasure number box to the game treasures

                            playerHandlerList =
                                PlayerHandler.updatePlayerList(
                                    playerHandlerList,
                                    playerCount,
                                    this@EditSingleGame
                                )
                            // Makes a player list based on the number of players
                            playerAdapter = EditGameAdapter(playerHandlerList, coOpGame)
                            // Create the adapter for the results list
                            playerRecycler.adapter = playerAdapter
                            // Attach the adapter to the player recycler
                        }
                    }
                }

                treasureNo.setOnEditorActionListener { view, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // if the soft input is done
                        val imm =
                            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        // Get an input method manager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
                            treasureNo.setText(gameTreasures.toString())
                            // Set the field to the existing number of treasures
                        } else {
                            gameTreasures = treasureNo.text.toString().toInt()
                            // Otherwise store the new number of treasures
                        }
                    } else {
                        // If the user has just entered the text field
                        treasureNo.setText("")
                        // Clear the text field
                    }
                }

                turnNo.setOnEditorActionListener { view, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // if the soft input is done
                        val imm =
                            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        // Get an input method manager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                        // Hide the keyboard
                        turnNo.clearFocus()
                        // Clear the focus of the edit text
                        return@setOnEditorActionListener true
                    }
                    false
                }

                turnNo.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        // After the number of treasures has been input
                        if (turnNo.text.toString() == "") {
                            // If the field is blank
                            turnNo.setText(turnsLeft.toString())
                            // Set the field to the existing number of treasures
                        } else {
                            turnsLeft = turnNo.text.toString().toInt()
                            // Otherwise store the new number of treasures
                        }
                    } else {
                        // If the user has just entered the text field
                        turnNo.setText("")
                        // Clear the text field
                    }
                }

                coOpBox.setOnCheckedChangeListener { _, b ->
                    gameTreasures = if (b) {
                        0
                    } else {
                        2
                    }
                    coOpGame = b
                    treasureNo.setText(gameTreasures.toString())
                    // Set the treasure number box to the game treasures

                    playerHandlerList.forEach {
                        it.winner = false
                    }
                    playerAdapter = EditGameAdapter(playerHandlerList, coOpGame)
                    // Create the adapter for the results list
                    playerRecycler.adapter = playerAdapter
                    // Attach the adapter to the player recycler


                    if (coOpGame) {
                        turnPrompt.visibility = View.VISIBLE
                        turnNo.visibility = View.VISIBLE
                        turnsLeft = 8
                    } else {
                        turnPrompt.visibility = View.GONE
                        turnNo.visibility = View.GONE
                        turnsLeft = -1
                    }
                    turnNo.setText(turnsLeft.toString())
                }

                exitButton.setOnClickListener {
                    if (checkSame(currResults, playerHandlerList)
                        and (game.treasureNo == gameTreasures)
                        and (game.turnsLeft == turnsLeft)
                    ) {
                        // If the player data and treasure number / turn number hasn't changed
                        finish()
                        // Go back to the page before
                    } else {
                        val exitDialog =
                            ExitGameEdit(this@EditSingleGame, fonts["body"]!!)
                        exitDialog.show(supportFragmentManager, "exitGame")
                        // Create and show the confirmation to exit the editing
                    }
                }

                submitButton.setOnClickListener {
                    val players = playerHandlerList.map { player -> player.playerName }
                    // Gets all the player names from the player handler list
                    val chars = playerHandlerList.map { player -> player.charName }
                    // Gets the characters from the player handler list
                    val eternals = playerHandlerList.map { player -> player.eternal }
                    // Gets the list of eternal items
                    val winners = playerHandlerList.map { player -> player.winner }
                    // Gets the winner boolean list from the player handler list

                    var eternalCheck = true
                    // As a default assume the eternals are correct
                    if (chars.indexOf("eden") > -1) {
                        // If there is an eden in the game
                        chars.forEachIndexed { index, char ->
                            // Check every character in the game
                            if ((char == "eden") and (eternals[index].isNullOrBlank())) {
                                // If the character is eden but the eternal is null
                                eternalCheck = false
                                // All eternals are not correct
                            }
                        }
                    }

                    if (((Collections.frequency(winners, true) == 1) or coOpGame)
                        // If there is only one winner
                        and (!players.contains(""))
                        // All players have names
                        and (!chars.contains(""))
                        // All characters have names
                        and (eternalCheck)
                    // All Edens have eternals
                    ) {
                        // If there is exactly one winner and all data is entered
                        if (!checkSame(currResults, playerHandlerList)
                            or (game.treasureNo != gameTreasures)
                            or (game.turnsLeft != turnsLeft)
                        ) {
                            // If the data has been changed or the number of treasures has changed
                            CoroutineScope(Dispatchers.IO).launch {
                                gameDao.clearSingleGame(gameID)
                                // Remove the game from the database
                                gameDao.clearSingleGameInstance(gameID)
                                // Remove all the associated instances from the database
                                if (online) {
                                    OnlineDataHandler.deleteOnlineGameInstances(gameID)
                                }
                                // Delete any online games if online connectivity is enabled

                                val newGame = Game(
                                    gameID,
                                    playerNo.text.toString().toInt(),
                                    treasureNo.text.toString().toInt(),
                                    game.uploaded,
                                    coOpGame,
                                    turnsLeft
                                )
                                // Create a new game with the new data
                                gameDao.addGame(newGame)
                                // Add the new game to the database
                                playerHandlerList.forEachIndexed { index, playerHandler ->
                                    // For all the players in the player list
                                    val newGameInstance = GameInstance(
                                        0,
                                        gameID,
                                        playerHandler.playerName,
                                        playerHandler.charName,
                                        playerHandler.eternal,
                                        playerHandler.soulsNum,
                                        playerHandler.winner,
                                        playerHandler.solo
                                    )
                                    // Create the new game instance
                                    gameDao.addGameInstance(newGameInstance)
                                    // Add the game instance to the database
                                    if (online) {
                                        OnlineDataHandler.saveOnlineGameInstance(
                                            newGame,
                                            newGameInstance,
                                            index
                                        )
                                    }
                                    // If online saving is allowed update the online database as well
                                }
                            }
                        }
                        finish()
                        // Exit the page
                    } else {
                        val errorSnackbar =
                            Snackbar.make(
                                background,
                                R.string.adjust_incorrect_data,
                                Snackbar.LENGTH_LONG
                            )
                        // Create the snackbar
                        errorSnackbar.changeFont(TextHandler.setFont(this@EditSingleGame)["body"]!!)
                        // Set the font of the snackbar
                        errorSnackbar.show()
                        // Show the snackbar
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // When the back arrow is pressed
                exitButton.performClick()
                // Press the exit button
            }
        })
    }

    private fun checkSame(original: Array<GameInstance>, new: Array<PlayerHandler>): Boolean {
        if (original.size == new.size) {
            // If the number of players has not changed
            val newPlayers = new.map { player -> player.playerName }
            val newChars = new.map { player -> player.charName }
            val newEternals = new.map { player -> player.eternal }
            val newSouls = new.map { player -> player.soulsNum }
            val newWinners = new.map { player -> player.winner }
            // Extract all the relevant information from the data
            original.forEach {
                if (newPlayers.contains(it.playerName)) {
                    val currIndex = newPlayers.indexOf(it.playerName)
                    if ((newChars[currIndex] != it.charName)
                        or (newEternals[currIndex] != it.eternal)
                        or (newSouls[currIndex] != it.souls)
                        or (newWinners[currIndex] != it.winner)
                    ) {
                        // If something is different
                        return false
                    }
                } else {
                    // If the current player isn't in the new game
                    return false
                }
            }
        } else {
            // If the number of players has changed
            return false
        }
        // If none of the if statements have forced a false return, the data hasn't changed
        return true

    }
}