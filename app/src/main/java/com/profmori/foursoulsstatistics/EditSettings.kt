package com.profmori.foursoulsstatistics

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.profmori.foursoulsstatistics.custom_adapters.dialogs.ChangeGroupDialog
import com.profmori.foursoulsstatistics.custom_adapters.dialogs.ConfirmDeleteDialog
import com.profmori.foursoulsstatistics.custom_adapters.DropDownAdapter
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig

class EditSettings : AppCompatActivity() {

    private var borderList = emptyMap<String, String>()
    // Initialises the border list

    private var backgroundList = emptyMap<String, String>()
    // Initialises the background list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_settings)

        var currentSettings = SettingsHandler.readSettings(this)
        // Gets the current settings

        borderList = mapOf(
            resources.getString(R.string.character_back) to "character_back",
            resources.getString(R.string.eternal_back) to "eternal_back",
            resources.getString(R.string.loot_back) to "loot_back",
            resources.getString(R.string.monster_back) to "monster_back",
            resources.getString(R.string.soul_back) to "soul_back",
            resources.getString(R.string.treasure_back) to "treasure_back"
        )
        // Lists all the border options and their save strings

        backgroundList = mapOf(
            resources.getString(R.string.character_back) to "character_back",
            resources.getString(R.string.eternal_back) to "eternal_back",
            resources.getString(R.string.loot_back) to "loot_back",
            resources.getString(R.string.treasure_back) to "treasure_back"
        )
        // Lists all the background options and their save strings

        val editionTitle = findViewById<TextView>(R.id.settingsEditionPrompt)
        // Gets the main edition prompt

        val gold = findViewById<SwitchCompat>(R.id.goldSwitch)
        gold.isChecked = currentSettings["gold"].toBoolean()
        // Set the gold box switch to match current settings

        val plus = findViewById<SwitchCompat>(R.id.plusSwitch)
        plus.isChecked = currentSettings["plus"].toBoolean()
        // Set the FS+ switch to match current settings

        val requiem = findViewById<SwitchCompat>(R.id.requiemSwitch)
        requiem.isChecked = currentSettings["requiem"].toBoolean()
        // Match requiem settings

        val warp = findViewById<SwitchCompat>(R.id.warpSwitch)
        warp.isChecked = currentSettings["warp"].toBoolean()
        // Match warp zone settings

        val promo = findViewById<SwitchCompat>(R.id.promoSwitch)
        promo.isChecked = currentSettings["promo"].toBoolean()
        // Match promo settings

        val custom = findViewById<SwitchCompat>(R.id.customSwitch)
        custom.isChecked = currentSettings["custom"].toBoolean()
        val customButton = findViewById<Button>(R.id.customButton)

        val altArt = findViewById<SwitchCompat>(R.id.altSwitch)
        altArt.isChecked = currentSettings["alt_art"].toBoolean()
        // Match alt art settings

        val easyFont = findViewById<SwitchCompat>(R.id.readableSwitch)
        easyFont.isChecked = currentSettings["readable_font"].toBoolean()
        // Match readability switch

        val borderText = findViewById<TextView>(R.id.borderPrompt)
        val borderSpinner = findViewById<Spinner>(R.id.borderSpinner)
        // Get the border line

        val backgroundText = findViewById<TextView>(R.id.backgroundPrompt)
        val backgroundSpinner = findViewById<Spinner>(R.id.backgroundSpinner)
        // Get the background line

        val online = findViewById<SwitchCompat>(R.id.onlineSwitch)
        online.isChecked = currentSettings["online"].toBoolean()
        // Match the online saving behaviour

        val groupPrompt = findViewById<TextView>(R.id.groupIDPrompt)
        val groupEntry = findViewById<EditText>(R.id.groupIDEntry)
        val groupExplain = findViewById<TextView>(R.id.groupIDExplanation)
        // Gets the group id prompts

