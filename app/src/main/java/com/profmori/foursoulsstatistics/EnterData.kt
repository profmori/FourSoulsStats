package com.profmori.foursoulsstatistics

import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.profmori.foursoulsstatistics.custom_adapters.CharListAdapter
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.PlayerHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.GameDAO
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.ItemList
import com.profmori.foursoulsstatistics.database.Player
import kotlinx.coroutines.launch

class EnterData : AppCompatActivity() {

    private lateinit var gameDatabase: GameDataBase

    // Initialise the database
    private lateinit var gameDao: GameDAO
    // Initialise the access object

    private var characterList: Array<CharEntity> = emptyArray()
    // Initialises character names

    private var playerList: Array<Player> = emptyArray()
    // Initialises player names

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_data)
        // Set the layout for the data page

        val charRecycler = findViewById<RecyclerView>(R.id.inputCharList)
        // Find the recycler view

        val playerNo = findViewById<EditText>(R.id.inputPlayerNumber)
        // Get the player number input box

        val playerPrompt = findViewById<TextView>(R.id.inputPlayerPrompt)
        // Get the player input title

        val treasureNo = findViewById<EditText>(R.id.inputTreasureNumber)
        // Get the treasure number input box

        val treasurePrompt = findViewById<TextView>(R.id.inputTreasurePrompt)
        // Gets the treasure prompt

        val titleView = findViewById<TextView>(R.id.inputTitle)
        // Gets the title

        val rerollButton = findViewById<Button>(R.id.rerollButton)
        // Gets the reroll button

        val coOpBox = findViewById<CheckBox>(R.id.inputCoOpBox)
        // Get the co-op check box

        val coOpPrompt = findViewById<TextView>(R.id.inputCoOpPrompt)
        // Get the co-op prompt

        var gameTreasures = treasureNo.text.toString().toInt()
        // Get the number of treasures

        var coOpGame = false

        var soloGame = false

        val background = findViewById<ImageView>(R.id.background)
        // Get the background of the page

        SettingsHandler.updateBackground(this, background)
        // Set it from the settings file

        var fromResults = true
        // By default assume you have come from the result entry page
        var playerNames = emptyArray<String>()
        var charNames = emptyArray<String>()
        var charImages = intArrayOf()
        var eternals = emptyArray<String?>()
        var souls = intArrayOf()
        // Create lists to store all of the results data

        try {
            playerNames = intent.getStringArrayExtra("names") as Array<String>
            charNames = intent.getStringArrayExtra("chars") as Array<String>
            charImages = intent.getIntArrayExtra("images") as IntArray
            eternals = intent.getStringArrayExtra("eternals") as Array<String?>
            souls = intent.getIntArrayExtra("souls") as IntArray
            gameTreasures = intent.getIntExtra("treasures", 0)
            coOpGame = intent.getBooleanExtra("coop", false)
            soloGame = intent.getBooleanExtra("solo", false)

            if (soloGame) {
                playerNo.setText("1")
            } else {
                playerNo.setText(playerNames.size.toString())
            }

            treasureNo.setText(gameTreasures.toString())
            // Get feedback from results if it exists and set the player and treasure numbers

        } catch (e: NullPointerException) {
            // If an error is thrown because this data was not passed
            fromResults = false
            // You haven't come from the results page
        }

        var playerCount = playerNo.text.toString().toInt()
        // Get the number of players in the game from the player number

        if (playerCount == 2) {
            coOpBox.visibility = View.VISIBLE
            coOpPrompt.visibility = View.VISIBLE
        } else {
            coOpBox.visibility = View.GONE
            coOpPrompt.visibility = View.GONE
        }
        coOpBox.isChecked = coOpGame

        val continueButton = findViewById<Button>(R.id.inputGameResults)
        // Gets the button to continue

        val returnButton = findViewById<Button>(R.id.inputToMain)
        // Gets the button to return to the main menu

        var playerHandlerList = emptyArray<PlayerHandler>()
        // Create an empty list of player handlers to hold the entered players

        if (fromResults) {
            // If data has been input
            for (i in (playerNames.indices)) {
                // Iterates through all players
                playerHandlerList += PlayerHandler(
                    playerNames[i],
                    charNames[i],
                    charImages[i],
                    eternals[i],
                    souls[i],
                    false,
                    soloGame
                )
                // Adds the player
            }
        } else {
            playerHandlerList = PlayerHandler.makePlayerList(playerCount)
            // Create adapter passing in the number of players
        }

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not)

        playerHandlerList.forEach {
            it.fonts = fonts
            // Sets the fonts of each player handler
        }

        var adapter = CharListAdapter(playerHandlerList)
        // Attach the adapter to the recyclerview to populate items
        charRecycler.adapter = adapter
        // Set layout manager to position the items
        charRecycler.layoutManager = GridLayoutManager(this, 2)
        // Lay the recycler out as a grid

        gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        gameDao = gameDatabase.gameDAO
        // Get the database access object

        val edition = SettingsHandler.getEditions(this)
        // Get the current editions from settings

        val settings = SettingsHandler.readSettings(this)
        // Get the current settings
        lifecycleScope.launch {
            // In a coroutine
            edition.forEach { characterList += gameDao.getCharacterList(it) }
            // For each edition you want characters for
            playerList = gameDao.getPlayers()
            // Get the player list
            playerHandlerList.forEach {
                it.addData(characterList, playerList, this@EnterData)
                // Add the player and character list to all player handlers

            }

        }

        playerNo.typeface = fonts["body"]
        playerPrompt.typeface = fonts["body"]
        treasureNo.typeface = fonts["body"]
        treasurePrompt.typeface = fonts["body"]
        titleView.typeface = fonts["title"]
        continueButton.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        coOpPrompt.typeface = fonts["body"]
        // Update the fonts

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        continueButton.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

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
                        PlayerHandler.updatePlayerList(playerHandlerList, playerCount, this)
                    // Makes a player list based on the number of players
                    adapter = CharListAdapter(playerHandlerList)
                    // Creates the recycler view adapter for this
                    charRecycler.adapter = adapter
                    // Creates the recycler view
                }
            } else {
                // If the user has just entered the text field
                playerNo.setText("")
                // Clear the input
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

        coOpBox.setOnCheckedChangeListener { _, b ->
            gameTreasures = if (b) {
                0
            } else {
                2
            }
            coOpGame = b
            treasureNo.setText(gameTreasures.toString())
            // Set the treasure number box to the game treasures
        }

        continueButton.setOnClickListener {
            tryMoveOn(playerHandlerList, gameTreasures, coOpGame, background)
            // Try to move to the next screen
        }

        returnButton.setOnClickListener {
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from", "enter_data")
            startActivity(backToMain)
            // Go back to the main screen
        }

        rerollButton.background = ImageHandler.randomReroll(this)
        // Set the image for the reroll icon from the available selection

        rerollButton.setOnClickListener {
            // When the reroll button is clicked
            var selectedChars = emptyArray<CharEntity>()
            SettingsHandler.saveToFile(this, settings)
            playerHandlerList.forEachIndexed { index, playerHandler ->
                // Iterate through all the current players added
                var randomChar = playerHandler.charList.random()
                // Select a random character from the list of possible options
                while (selectedChars.contains(randomChar) &&
                    // If a duplicate character has been selected
                    !settings["duplicate_characters"].toBoolean() &&
                    // And the user doesn't want duplicates
                    selectedChars.size < playerHandler.charList.size
                )
                // And the number of characters selected isn't equal to the total number of characters available
                {
                    if (randomChar.charName == "eden" && settings["duplicate_eden"].toBoolean()) {
                        break
                    }
                    randomChar = playerHandler.charList.random()
                    // Select a random character from the list of possible options
                }

                selectedChars += arrayOf(randomChar)
                // Add the character to the list of current characters
                if (selectedChars.distinct().size == playerHandler.charList.size) {
                    // If the number of selected characters equals the total number of characters (excluding duplicates)
                    selectedChars = emptyArray()
                    // Reset the number of selected characters
                }
                playerHandler.updateCharacter(randomChar.charName)
                // Set the player to that character
                if (randomChar.charName == "eden" &&
                    // If eden is selected
                    settings["random_eden"].toBoolean()
                )
                // And eden eternals are also to be randomised
                {
                    val randomEternal = ItemList.getItems(this).random()
                    // Generate a random Eden eternal
                    playerHandler.eternal = randomEternal
                    // Set the player eternal to the randomly selected one
                }
                adapter.notifyItemChanged(index)
                // Update the view
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // When the back arrow is pressed
                returnButton.performClick()
                // Clicks the return button
            }
        })
    }

    private fun tryMoveOn(
        playerHandlerList: Array<PlayerHandler>,
        gameTreasures: Int,
        coOpGame: Boolean,
        view: View
    ) {
        var moveOn = true
        // Say you can move on
        for (p in playerHandlerList) {
            // Iterate through all players
            if (((p.playerName == "") or (p.charName == "")) or
                (p.charName == "eden") and (p.eternal == null)
            ) {
                // If something is not entered correctly
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
            var playerNameList = emptyArray<String>()
            var charNameList = emptyArray<String>()
            var charImageList = intArrayOf()
            var eternalList = emptyArray<String?>()
            var soulsList = intArrayOf()
            // Store all of the relevant data in separate lists since PlayerHandler cannot be passed as an extra

            for (p in playerHandlerList) {
                playerNameList += arrayOf(p.playerName)
                charNameList += arrayOf(p.charName)
                charImageList += intArrayOf(p.charImage)
                eternalList += arrayOf(p.eternal)
                soulsList += intArrayOf(p.soulsNum)
            }
            // Add all of the data

            enterResult.putExtra("names", playerNameList)
            enterResult.putExtra("chars", charNameList)
            enterResult.putExtra("images", charImageList)
            enterResult.putExtra("treasures", gameTreasures)
            enterResult.putExtra("eternals", eternalList)
            enterResult.putExtra("souls", soulsList)
            enterResult.putExtra("coop", coOpGame)
            enterResult.putExtra("solo", playerHandlerList[0].solo)
            // Creates a set of extra parameters which passes all the data to the results page

            val dbPlayers = playerList.map { player -> player.playerName }
            // Gets the list of players stored in the database
            lifecycleScope.launch {
                for (player in playerNameList) {
                    // Iterates through all added players
                    if (!dbPlayers.contains(player)) {
                        // If the current player is not in the database
                        val dbPlayer = Player(player)
                        // Create a player object to store the new player
                        gameDao.addPlayer(dbPlayer)
                        // Add the new player to the database
                    }
                }
            }
            startActivity(enterResult)
            // Move to the result entry page

        } else {
            // If you cannot move on
            val errorSnackbar =
                Snackbar.make(view, R.string.input_too_few, Snackbar.LENGTH_LONG)
            // Create the snackbar
            errorSnackbar.changeFont(TextHandler.setFont(this)["body"]!!)
            // Set the font of the snackbar
            errorSnackbar.show()
            // Show the snackbar
        }
    }
}