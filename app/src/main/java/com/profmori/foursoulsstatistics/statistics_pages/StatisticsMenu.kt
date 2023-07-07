package com.profmori.foursoulsstatistics.statistics_pages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.MainActivity
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler.Companion.checkWifi
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler.Companion.getAllGames
import kotlinx.coroutines.launch


class StatisticsMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics_menu)

        val titleText = findViewById<TextView>(R.id.statsTitle)
        // Gets the title

        val buttonsRecycler = findViewById<RecyclerView>(R.id.statsButtonList)

        val returnButton = findViewById<Button>(R.id.statsBackButton)
        // Gets the back button

        val background = findViewById<ImageView>(R.id.background)
        // Get the background image view
        SettingsHandler.updateBackground(this, background)
        // Update the background

        val fonts = TextHandler.setFont(this)
        // Get the fonts from the text handler

        titleText.typeface = fonts["title"]
        returnButton.typeface = fonts["body"]
        // Set return button and title fonts

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        returnButton.setBackgroundResource(buttonBG)
        // Set the button background

        val gameDatabase = GameDataBase.getDataBase(this)
        // Get the database instance
        val gameDao = gameDatabase.gameDAO
        // Get the database access object

        lifecycleScope.launch {
            // As a coroutine

            val competitiveGames = gameDao.getGames(false)
            val coopGames = gameDao.getGames(true)
            // Get all games

            var menuItems = emptyArray<StatsPageProperties>()
            var noLocal = true
            var noOnline = true

            if (competitiveGames.isNotEmpty() or coopGames.isNotEmpty()) {
                // If there are games stored
                if (competitiveGames.isNotEmpty()) {
                    menuItems += StatsPageProperties(R.string.statistics_group_player, "Player")
                    menuItems += StatsPageProperties(
                        R.string.statistics_group_character,
                        "Character"
                    )
                }
                if (coopGames.isNotEmpty()) {
                    menuItems += StatsPageProperties(
                        R.string.statistics_group_solo_character,
                        "Character",
                        coop = true
                    )
                }
                // Add the local buttons to the menu
                noLocal = false
                // Don't show the popup to say there are no local games
            }

            if (checkWifi(this@StatisticsMenu)) {
                // If there is a wifi connection
                val onlineCompetitive = getAllGames(this@StatisticsMenu, false)
                val onlineCoop = getAllGames(this@StatisticsMenu, true)

                if (onlineCompetitive.isNotEmpty()) {
                    menuItems += StatsPageProperties(
                        R.string.statistics_global_character,
                        "Character",
                        online = true
                    )
                    menuItems += StatsPageProperties(
                        R.string.statistics_global_eternal,
                        "Eternal",
                        online = true
                    )
                }

                if (onlineCoop.isNotEmpty()) {
                    menuItems += StatsPageProperties(
                        R.string.statistics_global_solo_character,
                        "Character",
                        online = true,
                        coop = true
                    )
                    menuItems += StatsPageProperties(
                        R.string.statistics_global_solo_eternal,
                        "Eternal",
                        online = true,
                        coop = true
                    )
                }
                // Add the community buttons to the menu
                noOnline = false
                // Don't show the popup to say there is no wifi

            }

            val menuAdapter = StatsMenuAdapter(menuItems, fonts["body"]!!, buttonBG)
            buttonsRecycler.adapter = menuAdapter
            buttonsRecycler.layoutManager = GridLayoutManager(this@StatisticsMenu, 1)
            // Lay the recycler out as a list

            returnButton.setOnClickListener {
                // When the return button is clicked
                val backToMain = Intent(this@StatisticsMenu, MainActivity::class.java)
                // Create an intent back to the main screen
                backToMain.putExtra("from", "statistics")
                startActivity(backToMain)
                // Go back to the main screen
            }

            if (noLocal and noOnline) {
                noStatsAvailable(this@StatisticsMenu)
                returnButton.performClick()
            } else if (noLocal) {
                invalidGroupPopup(this@StatisticsMenu)
                // Show the popup for an invalid group
            } else if (noOnline) {
                noOnlineConnectivity(this@StatisticsMenu)
                // Show the popup to say there is no wifi
            }

        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                returnButton.performClick()
                // Clicks the button
            }
        })
    }

    private fun invalidGroupPopup(context: Context) {
        val source = intent.getStringExtra("from")
        if (source == null) {
            val existsToast = Toast.makeText(
                context,
                R.string.stats_no_local,
                Toast.LENGTH_LONG
            )
            // Create the Toast
            existsToast.show()
            // Show the Toast
        }
    }

    private fun noOnlineConnectivity(context: Context) {
        val source = intent.getStringExtra("from")
        if (source == null) {
            val disconnectedToast = Toast.makeText(
                context,
                R.string.stats_no_wifi,
                Toast.LENGTH_LONG
            )
            // Create the Toast
            disconnectedToast.show()
            // Show the Toast
        }
    }

    private fun noStatsAvailable(context: Context) {
        val source = intent.getStringExtra("from")
        if (source == null) {
            val noDataToast = Toast.makeText(
                context,
                R.string.stats_no_data,
                Toast.LENGTH_LONG
            )
            // Create the toast
            noDataToast.show()
            // Show the toast
        }
    }
}