        val oldId = currentSettings["groupID"]
        groupEntry.setText(oldId)
        // Get the current id and set the group entry to the current group id
        var existingIds = emptyArray<String>()
        // Initialises as an empty array
        CoroutineScope(Dispatchers.IO).launch {
            // Running asynchronously
            existingIds = OnlineDataHandler.getGroupIDs(this@EditSettings)
            // Gets the existing ids in the online database
        }

        if(!currentSettings["custom"].toBoolean()){
            customButton.visibility = View.GONE
        }
        // If custom cards are not being used, hide the input button

        val returnButton = findViewById<Button>(R.id.settingsMainButton)
        // Get the return button

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

        customButton.setBackgroundResource(buttonBG)
        clearButton.setBackgroundResource(buttonBG)
        tutorialButton.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        updateFonts(titleText, groupPrompt, groupEntry, groupExplain, online, editionTitle, gold,
            plus, requiem, warp, promo, custom, customButton, altArt, borderText, borderSpinner,
            backgroundText, backgroundSpinner, clearButton, tutorialButton, returnButton)
        // Update the fonts for every item

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        // Finds the scrolling view

        scrollView.scrollTo(0,0)
        // Move to the top of the scroll view when the settings page opens

        runTutorial()
        // Run the tutorial

        borderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // When a border item is selected
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentSettings = updateSave(
                    gold, plus, requiem, warp, promo, custom, altArt, easyFont,
                    borderSpinner, backgroundSpinner, online, groupEntry)
                // Update the current settings
                SettingsHandler.updateBackground(borderSpinner.context, backgroundImage)
                // Update the background image
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        backgroundSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // When a background is selected
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentSettings = updateSave(
                    gold, plus, requiem, warp, promo, custom, altArt, easyFont,
                    borderSpinner, backgroundSpinner, online, groupEntry)
                // Update the current settings
                SettingsHandler.updateBackground(backgroundSpinner.context, backgroundImage)
                // Update the background image
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        easyFont.setOnCheckedChangeListener { _, _ ->
            // When the font slider is changed
            currentSettings = updateSave(
                gold, plus, requiem, warp, promo, custom, altArt, easyFont,
                borderSpinner, backgroundSpinner, online, groupEntry)
            updateFonts(titleText, groupPrompt, groupEntry, groupExplain, online, editionTitle, gold,
                plus, requiem, warp, promo, custom, customButton, altArt, borderText, borderSpinner,
                backgroundText, backgroundSpinner, clearButton, tutorialButton, returnButton)
            // Change all the fonts
        }

