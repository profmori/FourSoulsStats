package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foursoulsstatistics.database.CharacterList
import com.example.foursoulsstatistics.database.GameDataBase
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SettingsHandler.initialiseSettings(this)

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

        val titleText = findViewById<TextView>(R.id.mainTitle)

        val dataButton = findViewById<Button>(R.id.mainData)

        val statsButton = findViewById<Button>(R.id.mainStats)

        val settingsButton = findViewById<Button>(R.id.mainSettings)

        val background = findViewById<ImageView>(R.id.background)

        SettingsHandler.updateBackground(this, background)

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
        val source = intent.getStringExtra("from")
        if (source != "EnterResult"){
            finish()
        }
    }
}
