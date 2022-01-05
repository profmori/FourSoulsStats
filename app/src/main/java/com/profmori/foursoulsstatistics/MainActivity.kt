package com.profmori.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharacterList
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import com.profmori.foursoulsstatistics.statistics_pages.StatisticsMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig


class MainActivity : AppCompatActivity() {

    private var source: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        source = intent.getStringExtra("from")
        // Get which view you are coming from

        when (source) {
            null -> {
                // When the app is opened from nothing

                CoroutineScope(Dispatchers.IO).launch {
                    // In a coroutine to avoid slowing down the app loading
                    val settings = SettingsHandler.readSettings(this@MainActivity)
                    // Get the current settings
                    if (settings["online"].toBoolean()) {
                        // If the user has agreed to be online

                        OnlineDataHandler.getGroupGames(this@MainActivity)
                        // Get any new online saved games
                    }

                    settings["version_no"] = SettingsHandler.versionCheck(
                        settings["version_no"],
                        this@MainActivity,
                        supportFragmentManager
                    )
                    // Update the version number and show any missed patch notes

                    SettingsHandler.saveToFile(this@MainActivity, settings)
                    // Save the updated settings

                }


                runTutorial()
                // Show the tutorial

                val gameDatabase = GameDataBase.getDataBase(this)
                // Get the database instance
                val gameDao = gameDatabase.gameDAO
                // Get the database access object
                val charList = CharacterList.charList
                // Gets the char list from the characters list class

                CoroutineScope(Dispatchers.IO).launch {
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
            }

            "enter_result" -> {
                if (SettingsHandler.readSettings(this)["online"].toBoolean()) {
                    // If the user has agreed to be online
                    OnlineDataHandler.saveGames(this)
                    // Save any unsaved games
                }
            }

            "settings" -> {
                runTutorial()
                if (SettingsHandler.readSettings(this)["online"].toBoolean()) {
                    // If the user has agreed to be online
                    OnlineDataHandler.getGroupGames(this)
                    // Get any games not stored locally
                }
            }
        }

        val titleText = findViewById<TextView>(R.id.mainTitle)
        val dataButton = findViewById<Button>(R.id.mainData)
        val statsButton = findViewById<Button>(R.id.mainStats)
        val editButton = findViewById<Button>(R.id.mainEdit)
        val settingsButton = findViewById<Button>(R.id.mainSettings)
        val thanksButton = findViewById<Button>(R.id.mainThanks)
        val feedbackButton = findViewById<Button>(R.id.mainReport)
        // Get all the main elements

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image view
        SettingsHandler.updateBackground(this, background)
        // Updates the background

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not

        titleText.typeface = fonts["title"]
        dataButton.typeface = fonts["body"]
        statsButton.typeface = fonts["body"]
        editButton.typeface = fonts["body"]
        settingsButton.typeface = fonts["body"]
        thanksButton.typeface = fonts["body"]
        feedbackButton.typeface = fonts["body"]
        // Update the fonts

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        dataButton.setBackgroundResource(buttonBG)
        statsButton.setBackgroundResource(buttonBG)
        editButton.setBackgroundResource(buttonBG)
        settingsButton.setBackgroundResource(buttonBG)
        thanksButton.setBackgroundResource(buttonBG)
        feedbackButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        //UpdateHandler.checkUpdates()

        dataButton.setOnClickListener {
            // When the enter game button is pressed
            val goToData = Intent(this, EnterData::class.java)
            startActivity(goToData)
            // Change to the Enter Data activity
        }

        statsButton.setOnClickListener {
            // When the view statistics button is pressed
            val goToStats = Intent(this, StatisticsMenu::class.java)
            startActivity(goToStats)
            // Change to the Statistics Menu activity
        }

        editButton.setOnClickListener {
            // When the edit games button is pressed
            val goToEdit = Intent(this, EditGames::class.java)
            startActivity(goToEdit)
            // Change to the Edit Games activity
        }

        settingsButton.setOnClickListener {
            // When the app settings button is pressed
            val goToSettings = Intent(this, EditSettings::class.java)
            startActivity(goToSettings)
            // Change to the Settings activity
        }


        thanksButton.setOnClickListener {
            // When the thanks button is pressed
            val thanksScreen = Intent(this, ShowThanks::class.java)
            startActivity(thanksScreen)
            // Change to the Thanks activity
        }

        feedbackButton.setOnClickListener {
            // When the feedback button is pressed
            val feedbackPage = Intent(this, FeedbackPage::class.java)
            startActivity(feedbackPage)
            // Change to the Feedback and Suggestions activity
        }
    }