        custom.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {customButton.visibility = View.VISIBLE}
            // Show the custom entry button if custom cards are enabled
            else {customButton.visibility = View.GONE}
            // Hide it otherwise
        }

        customButton.setOnClickListener {
            // When the custom cards button is clicked
            updateSave(
                gold, plus, requiem, warp, promo, custom, altArt, easyFont,
                borderSpinner, backgroundSpinner, online, groupEntry)
            // Update the save before moving to the custom page
            val customIntent = Intent(this, CustomCardEntry::class.java)
            startActivity(customIntent)
            // Move to the custom cards page
        }

        clearButton.setOnClickListener {
        // When the button to clear all data is clicked
            val fonts = TextHandler.setFont(this)
            // Get the font for the deletion dialog
            val clearDialog =
                ConfirmDeleteDialog(this, fonts["body"]!!)
            clearDialog.show(supportFragmentManager, "clearData")
            // Create and show the confirmation to change the group ID
        }

        tutorialButton.setOnClickListener {
        // When the tutorial reset is clicked
            MaterialShowcaseView.resetAll(this)
            // Reset all the tutorials so they show again
            returnButton.performClick()
            // Click the return button to save and go back to the main menu
        }


        returnButton.setOnClickListener {
            // When the return button is clicked
            currentSettings = updateSave(
                gold, plus, requiem, warp, promo, custom, altArt, easyFont,
                borderSpinner, backgroundSpinner, online, groupEntry)
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
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                // Get an input method manager
                imm.hideSoftInputFromWindow(view.windowToken,0)
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
                    groupEntry.setText(currentSettings["groupID"])
                    // Reset the text in the edit text
                } else {
                    var id = groupEntry.text.toString().uppercase()
                    id = SettingsHandler.sanitiseGroupID(id)
                    if (id in existingIds) {
                        val existsSnackbar = Snackbar.make(
                            view,
                            R.string.settings_duplicate_group,
                            Snackbar.LENGTH_LONG
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
                        val entryDialog =
                            ChangeGroupDialog(groupEntry, oldId!!, fonts["body"]!!)
                        entryDialog.show(supportFragmentManager, "groupID")
                        // Create and show the confirmation to change the group ID

                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val returnButton = findViewById<Button>(R.id.settingsMainButton)
        // Get the return button
        returnButton.performClick()
        // Clicks the button
    }


    private fun runTutorial(){

        val config = ShowcaseConfig()
        config.delay = 200
        // Delay between each showcase view

        val editionSelect = findViewById<ConstraintLayout>(R.id.editionSelect)
        // Get editions area

        val altArt = findViewById<SwitchCompat>(R.id.altSwitch)
        // Get alt art switch

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

        val tutorialButton = findViewById<Button>(R.id.tutorialButton)
        // Get the button to reset the tutorial

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        // Get the scroll view so the view can be automatically scrolled

        val sequence = MaterialShowcaseSequence(this, "settings")
        // Creates the sequence to store all the tutorial steps

        sequence.setConfig(config)
        // Configures it to the chosen settings

        val edition = MaterialShowcaseView.Builder(this)
            .setTarget(editionSelect)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_edition))
            .withRectangleShape(true)
            .build()
        // Highlights the edition select area

        val altSwitch = MaterialShowcaseView.Builder(this)
            .setTarget(altArt)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.alt_art))
            .withRectangleShape(true)
            .build()
        // Highlights the alt art switch

        val easySwitch = MaterialShowcaseView.Builder(this)
            .setTarget(easyFont)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_readable_font))
            .withRectangleShape(true)
            .build()
        // Highlights the readable font switch

        val border = MaterialShowcaseView.Builder(this)
            .setTarget(borderLine)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_border))
            .withRectangleShape(true)
            .build()
        // Highlights the border select dropdown

        val background = MaterialShowcaseView.Builder(this)
            .setTarget(backgroundLine)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_background))
            .withRectangleShape(true)
            .build()
        // Highlights the background select dropdown

        val onlineSwitch = MaterialShowcaseView.Builder(this)
            .setTarget(online)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_online))
            .withRectangleShape(true)
            .build()
        // Highlights the online connection switch

        val groupID = MaterialShowcaseView.Builder(this)
            .setTarget(groupLine)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_group_id))
            .withRectangleShape(true)
            .build()
        // Highlights the group id input area

        val clearData = MaterialShowcaseView.Builder(this)
            .setTarget(clearButton)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_clear_button))
            .build()
        // Highlights the button to clear all data

        sequence.addSequenceItem(groupID)
        sequence.addSequenceItem(onlineSwitch)
        sequence.addSequenceItem(edition)
        sequence.addSequenceItem(altSwitch)
        sequence.addSequenceItem(border)
        sequence.addSequenceItem(background)
        sequence.addSequenceItem(easySwitch)
        sequence.addSequenceItem(clearData)
        // Put the tutorial sequence together
        sequence.start()
        // Run the tutorial

        sequence.setOnItemDismissedListener { itemView, _ ->
            // When any sequence item is dismissed
            val yPos = when(itemView){
                edition -> altArt.bottom
                altSwitch -> borderLine.bottom
                border -> backgroundLine.bottom
                background -> easyFont.bottom
                easySwitch -> clearButton.bottom
                clearData -> tutorialButton.bottom
                else ->  scrollView.scrollY
            }
            // Get the y position of the next item
            val objectAnimator = ObjectAnimator.ofInt(scrollView, "scrollY",scrollView.scrollY, yPos ).setDuration(500)
            // Scroll to the chosen y position in 500 milliseconds
            objectAnimator.start()
            // Start the animation
        }

    }

    private fun updateFonts(
        titleText: TextView,
        groupPrompt : TextView,
        groupEntry: EditText,
        groupExplain: TextView,
        online : SwitchCompat,
        editionTitle: TextView,
        gold: SwitchCompat,
        plus: SwitchCompat,
        requiem: SwitchCompat,
        warp: SwitchCompat,
        promo: SwitchCompat,
        custom: SwitchCompat,
        customButton: Button,
        altArt: SwitchCompat,
        borderText: TextView,
        borderSpinner: Spinner,
        backgroundText: TextView,
        backgroundSpinner: Spinner,
        clearButton: Button,
        tutorialButton: Button,
        returnButton: Button){
        val fonts = TextHandler.setFont(this)

        titleText.typeface = fonts["title"]

        groupPrompt.typeface = fonts["body"]
        groupEntry.typeface = fonts["body"]
        groupExplain.typeface = fonts["body"]

        online.typeface = fonts["body"]

        editionTitle.typeface = fonts["body"]
        gold.typeface = fonts["body"]
        plus.typeface = fonts["body"]
        requiem.typeface = fonts["body"]
        warp.typeface = fonts["body"]
        promo.typeface = fonts["body"]

        custom.typeface = fonts["body"]
        customButton.typeface = fonts["body"]

        altArt.typeface = fonts["body"]

        borderText.typeface = fonts["body"]
        backgroundText.typeface = fonts["body"]

        clearButton.typeface = fonts["body"]

        tutorialButton.typeface = fonts["body"]

        returnButton.typeface = fonts["body"]
        // Update all the static fonts

        val spinnerItems = SettingsHandler.getBackground(this)
        // Gets the spinner items from the settings

        val borderAdapter = DropDownAdapter(this, borderList.keys.toTypedArray(), fonts["body"]!!)
        borderSpinner.adapter = borderAdapter
        borderSpinner.setSelection(borderAdapter.getPosition(spinnerItems["border"]), false)
        // Update the border dropdown adapters while keeping the same border

        val backgroundAdapter = DropDownAdapter(this, backgroundList.keys.toTypedArray(), fonts["body"]!!)
        backgroundSpinner.adapter = backgroundAdapter
        backgroundSpinner.setSelection(backgroundAdapter.getPosition(spinnerItems["background"]), false)
        // Update the background dropdown adapters while keeping the same background
    }

    private fun updateSave(gold: SwitchCompat,
                           plus: SwitchCompat,
                           requiem: SwitchCompat,
                           warp: SwitchCompat,
                           promo: SwitchCompat,
                           custom: SwitchCompat,
                           altArt: SwitchCompat,
                           easyFont: SwitchCompat,
                           borderSpinner: Spinner,
                           backgroundSpinner: Spinner,
                           online: SwitchCompat,
                           groupID: EditText): MutableMap<String, String>{
        val newMap = mutableMapOf(
            "gold" to gold.isChecked.toString(),
            "plus" to plus.isChecked.toString(),
            "requiem" to requiem.isChecked.toString(),
            "warp" to warp.isChecked.toString(),
            "promo" to promo.isChecked.toString(),
            "custom" to custom.isChecked.toString(),
            "alt_art" to altArt.isChecked.toString(),
            "readable_font" to easyFont.isChecked.toString(),
            "border" to borderList[borderSpinner.selectedItem.toString()]!!,
            "background" to backgroundList[backgroundSpinner.selectedItem.toString()]!!,
            "online" to online.isChecked.toString(),
            "groupID" to groupID.text.toString().uppercase()
        )
        // Save all the values to a map
        return SettingsHandler.saveToFile(this, newMap)
        // Save the map
    }
}

fun Snackbar.changeFont(font: Typeface)
// Add a snackbar change font function
{
    val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    // Create a textview of the text
    tv.typeface = font
    // Set the typeface to the font
}