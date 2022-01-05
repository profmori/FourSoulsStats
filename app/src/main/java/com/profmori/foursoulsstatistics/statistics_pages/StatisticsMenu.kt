package com.profmori.foursoulsstatistics.statistics_pages

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.profmori.foursoulsstatistics.MainActivity
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.changeFont
import com.profmori.foursoulsstatistics.custom_adapters.*
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.*
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.launch


class StatisticsMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics_menu)

        val titleText = findViewById<TextView>(R.id.statsTitle)
        // Gets the title

        val playerButton = findViewById<Button>(R.id.playerStats)
        val localCharButton = findViewById<Button>(R.id.localCharStats)
        val communityCharButton = findViewById<Button>(R.id.communityCharStats)
        val communityEternalButton = findViewById<Button>(R.id.communityEternalStats)
        // Get the different statistics page buttons

        val returnButton = findViewById<Button>(R.id.statsBackButton)
        // Gets the back button

        val background = findViewById<ImageView>(R.id.background)
        // Get the background image view
        SettingsHandler.updateBackground(this, background)
        // Update the background

        val fonts = TextHandler.setFont(this)
        // Get the fonts from the text handler

        titleText.typeface = fonts["title"]
        playerButton.typeface = fonts["body"]
        localCharButton.typeface = fonts["body"]
        communityCharButton.typeface = fonts["body"]
        communityEternalButton.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        // Set all button and title fonts

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        playerButton.setBackgroundResource(buttonBG)
        localCharButton.setBackgroundResource(buttonBG)
        communityCharButton.setBackgroundResource(buttonBG)
        communityEternalButton.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        lifecycleScope.launch {
            // As a coroutine

            val games = gameDao.getGames()
            // Get all games

            playerButton.setOnClickListener {
                // When the player stats button is clicked
                if (games.isNotEmpty()) {
                    // If some games have been played
                    val playerStats = Intent(this@StatisticsMenu, ViewPlayerStats::class.java)
                    startActivity(playerStats)
                    // Go to the player stats page
                } else {
                    invalidGroupPopup(playerButton)
                    // Show the popup for an invalid group
                }
            }

            localCharButton.setOnClickListener {
                // When the local character stats button is clicked
                if (games.isNotEmpty()) {
                    // If some games have been played
                    val localChar = Intent(this@StatisticsMenu, ViewLocalCharacterStats::class.java)
                    startActivity(localChar)
                    // Go to the local character stats page
                } else {
                    invalidGroupPopup(localCharButton)
                    // Show the popup for an invalid group
                }
            }

            communityCharButton.setOnClickListener {
                // When the community character stats button is clicked
                if (OnlineDataHandler.checkWifi(this@StatisticsMenu)) {
                    // If there is a wifi connection
                    val onlineChar =
                        Intent(this@StatisticsMenu, ViewCommunityCharacterStats::class.java)
                    startActivity(onlineChar)
                    // Go to the community character stats page
                } else {
                    noOnlineConnectivity(communityCharButton)
                    // Show the popup to say there is no wifi
                }
            }

            communityEternalButton.setOnClickListener {
                // When the community eternal stats button is clicked
                if (OnlineDataHandler.checkWifi(this@StatisticsMenu)) {
                    // If there is a wifi connection
                    val onlineEternal =
                        Intent(this@StatisticsMenu, ViewCommunityEternalStats::class.java)
                    startActivity(onlineEternal)
                    // Go to the community eternal stats page
                } else {
                    noOnlineConnectivity(communityCharButton)
                    // Show the popup to say there is no wifi
                }
            }
        }

        returnButton.setOnClickListener {
            // When the return button is clicked
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from", "statistics")
            startActivity(backToMain)
            // Go back to the main screen
        }
    }

    override fun onBackPressed() {
        val returnButton = findViewById<Button>(R.id.statsBackButton)
        // Get the return button
        returnButton.performClick()
        // Clicks the button
    }

    private fun invalidGroupPopup(view: TextView) {
        val existsSnackbar = Snackbar.make(
            view,
            R.string.stats_no_local,
            Snackbar.LENGTH_LONG
        )
        // Create the snackbar
        existsSnackbar.changeFont(TextHandler.setFont(this)["body"]!!)
        // Set the font of the snackbar
        existsSnackbar.show()
        // Show the snackbar
    }

    private fun noOnlineConnectivity(view: TextView) {
        val disconnectedSnackbar = Snackbar.make(
            view,
            R.string.stats_no_wifi,
            Snackbar.LENGTH_LONG
        )
        // Create the snackbar
        disconnectedSnackbar.changeFont(TextHandler.setFont(this)["body"]!!)
        // Set the font of the snackbar
        disconnectedSnackbar.show()
        // Show the snackbar
    }
}