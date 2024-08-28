package com.profmori.foursoulsstatistics

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.profmori.foursoulsstatistics.custom_adapters.DropDownAdapter
import com.profmori.foursoulsstatistics.custom_adapters.SetSelectionAdapter
import com.profmori.foursoulsstatistics.custom_adapters.dialogs.ChangeGroupDialog
import com.profmori.foursoulsstatistics.custom_adapters.dialogs.ConfirmDeleteDialog
import com.profmori.foursoulsstatistics.custom_adapters.tutorial.TutorialAdaptor
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.LanguageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.set

class EditSettings : AppCompatActivity() {

    private var borderList = emptyMap<String, String>()
    // Initialises the border list

    private var backgroundList = emptyMap<String, String>()
    // Initialises the background list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_settings)

        val settings = SettingsHandler.readSettings(this)

        borderList = mapOf(
            resources.getString(R.string.character_back) to "character_back",
            resources.getString(R.string.eternal_back) to "eternal_back",
            resources.getString(R.string.loot_back) to "loot_back",
            resources.getString(R.string.monster_back) to "monster_back",
            resources.getString(R.string.soul_back) to "soul_back",
            resources.getString(R.string.treasure_back) to "treasure_back",
            resources.getString(R.string.room_back) to "room_back"
        )
        // Lists all the border options and their save strings

        backgroundList = mapOf(
            resources.getString(R.string.character_back) to "character_back",
            resources.getString(R.string.eternal_back) to "eternal_back",
            resources.getString(R.string.loot_back) to "loot_back",
            resources.getString(R.string.treasure_back) to "treasure_back",
            resources.getString(R.string.monster_back) to "monster_back"
        )
        // Lists all the background options and their save strings

        val editionTitle = findViewById<TextView>(R.id.settingsEditionPrompt)
        // Gets the main edition prompt

        val customButton = findViewById<Button>(R.id.customButton)
        // Find the custom card entry button

        if (!settings["custom"].toBoolean()) {
            customButton.visibility = View.GONE
        }
        // If custom cards are not being used, hide the input button

        val pixelLine = findViewById<ConstraintLayout>(R.id.pixelLine)
        val pixelSwitch = findViewById<SwitchCompat>(R.id.pixelSwitch)
        val pixelFont = findViewById<TextView>(R.id.pixelText)
        pixelSwitch.isChecked = settings["pixel_font"].toBoolean()
        // Match the pixel font settings

        if (!settings["retro"].toBoolean()) {
            pixelLine.visibility = View.GONE
        }
        // If retro eden is not being used, hide the switch


        val easyFont = findViewById<SwitchCompat>(R.id.readableSwitch)
        easyFont.isChecked = settings["readable_font"].toBoolean()
        // Match readability switch

        val borderText = findViewById<TextView>(R.id.borderPrompt)
        val borderSpinner = findViewById<Spinner>(R.id.borderSpinner)
        // Get the border line

        val backgroundText = findViewById<TextView>(R.id.backgroundPrompt)
        val backgroundSpinner = findViewById<Spinner>(R.id.backgroundSpinner)
        // Get the background line

        val online = findViewById<SwitchCompat>(R.id.onlineSwitch)
        online.isChecked = settings["online"].toBoolean()
        // Match the online saving behaviour

        val duplicateChars = findViewById<SwitchCompat>(R.id.duplicateSwitch)
        duplicateChars.isChecked = settings["duplicate_characters"].toBoolean()

        val duplicateEden = findViewById<SwitchCompat>(R.id.duplicateEdenSwitch)
        duplicateEden.isChecked = settings["duplicate_eden"].toBoolean()

        val randomEden = findViewById<SwitchCompat>(R.id.randomEternalSwitch)
        randomEden.isChecked = settings["random_eden"].toBoolean()

        val randomButton = findViewById<Button>(R.id.rerollIconSelect)

        val groupPrompt = findViewById<TextView>(R.id.groupIDPrompt)
        val groupEntry = findViewById<EditText>(R.id.groupIDEntry)
        val groupExplain = findViewById<TextView>(R.id.groupIDExplanation)
        // Gets the group id prompts