    private fun runTutorial() {

        val config = ShowcaseConfig()
        config.delay = 200
        // Delay between each showcase view

        val dataButton = findViewById<Button>(R.id.mainData)
        val statsButton = findViewById<Button>(R.id.mainStats)
        val editButton = findViewById<Button>(R.id.mainEdit)
        val settingsButton = findViewById<Button>(R.id.mainSettings)
        val issuesButton = findViewById<Button>(R.id.mainReport)
        // Get the buttons

        val sequence = MaterialShowcaseSequence(this, "main_tutorial")
        // Creates the tutorial sequence handler

        sequence.setConfig(config)
        // Set the sequence config to the correct settings

        val data = MaterialShowcaseView.Builder(this)
            .setTarget(dataButton)
            .setDismissText(resources.getString(R.string.generic_dismiss))
            .setContentText(resources.getString(R.string.tutorial_data))
            .build()
        // Creates a focus on the Enter Game button

        val stats = MaterialShowcaseView.Builder(this)
            .setTarget(statsButton)
            .setDismissText(resources.getString(R.string.generic_dismiss))
            .setContentText(resources.getString(R.string.tutorial_stats))
            .build()
        // Creates a focus on the Statistics button

        val edit = MaterialShowcaseView.Builder(this)
            .setTarget(editButton)
            .setDismissText(resources.getString(R.string.generic_dismiss))
            .setContentText(resources.getString(R.string.tutorial_edit))
            .build()
        // Creates a focus on the Edit Games button

        val settings = MaterialShowcaseView.Builder(this)
            .setTarget(settingsButton)
            .setDismissText(resources.getString(R.string.generic_dismiss))
            .setContentText(resources.getString(R.string.tutorial_settings))
            .build()
        // Creates a focus on the Settings button

        val issues = MaterialShowcaseView.Builder(this)
            .setTarget(issuesButton)
            .setDismissText(resources.getString(R.string.generic_dismiss))
            .setContentText(resources.getString(R.string.tutorial_issues))
            .build()
        // Creates a focus on the Feedback button

        sequence.addSequenceItem(data)
        sequence.addSequenceItem(stats)
        sequence.addSequenceItem(edit)
        sequence.addSequenceItem(settings)
        sequence.addSequenceItem(issues)
        // Add all the items to the sequence in order

        sequence.setOnItemDismissedListener { itemView, _ ->
            if (itemView == issues) {
                // If the final sequence item has just finished
                settingsButton.performClick()
                // Click the settings button
            }
        }

        sequence.start()
        // Start running the tutorial
    }

    override fun onBackPressed() {
        // When the back button is pressed
        val dataButton = findViewById<Button>(R.id.mainData)
        val statsButton = findViewById<Button>(R.id.mainStats)
        val editButton = findViewById<Button>(R.id.mainEdit)
        val settingsButton = findViewById<Button>(R.id.mainSettings)
        val thanksButton = findViewById<Button>(R.id.mainThanks)
        val feedbackButton = findViewById<Button>(R.id.mainReport)
        // Get all the buttons
        when (source) {
            "enter_data" -> dataButton.performClick()
            "statistics" -> statsButton.performClick()
            "edit_data" -> editButton.performClick()
            "settings" -> settingsButton.performClick()
            "thanks" -> thanksButton.performClick()
            "feedback" -> feedbackButton.performClick()
            null -> finish()
            // Depending on the source click the correct button
        }
    }
}
