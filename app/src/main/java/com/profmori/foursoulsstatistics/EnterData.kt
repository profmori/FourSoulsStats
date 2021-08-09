package com.profmori.foursoulsstatistics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.custom_adapters.CharListAdapter
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.PlayerHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.GameDAO
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.Player
import kotlinx.coroutines.launch
import java.lang.NullPointerException

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

        var fromResults = true
        var playerNames = emptyArray<String>()
        var charNames = emptyArray<String>()
        var charImages = intArrayOf()
        var eternals = emptyArray<String?>()

        try {
            playerNames = intent.getStringArrayExtra("names") as Array<String>
            charNames = intent.getStringArrayExtra("chars") as Array<String>
            charImages = intent.getIntArrayExtra("images") as IntArray
            eternals = intent.getStringArrayExtra("eternals") as Array<String?>
            // Get feedback from results if it exists
        }catch (e: NullPointerException){
            fromResults = false
        }

        val charRecycler = findViewById<RecyclerView>(R.id.inputCharList)
        // Find the recycler view

        val playerNo = findViewById<EditText>(R.id.inputPlayerNumber)
        // Get the player number input box

        val playerPrompt = findViewById<TextView>(R.id.inputPlayerPrompt)

        val treasureNo = findViewById<EditText>(R.id.inputTreasureNumber)
        // Get the treasure number input box

        val treasurePrompt = findViewById<TextView>(R.id.inputTreasurePrompt)
        // Gets the treasure prompt

        val titleView = findViewById<TextView>(R.id.inputTitle)
        // Gets the title

        var gameTreasures = treasureNo.text.toString().toInt()
        // Get the number of treasures

        var playerCount = playerNo.text.toString().toInt()
        // Get the number of players in the game from the player number

        val continueButton = findViewById<Button>(R.id.inputGameResults)
        // Gets the button to continue

        val returnButton = findViewById<Button>(R.id.inputToMain)
        // Gets the button to return to the main menu

        var playerHandlerList = emptyArray<PlayerHandler>()

        if (fromResults){
        // If data has been input
            for (i in (playerNames.indices)){
                // Iterates through all players
                playerHandlerList += PlayerHandler(playerNames[i],charNames[i],charImages[i],eternals[i],0,false)
                // Adds the player
            }
        }else{
            playerHandlerList = PlayerHandler.makePlayerList(playerCount)
            // Create adapter passing in the number of players
        }

        val background = findViewById<ImageView>(R.id.background)

        SettingsHandler.updateBackground(this, background)

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not

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

        val altArt = SettingsHandler.readSettings(this)["alt_art"]
        lifecycleScope.launch {
        // In a coroutine
            edition.forEach{characterList += gameDao.getCharacterList(it)}
            // For each edition you want characters for
            playerList = gameDao.getPlayers()
            // Get the player list
            playerHandlerList.forEach {
                it.addData(characterList, playerList)
                // Add the player and character list to all player handlers
                it.useAlts = altArt.toBoolean()
                // Set the player handler flag for using alt art correctly
            }

        }

        playerNo.typeface = fonts["body"]
        playerPrompt.typeface = fonts["body"]
        treasureNo.typeface = fonts["body"]
        treasurePrompt.typeface = fonts["body"]
        titleView.typeface = fonts["title"]
        continueButton.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        // Update the fonts

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        continueButton.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

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
                    }
                    playerNo.setText(playerCount.toString())
                    // Rewrite the text field to show this
                    playerHandlerList = PlayerHandler.updatePlayerList(playerHandlerList, playerCount)
                    // Makes a player list based on the number of players
                    adapter = CharListAdapter(playerHandlerList)
                    // Creates the recycler view adapter for this
                    charRecycler.adapter = adapter
                    // Creates the recycler view
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
                    treasureNo.setText(gameTreasures.toString())
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

        continueButton.setOnClickListener {
            tryMoveOn(playerHandlerList,gameTreasures)
            // Try to move to the next screen
        }

        returnButton.setOnClickListener {
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from","data_entry")
            startActivity(backToMain)
            // Go back to the main screen
        }
    }

    override fun onBackPressed() {
        val returnButton = findViewById<Button>(R.id.inputToMain)
        // Get the return button
        returnButton.performClick()
        // Clicks the button
    }

    private fun tryMoveOn(playerHandlerList: Array<PlayerHandler>, gameTreasures: Int){
        var moveOn = true
        // Say you can move on
        for (p in playerHandlerList) {
        // Iterate through all players
            if (((p.playerName == "") or (p.charName == "")) or
                    (p.charName == "eden") and (p.eternal == null)){
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
            for(p in playerHandlerList){
                playerNameList += arrayOf(p.playerName)
                charNameList += arrayOf(p.charName)
                charImageList += intArrayOf(p.charImage)
                eternalList += arrayOf(p.eternal)
            }
            enterResult.putExtra("names",playerNameList)
            enterResult.putExtra("chars",charNameList)
            enterResult.putExtra("images",charImageList)
            enterResult.putExtra("treasures",gameTreasures)
            enterResult.putExtra("eternals", eternalList)
            // Creates an extra parameter which passes the number of treasures to the results page
            val handler = playerHandlerList.map{playerHandler -> playerHandler.playerName}.toTypedArray()
            val list = playerList.map{player -> player.playerName }
            lifecycleScope.launch {
                for (h in handler) {
                    if (!list.contains(h)) {
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