package com.profmori.foursoulsstatistics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.profmori.foursoulsstatistics.custom_adapters.ResultsListAdapter
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.PlayerHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.GameInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EnterResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_result)
        // Set the layout

        val playerNames = intent.getStringArrayExtra("names") as Array<String>
        val charNames = intent.getStringArrayExtra("chars") as Array<String>
        val charImages = intent.getIntArrayExtra("images") as IntArray
        val eternals = intent.getStringArrayExtra("eternals") as Array<String?>
        val souls = intent.getIntArrayExtra("souls") as IntArray
        val coOpGame = intent.getBooleanExtra("coop", false)
        val soloGame = intent.getBooleanExtra("solo", false)
        // Get all the data from the data entry page

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not)

        var playerList = emptyArray<PlayerHandler>()
        // Creates the empty player handler list

        for (i in (playerNames.indices)) {
            // Iterates through all players
            playerList += PlayerHandler(
                playerNames[i],
                charNames[i],
                charImages[i],
                eternals[i],
                souls[i],
                false,
                soloGame
            )
            // Adds the player
            playerList.last().fonts = fonts
            // Updates the stored font
        }

        val treasureCount = intent.getIntExtra("treasures", 0)
        // Pull the count of treasures from the intent pass as a string

        val timeCode = System.currentTimeMillis()
        // Get the current time for a unique game identifier
        var groupID = SettingsHandler.readSettings(this)["groupID"]
        // Get the unique group identifier
        if (groupID != null) {
            // If there is a group id
            if (groupID.contains('O', true)) {
                // If the id contains the letter o
                groupID = groupID.replace('O', '0', true)
                // Replace your letter o with number 0
            }
        }
        // Get rid of any o characters for 0 in legacy group ids
        val gameId = groupID + timeCode.toString()
        // Makes the game id out of the timecode and the unique identifier

        val turnNoPrompt = findViewById<TextView>(R.id.inputTurnsPrompt)
        val turnNoBox = findViewById<EditText>(R.id.inputTurnsNumber)
        var turnCount = -1
        // Get the turn input box

        val playerRecycler = findViewById<RecyclerView>(R.id.winPlayerList)
        // Find the recycler view

        val playerAdapter = ResultsListAdapter(playerList, coOpGame)
        // Create the adapter for the results list

        playerRecycler.adapter = playerAdapter
        // Attach the adapter to the player recycler

        playerRecycler.layoutManager = GridLayoutManager(this, 2)
        // Lay the recycler out as a grid

        val confirmResult = findViewById<Button>(R.id.enterResultsButton)
        // Finds the button to confirm the results

        val returnButton = findViewById<Button>(R.id.resultsToEntry)
        // Gets the button to return to the data entry phase

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        confirmResult.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background view
        SettingsHandler.updateBackground(this, background)
        // Update the background

        turnNoPrompt.typeface = fonts["title"]
        turnNoBox.typeface = fonts["title"]
        confirmResult.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        // Update the fonts

        if (coOpGame) {
            turnNoPrompt.visibility = View.VISIBLE
            turnNoBox.visibility = View.VISIBLE
            turnCount = 8
        } else {
            turnNoPrompt.visibility = View.INVISIBLE
            turnNoBox.visibility = View.INVISIBLE
        }
        turnNoBox.setText(turnCount.toString())

        turnNoBox.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // if the soft input is done
                val imm =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // Get an input method manager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                // Hide the keyboard
                turnNoBox.clearFocus()
                // Clear the focus of the edit text
                return@setOnEditorActionListener true
            }
            false
        }

        turnNoBox.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // After the text in the player number field is changed
                val oldTurnCount = turnCount
                // Stores the player count for if an error occurs
                try {
                    turnCount = turnNoBox.text.toString().toInt()
                    // Try and make the value in the text into an integer
                } catch (e: NumberFormatException) {
                    turnCount = oldTurnCount
                    // If an error is thrown from no text set the number of players back to what it was
                    turnNoBox.setText(turnCount.toString())
                    // Rewrite the text field to show this
                } finally {
                    if (turnCount < 0) {
                        // If the user tries to input something invalid
                        turnCount = 0
                        // Set the turn count to 0
                        turnNoBox.setText("0")
                        // Rewrite the text field to show this
                    }

                    playerList.forEachIndexed { index, playerHandler ->
                        playerHandler.winner = turnCount > 0
                        playerAdapter.notifyItemChanged(index)
                    }

                }
            } else {
                // If the user has just entered the text field
                turnNoBox.setText("")
                // Clear the input
            }
        }

        confirmResult.setOnClickListener {
            // When the button is pressed
            var count = 0
            // Zero the winner count
            for (p in playerList) {
                // Iterate through all players
                if (p.winner) {
                    // If someone won
                    count += 1
                    // Add 1 to the winner count
                }
            }
            if ((count == 1) or coOpGame) {
                // If there is exactly 1 winner or it's a co-op game
                saveData(gameId, playerList, treasureCount, coOpGame, turnCount)
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
            } else {
                // If you cannot move on
                val errorSnackbar =
                    Snackbar.make(background, R.string.result_wrong_count, Snackbar.LENGTH_LONG)
                // Create the snackbar
                errorSnackbar.changeFont(TextHandler.setFont(this)["body"]!!)
                // Set the font of the snackbar
                errorSnackbar.show()
                // Show the snackbar
            }
        }

        returnButton.setOnClickListener {
            // If the back button is clicked
            playerList.forEachIndexed { i, playerHandler ->
                souls[i] = playerHandler.soulsNum
            }

            val enterData = Intent(this, EnterData::class.java)
            enterData.putExtra("names", playerNames)
            enterData.putExtra("chars", charNames)
            enterData.putExtra("images", charImages)
            enterData.putExtra("treasures", treasureCount)
            enterData.putExtra("eternals", eternals)
            enterData.putExtra("souls", souls)
            enterData.putExtra("coop", coOpGame)
            enterData.putExtra("solo", soloGame)
            // Creates an extra parameter which passes data back to the data entry page
            startActivity(enterData)
            // Start the data entry page with the new parameters
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // When the back arrow is pressed
                returnButton.performClick()
                // Clicks the return button
            }
        })
    }

    private fun saveData(
        gameId: String,
        playerList: Array<PlayerHandler>,
        treasureCount: Int,
        coOpGame: Boolean,
        turnsLeft: Int
    ) {
        // Function to save the game
        val gameDatabase: GameDataBase = GameDataBase.getDataBase(this)
        // Gets the game database
        val gameDao = gameDatabase.gameDAO
        // Gets the database access object
        val playerNum = if (playerList[0].solo) {
            1
        } else {
            playerList.size
        }
        val game = Game(gameId, playerNum, treasureCount, false, coOpGame, turnsLeft)
        // Creates a game variable to store this game

        CoroutineScope(Dispatchers.IO).launch {
            // Runs a coroutine which will block upload of data
            gameDao.addGame(game)
            // Adds the game to the database
            for (p in playerList) {
                // Iterates through the player data list
                val newGameInstance = GameInstance(
                    0,
                    gameId,
                    p.playerName,
                    p.charName,
                    p.eternal,
                    p.soulsNum,
                    p.winner,
                    p.solo
                )
                // Creates a new game instance to record each player
                gameDao.addGameInstance(newGameInstance)
                // Adds the new game instance
            }
        }
    }
}