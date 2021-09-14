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

                SettingsHandler.initialiseSettings(this)
                // Create settings file if there is none

                val settings = SettingsHandler.readSettings(this)

                runTutorial()
                // Show the tutorial

                if (settings["online"].toBoolean()) {

                    OnlineDataHandler.getGroupGames(this)
                    // Get any new online saved games
                }

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
                    OnlineDataHandler.saveGames(this)
                    // Save any unsaved games
                }
            }

            "settings" -> {
                runTutorial()
                if (SettingsHandler.readSettings(this)["online"].toBoolean()) {
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
        val issuesButton = findViewById<Button>(R.id.mainReport)
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
        issuesButton.typeface = fonts["body"]
        // Update the fonts

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        dataButton.setBackgroundResource(buttonBG)
        statsButton.setBackgroundResource(buttonBG)
        editButton.setBackgroundResource(buttonBG)
        settingsButton.setBackgroundResource(buttonBG)
        thanksButton.setBackgroundResource(buttonBG)
        issuesButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        dataButton.setOnClickListener {
            val goToData = Intent(this, EnterData::class.java)
            startActivity(goToData)
        }

        statsButton.setOnClickListener {
            val goToStats = Intent(this, StatisticsMenu::class.java)
            startActivity(goToStats)
        }

        editButton.setOnClickListener {
            val goToEdit = Intent(this, EditGames::class.java)
            startActivity(goToEdit)
        }

        settingsButton.setOnClickListener {
            val goToSettings = Intent(this, EditSettings::class.java)
            startActivity(goToSettings)
        }


        thanksButton.setOnClickListener {
            val thanksScreen = Intent(this, ShowThanks::class.java)
            startActivity(thanksScreen)
        }

        issuesButton.setOnClickListener {
            val issuesPage = Intent(this, FeedbackPage::class.java)
            startActivity(issuesPage)
        }
    }

    private fun runTutorial(){

        val config = ShowcaseConfig()
        config.delay = 200
        // Delay between each showcase view

        val dataButton = findViewById<Button>(R.id.mainData)
        val statsButton = findViewById<Button>(R.id.mainStats)
        val editButton = findViewById<Button>(R.id.mainEdit)
        val settingsButton = findViewById<Button>(R.id.mainSettings)
        val issuesButton = findViewById<Button>(R.id.mainReport)
        // Get the buttons

        val sequence = MaterialShowcaseSequence(this, "open_tutorial")

        sequence.setConfig(config)

        val data = MaterialShowcaseView.Builder(this)
            .setTarget(dataButton)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_data))
            .build()

        val stats = MaterialShowcaseView.Builder(this)
            .setTarget(statsButton)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_stats))
            .build()

        val edit = MaterialShowcaseView.Builder(this)
            .setTarget(editButton)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_edit))
            .build()

        val settings = MaterialShowcaseView.Builder(this)
            .setTarget(settingsButton)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_settings))
            .build()

        val issues = MaterialShowcaseView.Builder(this)
            .setTarget(issuesButton)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_issues))
            .build()

        sequence.addSequenceItem(data)
        sequence.addSequenceItem(stats)
        sequence.addSequenceItem(edit)
        sequence.addSequenceItem(settings)
        sequence.addSequenceItem(issues)

        sequence.setOnItemDismissedListener { itemView, _ ->
            if(itemView == issues)
                settingsButton.performClick()
        }

        sequence.start()
    }

    override fun onBackPressed() {
    // When the back button is pressed
        val dataButton = findViewById<Button>(R.id.mainData)
        val statsButton = findViewById<Button>(R.id.mainStats)
        val settingsButton = findViewById<Button>(R.id.mainSettings)
        when (source){
            null -> finish()
            "data_entry" -> dataButton.performClick()
            "statistics" -> statsButton.performClick()
            "settings" -> settingsButton.performClick()
            "thanks" -> finish()
        }
    }
}
