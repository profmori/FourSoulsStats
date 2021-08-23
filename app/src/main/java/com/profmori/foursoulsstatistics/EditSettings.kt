package com.profmori.foursoulsstatistics

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
import com.profmori.foursoulsstatistics.custom_adapters.ChangeGroupDialog
import com.profmori.foursoulsstatistics.custom_adapters.DropDownAdapter
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharacterList
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import com.google.android.material.snackbar.Snackbar
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
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
        
        var firstOpen = currentSettings["first_open"].toBoolean()

        if (firstOpen){
            runTutorial()
            firstOpen = false
        }

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
            existingIds = OnlineDataHandler.getGroupIDs()
            // Gets the existing ids in the online database
        }

        if(!currentSettings["custom"].toBoolean()){
            customButton.visibility = View.GONE
        }
        // If custom cards are not being used, hide the input button


        val returnButton = findViewById<Button>(R.id.settingsMainButton)
        // Get the return button

        val titleText = findViewById<TextView>(R.id.settingsTitle)
        // Get the title

        val backgroundImage = findViewById<ImageView>(R.id.background)
        // Get the background image
        SettingsHandler.updateBackground(this, backgroundImage)
        // Update it

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        customButton.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        updateFonts(titleText, groupPrompt, groupEntry, groupExplain, online, editionTitle, gold,
            plus, requiem, warp, promo, custom, customButton, altArt, borderText, borderSpinner,
            backgroundText, backgroundSpinner, returnButton)
        // Use them

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
                    borderSpinner, backgroundSpinner, online, groupEntry, firstOpen)
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
                    borderSpinner, backgroundSpinner, online, groupEntry, firstOpen)
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
                borderSpinner, backgroundSpinner, online, groupEntry, firstOpen)
            updateFonts(titleText, groupPrompt, groupEntry, groupExplain, online, editionTitle, gold,
                plus, requiem, warp, promo, custom, customButton, altArt, borderText, borderSpinner,
                backgroundText, backgroundSpinner, returnButton)
            // Change all the fonts
        }

        custom.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked) {customButton.visibility = View.VISIBLE}
            // Show the custom entry button if custom cards are enabled
            else {customButton.visibility = View.GONE}
            // Hide it otherwise
        }

        customButton.setOnClickListener {
            updateSave(
                gold, plus, requiem, warp, promo, custom, altArt, easyFont,
                borderSpinner, backgroundSpinner, online, groupEntry, firstOpen)
            val customIntent = Intent(this, CustomCardEntry::class.java)
            startActivity(customIntent)
        }

        returnButton.setOnClickListener {
            // When the return button is clicked
            currentSettings = updateSave(
                gold, plus, requiem, warp, promo, custom, altArt, easyFont,
                borderSpinner, backgroundSpinner, online, groupEntry, firstOpen)
            // Save the new settings file
            val newID = groupEntry.text.toString().uppercase()
            if (newID !in existingIds) {
                OnlineDataHandler.saveGroupID(newID)
                // Save the group id online
            }
            if (newID != oldId) {
                val dataBase = GameDataBase.getDataBase(this)
                val gameDao = dataBase.gameDAO
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
                    val id = groupEntry.text.toString().uppercase()
                    if(id.contains('O',true)){
                        groupEntry.setText(id.replace('O','0',true))
                    }
                    if (groupEntry.text.toString().uppercase() in existingIds) {
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

                    if (groupEntry.text.toString().uppercase() != oldId) {
                        val fonts = TextHandler.setFont(this)
                        val entryDialog =
                            ChangeGroupDialog(groupEntry, oldId!!, fonts["body"]!!)
                        entryDialog.show(supportFragmentManager, "groupID")
                        // Create and show the confirmation to change the group ID

                    }
                }
            }
        }
    }

    private fun runTutorial(){

        val config = ShowcaseConfig()
        config.delay = 100
        // Delay between each showcase view

        val editionSelect = findViewById<ConstraintLayout>(R.id.editionSelect)
        // Get editions area

        val altArt = findViewById<SwitchCompat>(R.id.altSwitch)
        // Get alt art switch

        val easyFont = findViewById<SwitchCompat>(R.id.readableSwitch)
        // Match readability switch

        val borderLine = findViewById<TextView>(R.id.borderPrompt)
        // Get the border line

        val backgroundLine = findViewById<TextView>(R.id.backgroundPrompt)
        // Get the background line

        val online = findViewById<SwitchCompat>(R.id.onlineSwitch)
        // Match the online saving behaviour

        val groupLine = findViewById<TextView>(R.id.groupIDExplanation)
        // Gets the group id input

        val sequence = MaterialShowcaseSequence(this, System.currentTimeMillis().toString())

        sequence.setConfig(config)

        val edition = MaterialShowcaseView.Builder(this)
            .setTarget(editionSelect)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_edition))
            .withRectangleShape(true)
            .build()

        val altSwitch = MaterialShowcaseView.Builder(this)
            .setTarget(altArt)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.alt_art))
            .withRectangleShape(true)
            .build()

        val easySwitch = MaterialShowcaseView.Builder(this)
            .setTarget(easyFont)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_readable_font))
            .withRectangleShape(true)
            .build()

        val border = MaterialShowcaseView.Builder(this)
            .setTarget(borderLine)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_border))
            .withRectangleShape(true)
            .build()

        val background = MaterialShowcaseView.Builder(this)
            .setTarget(backgroundLine)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_background))
            .withRectangleShape(true)
            .build()

        val onlineSwitch = MaterialShowcaseView.Builder(this)
            .setTarget(online)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_online))
            .withRectangleShape(true)
            .build()

        val groupID = MaterialShowcaseView.Builder(this)
            .setTarget(groupLine)
            .setDismissText(resources.getString(R.string.tutorial_dismiss))
            .setContentText(resources.getString(R.string.tutorial_group_id))
            .withRectangleShape(true)
            .build()


        sequence.addSequenceItem(groupID)
        sequence.addSequenceItem(onlineSwitch)
        sequence.addSequenceItem(edition)
        sequence.addSequenceItem(altSwitch)
        sequence.addSequenceItem(border)
        sequence.addSequenceItem(background)
        sequence.addSequenceItem(easySwitch)
        // Put the tutorial sequence together
        sequence.start()
        // Run the tutorial
    }

    override fun onBackPressed() {
        val returnButton = findViewById<Button>(R.id.settingsMainButton)
        // Get the return button
        returnButton.performClick()
        // Clicks the button
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
                           groupID: EditText,
                           firstOpen: Boolean): Map<String, String>{
        val newMap = mapOf(
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
            "groupID" to groupID.text.toString().uppercase(),
            "first_open" to firstOpen.toString()
        )
        // Save all the values to a map
        return SettingsHandler.saveToFile(this, newMap)
        // Save the map
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
}

fun Snackbar.changeFont(font: Typeface)
// Add a snackbar change font function
{
    val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    // Create a textview of the text
    tv.typeface = font
    // Set the typeface to the font
}