        val oldId = settings["groupID"]
        groupEntry.setText(oldId)
        // Get the current id and set the group entry to the current group id
        var existingIds = emptyArray<String>()
        // Initialises as an empty array
        CoroutineScope(Dispatchers.IO).launch {
            // Running asynchronously
            existingIds = OnlineDataHandler.getGroupIDs()
            // Gets the existing ids in the online database
        }

        val returnButton = findViewById<Button>(R.id.settingsMainButton)
        // Get the return button

        val patchButton = findViewById<Button>(R.id.patchNotesButton)
        // Get the patch notes button

        val clearButton = findViewById<Button>(R.id.clearButton)
        // Gets the button to clear data

        val tutorialButton = findViewById<Button>(R.id.tutorialButton)
        // Get the button to reset the tutorial

        val titleText = findViewById<TextView>(R.id.settingsTitle)
        // Get the title

        val backgroundImage = findViewById<ImageView>(R.id.background)
        // Get the background image
        SettingsHandler.updateBackground(this, backgroundImage)
        // Update it

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        val changeLanguage = findViewById<Button>(R.id.settingsLanguage)
        LanguageHandler.getLanguage(changeLanguage)

        changeLanguage.setOnClickListener {
            LanguageHandler.changeLanguage()
        }



        customButton.setBackgroundResource(buttonBG)
        clearButton.setBackgroundResource(buttonBG)
        tutorialButton.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        randomButton.setBackgroundResource(buttonBG)
        patchButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        updateFonts(
            titleText,
            groupPrompt,
            groupEntry,
            groupExplain,
            online,
            editionTitle,
            customButton,
            pixelFont,
            pixelLine,
            borderText,
            borderSpinner,
            backgroundText,
            backgroundSpinner,
            duplicateChars,
            duplicateEden,
            randomEden,
            randomButton,
            clearButton,
            tutorialButton,
            returnButton,
            patchButton
        )
        // Update the fonts for every item

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        // Finds the scrolling view

        scrollView.scrollTo(0, 0)
        // Move to the top of the scroll view when the settings page opens

        if (SettingsHandler.getTutorial(this)[0]) {
            runTutorial()
        }
        // Run the tutorial

        borderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // When a border item is selected
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                updateSave(
                    easyFont,
                    pixelSwitch,
                    borderSpinner,
                    backgroundSpinner,
                    online,
                    randomEden,
                    duplicateChars,
                    duplicateEden,
                    groupEntry
                )
                // Update the current settings
                SettingsHandler.updateBackground(borderSpinner.context, backgroundImage)
                // Update the background image
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        backgroundSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // When a background is selected
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                updateSave(
                    easyFont,
                    pixelSwitch,
                    borderSpinner,
                    backgroundSpinner,
                    online,
                    randomEden,
                    duplicateChars,
                    duplicateEden,
                    groupEntry
                )
                // Update the current settings
                SettingsHandler.updateBackground(backgroundSpinner.context, backgroundImage)
                // Update the background image
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        easyFont.setOnCheckedChangeListener { _, _ ->
            // When the font slider is changed
            updateSave(
                easyFont,
                pixelSwitch,
                borderSpinner,
                backgroundSpinner,
                online,
                randomEden,
                duplicateChars,
                duplicateEden,
                groupEntry
            )
            // Update the save data

            updateFonts(
                titleText,
                groupPrompt,
                groupEntry,
                groupExplain,
                online,
                editionTitle,
                customButton,
                pixelFont,
                pixelLine,
                borderText,
                borderSpinner,
                backgroundText,
                backgroundSpinner,
                duplicateChars,
                duplicateEden,
                randomEden,
                randomButton,
                clearButton,
                tutorialButton,
                returnButton,
                patchButton
            )
            // Change all the fonts
        }

        pixelSwitch.setOnCheckedChangeListener { _, _ ->
            // When the pixel font slider is changed

            updateSave(
                easyFont,
                pixelSwitch,
                borderSpinner,
                backgroundSpinner,
                online,
                randomEden,
                duplicateChars,
                duplicateEden,
                groupEntry
            )
            // Update the save data

            updateFonts(
                titleText,
                groupPrompt,
                groupEntry,
                groupExplain,
                online,
                editionTitle,
                customButton,
                pixelFont,
                pixelLine,
                borderText,
                borderSpinner,
                backgroundText,
                backgroundSpinner,
                duplicateChars,
                duplicateEden,
                randomEden,
                randomButton,
                clearButton,
                tutorialButton,
                returnButton,
                patchButton
            )
            // Change all the fonts
        }

