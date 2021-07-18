package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foursoulsstatistics.data_handlers.SettingsHandler
import com.example.foursoulsstatistics.data_handlers.TextHandler
import com.example.foursoulsstatistics.database.CharacterList
import com.example.foursoulsstatistics.database.GameDataBase
import com.example.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    var source: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        source = intent.getStringExtra("from")

        if(source == null){
            SettingsHandler.initialiseSettings(this)
            // Create settings file if there is none

            OnlineDataHandler.saveGames(this)
            // Save any unsaved games

            OnlineDataHandler.getGroupGames(this)
            // Get any new online saved games

            val gameDatabase = GameDataBase.getDataBase(this)
            // Get the database instance
            val gameDao = gameDatabase.gameDAO
            // Get the database access object
            val charList = CharacterList.charList
            // Gets the char list from the characters list class

            lifecycleScope.launch {
                val currentChars = gameDao.getFullCharacterList()
                // Gets the current character database
                for (char in charList) {
                    // Iterates through the characters
                    if (!currentChars.contains(char)) {
                        // If the character is not already in the database
                        gameDao.updateCharacter(char)
                        // Add the character, replacing existing versions
                    }
                }
            }
        }else if(source == "enter_result"){
            OnlineDataHandler.saveGames(this)
            // Save any unsaved games
        }else if(source == "settings"){
            OnlineDataHandler.getGroupGames(this)
        }

        val titleText = findViewById<TextView>(R.id.mainTitle)
        val dataButton = findViewById<Button>(R.id.mainData)
        val statsButton = findViewById<Button>(R.id.mainStats)
        val settingsButton = findViewById<Button>(R.id.mainSettings)
        // Get all the main elements

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image view
        SettingsHandler.updateBackground(this, background)
        // Updates the background

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not

        if (titleText.typeface != fonts["title"]){
        // If the fonts are wrong
            titleText.typeface = fonts["title"]
            dataButton.typeface = fonts["body"]
            statsButton.typeface = fonts["body"]
            settingsButton.typeface = fonts["body"]
            // Update them
        }

        dataButton.setOnClickListener {
            val goToData = Intent(this, EnterData::class.java)
            startActivity(goToData)
        }

        statsButton.setOnClickListener {
            val goToStats = Intent(this, ViewStatistics::class.java)
            startActivity(goToStats)
        }

        settingsButton.setOnClickListener {
            val goToSettings = Intent(this, EditSettings::class.java)
            startActivity(goToSettings)
        }
    }

    override fun onBackPressed() {
    // When the back button is pressed
        if (source != "enter_result") {
        // If you have not come from the results screen
            finish()
            // Act normally
        }
    }
}
