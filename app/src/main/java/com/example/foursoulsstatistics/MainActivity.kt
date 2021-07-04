package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foursoulsstatistics.database.CharacterList
import com.example.foursoulsstatistics.database.GameDataBase
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var updated = false
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
            updated = true
        }
        val dataButton = findViewById<Button>(R.id.mainData)

        dataButton.setOnClickListener {
            if(updated) {
                val goToData = Intent(this, EnterData::class.java)
                startActivity(goToData)
            }
        }

        val statsButton = findViewById<Button>(R.id.mainStats)

        statsButton.setOnClickListener {
            val goToStats = Intent(this, ViewStatistics::class.java)
            startActivity(goToStats)
        }

        val settingsButton = findViewById<Button>(R.id.mainSettings)

        settingsButton.setOnClickListener {
            //val goToSettings = Intent(this, settingsEdit::class.java)
            //startActivity(goToSettings)
        }
    }
}