        customButton.setOnClickListener {
            // When the custom cards button is clicked
            updateSave(
                easyFont,
                pixelSwitch,
                borderSpinner,
                backgroundSpinner,
                online,
                randomEden,
                duplicateChars,
                duplicateEden,
                groupEntry
            )
            // Update the save before moving to the custom page
            val customIntent = Intent(this, CustomCardEntry::class.java)
            startActivity(customIntent)
            // Move to the custom cards page
        }

        randomButton.setOnClickListener {
            // When the custom cards button is clicked
            updateSave(
                easyFont,
                pixelSwitch,
                borderSpinner,
                backgroundSpinner,
                online,
                randomEden,
                duplicateChars,
                duplicateEden,
                groupEntry
            )
            // Update the save before moving to the custom page
            val rerollIntent = Intent(this, RerollPrefSelect::class.java)
            startActivity(rerollIntent)
            // Move to the custom cards page
        }

        clearButton.setOnClickListener {
            // When the button to clear all data is clicked
            val fonts = TextHandler.setFont(this)
            // Get the font for the deletion dialog
            val clearDialog = ConfirmDeleteDialog(this, fonts["body"]!!)
            clearDialog.show(supportFragmentManager, "clearData")
            // Create and show the confirmation to change the group ID
        }

        tutorialButton.setOnClickListener {
            // When the tutorial reset is clicked
            SettingsHandler.setTutorial(this, true)
            // Reset the tutorial variable so they show again
            returnButton.performClick()
            // Click the return button to save and go back to the main menu
        }


        returnButton.setOnClickListener {
            // When the return button is clicked
            updateSave(
                easyFont,
                pixelSwitch,
                borderSpinner,
                backgroundSpinner,
                online,
                randomEden,
                duplicateChars,
                duplicateEden,
                groupEntry
            )
            // Save the new settings file
            val newID = SettingsHandler.sanitiseGroupID(groupEntry.text.toString().uppercase())
            // Get the new ID from th group id input, and sanitise it to remove ambiguous characters
            if (newID !in existingIds) {
                // If the new id doesn't exist already
                OnlineDataHandler.saveGroupID(newID)
                // Save the group id online
            }
            if (newID != oldId) {
                // If the id has changed from the old one
                val dataBase = GameDataBase.getDataBase(this)
                val gameDao = dataBase.gameDAO
                // Get the database access object to clear all the games
                CoroutineScope(Dispatchers.IO).launch {
                    gameDao.clearGames()
                    gameDao.clearGameInstances()
                    // Clear existing data which will be replaced in the main thread
                }
            }
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from", "settings")
            startActivity(backToMain)
            // Go back to the main menu
        }

        groupEntry.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // if the soft input is done
                val imm =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // Get an input method manager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                // Hide the keyboard
                groupEntry.clearFocus()
                // Clear the focus of the edit text
                return@setOnEditorActionListener true
            }
            false
        }

        groupEntry.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                // If the edit text has lost focus
                if (groupEntry.text.length < 6) {
                    // If the entry is too short
                    val shortSnackbar =
                        Snackbar.make(view, R.string.settings_invalid_group, Snackbar.LENGTH_LONG)
                    // Create the snackbar
                    shortSnackbar.changeFont(TextHandler.setFont(this)["body"]!!)
                    // Set the font of the snackbar
                    shortSnackbar.show()
                    // Show the snackbar
                    groupEntry.setText(settings["groupID"])
                    // Reset the text in the edit text
                } else {
                    var id = groupEntry.text.toString().uppercase()
                    id = SettingsHandler.sanitiseGroupID(id)
                    if (id in existingIds) {
                        val existsSnackbar = Snackbar.make(
                            view, R.string.settings_duplicate_group, Snackbar.LENGTH_LONG
                        )
                        // Create the snackbar
                        existsSnackbar.changeFont(TextHandler.setFont(this)["body"]!!)
                        // Set the font of the snackbar
                        existsSnackbar.show()
                        // Show the snackbar
                    }

                    if (id != oldId) {
                        val fonts = TextHandler.setFont(this)
                        // Get the font for the dialog
                        val entryDialog = ChangeGroupDialog(groupEntry, oldId!!, fonts["body"]!!)
                        entryDialog.show(supportFragmentManager, "groupID")
                        // Create and show the confirmation to change the group ID

                    }
                }
            }
        }

        patchButton.setOnClickListener {
            SettingsHandler.versionCheck(
                "0", this, supportFragmentManager
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // When the back arrow is pressed
                returnButton.performClick()
                // Do everything that would happen from pressing the actual button
            }
        })
    }

    private fun runTutorial() {

        val editionSelect = findViewById<ConstraintLayout>(R.id.editionSelect)
        // Get editions area
        val easyFont = findViewById<SwitchCompat>(R.id.readableSwitch)
        // Match readability switch
        val borderLine = findViewById<ConstraintLayout>(R.id.borderGroup)
        // Get the border line
        val backgroundLine = findViewById<ConstraintLayout>(R.id.backgroundGroup)
        // Get the background line
        val online = findViewById<SwitchCompat>(R.id.onlineSwitch)
        // Match the online saving behaviour
        val groupLine = findViewById<TextView>(R.id.groupIDEntry)
        // Gets the group id input
        val clearButton = findViewById<Button>(R.id.clearButton)
        // Gets the button to clear data
        val rerollButton = findViewById<Button>(R.id.rerollIconSelect)
        // Get the button to pick a reroll icon
        val rerollOptions = findViewById<ConstraintLayout>(R.id.randomSettingsGroup)
        //Get the full settings group
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        // Get the scroll view so the view can be automatically scrolled


        val clearData = TutorialAdaptor.runTutorialStep(
            this, clearButton, R.string.tutorial_clear_button, this, scrollView = scrollView
        )
        // Highlights the button to clear all data
        val rerollSettings2 = TutorialAdaptor.runTutorialStep(
            this,
            rerollOptions,
            R.string.tutorial_random_settings_2,
            clearData,
            scrollView = scrollView
        )
        val rerollSettings = TutorialAdaptor.runTutorialStep(
            this,
            rerollOptions,
            R.string.tutorial_random_settings,
            rerollSettings2,
            scrollView = scrollView
        )
        // Highlights the reroll options
        val rerollIconSelect = TutorialAdaptor.runTutorialStep(
            this,
            rerollButton,
            R.string.tutorial_select_icon,
            rerollSettings,
            scrollView = scrollView
        )
        // Highlights the reroll icon selection button
        val background = TutorialAdaptor.runTutorialStep(
            this,
            backgroundLine,
            R.string.tutorial_background,
            rerollIconSelect,
            scrollView = scrollView
        )
        // Highlights the background select dropdown
        val border = TutorialAdaptor.runTutorialStep(
            this, borderLine, R.string.tutorial_border, background, scrollView = scrollView
        )
        // Highlights the border select dropdown
        val edition = TutorialAdaptor.runTutorialStep(
            this, editionSelect, R.string.tutorial_edition, border, scrollView = scrollView
        )
        // Highlights the edition select area
        val easySwitch = TutorialAdaptor.runTutorialStep(
            this, easyFont, R.string.tutorial_readable_font, edition, scrollView = scrollView
        )
        // Highlights the readable font switch
        val onlineSwitch = TutorialAdaptor.runTutorialStep(
            this, online, R.string.tutorial_online, easySwitch, scrollView = scrollView
        )
        // Highlights the online connection switch
        val groupID = TutorialAdaptor.runTutorialStep(
            this, groupLine, R.string.tutorial_group_id, onlineSwitch, scrollView = scrollView
        )
        // Highlights the group id input area
        groupID.build()
    }

    private fun updateFonts(
        titleText: TextView,
        groupPrompt: TextView,
        groupEntry: EditText,
        groupExplain: TextView,
        online: SwitchCompat,
        editionTitle: TextView,
        customButton: Button,
        pixelFont: TextView,
        pixelLine: ConstraintLayout,
        borderText: TextView,
        borderSpinner: Spinner,
        backgroundText: TextView,
        backgroundSpinner: Spinner,
        duplicateChars: SwitchCompat,
        duplicateEden: SwitchCompat,
        randomEden: SwitchCompat,
        randomButton: Button,
        clearButton: Button,
        tutorialButton: Button,
        returnButton: Button,
        patchButton: Button
    ) {
        val settings = SettingsHandler.readSettings(this)

        val fonts = TextHandler.setFont(this)

        titleText.typeface = fonts["title"]

        groupPrompt.typeface = fonts["body"]
        groupEntry.typeface = fonts["body"]
        groupExplain.typeface = fonts["body"]

        online.typeface = fonts["body"]

        editionTitle.typeface = fonts["body"]
        customButton.typeface = fonts["body"]

        borderText.typeface = fonts["body"]
        backgroundText.typeface = fonts["body"]

        clearButton.typeface = fonts["body"]

        randomButton.typeface = fonts["body"]
        duplicateChars.typeface = fonts["body"]
        duplicateEden.typeface = fonts["body"]
        randomEden.typeface = fonts["body"]

        pixelFont.typeface = TextHandler.updateRetroFont(this, fonts)["body"]

        val bodySize = if ((pixelFont.typeface == ResourcesCompat.getFont(
                this, R.font.four_souls_pixel
            ))
        ) {
            12f
        } else {
            18f
        }
        pixelFont.setTextSize(TypedValue.COMPLEX_UNIT_DIP, bodySize)

        tutorialButton.typeface = fonts["body"]

        returnButton.typeface = fonts["body"]

        patchButton.typeface = fonts["body"]
        // Update all the static fonts

        val setRecycler = findViewById<RecyclerView>(R.id.iconRecycler)
        val iconList = SettingsHandler.getSets()
        val setAdapter =
            SetSelectionAdapter(iconList, fonts["body"]!!, pixelLine, customButton)
        setRecycler.adapter = setAdapter
        setRecycler.layoutManager = GridLayoutManager(this, 4)
        // Lay the recycler out as a grid

        val spinnerItems = SettingsHandler.getBackground(this)
        // Gets the spinner items from the settings

        val borderAdapter = DropDownAdapter(this, borderList.keys.toTypedArray(), fonts["body"]!!)
        borderSpinner.adapter = borderAdapter
        borderSpinner.setSelection(borderAdapter.getPosition(spinnerItems["border"]), false)
        // Update the border dropdown adapters while keeping the same border

        val backgroundAdapter =
            DropDownAdapter(this, backgroundList.keys.toTypedArray(), fonts["body"]!!)
        backgroundSpinner.adapter = backgroundAdapter
        backgroundSpinner.setSelection(
            backgroundAdapter.getPosition(spinnerItems["background"]), false
        )
        // Update the background dropdown adapters while keeping the same background
        SettingsHandler.saveSettings(this, settings)
    }

    private fun updateSave(
        easyFont: SwitchCompat,
        pixelSwitch: SwitchCompat,
        borderSpinner: Spinner,
        backgroundSpinner: Spinner,
        online: SwitchCompat,
        randomEternal: SwitchCompat,
        duplicateChars: SwitchCompat,
        duplicateEden: SwitchCompat,
        groupID: EditText
    ) {
        val settings = SettingsHandler.readSettings(this)
        settings["readable_font"] = easyFont.isChecked.toString()
        settings["pixel_font"] = pixelSwitch.isChecked.toString()
        settings["border"] = borderList[borderSpinner.selectedItem.toString()]!!
        settings["background"] = backgroundList[backgroundSpinner.selectedItem.toString()]!!
        settings["online"] = online.isChecked.toString()
        settings["groupID"] = groupID.text.toString().uppercase()
        settings["duplicate_characters"] = duplicateChars.isChecked.toString()
        settings["duplicate_eden"] = duplicateEden.isChecked.toString()
        settings["random_eden"] = randomEternal.isChecked.toString()
        // Update the settings map
        SettingsHandler.saveSettings(this, settings)
        // Save the map
    }
}

fun Snackbar.changeFont(font: Typeface)
// Add a snackbar change font function
{
    val tv: TextView = view.findViewById(com.google.android.material.R.id.snackbar_text)
    // Create a textview of the text
    tv.typeface = font
    // Set the typeface to the font
}