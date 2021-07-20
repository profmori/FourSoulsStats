package com.example.foursoulsstatistics

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
import com.example.foursoulsstatistics.custom_adapters.ChangeGroupDialog
import com.example.foursoulsstatistics.custom_adapters.DropDownAdapter
import com.example.foursoulsstatistics.data_handlers.SettingsHandler
import com.example.foursoulsstatistics.data_handlers.TextHandler
import com.example.foursoulsstatistics.database.CharacterList
import com.example.foursoulsstatistics.database.GameDataBase
import com.example.foursoulsstatistics.online_database.OnlineDataHandler
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

        if (!currentSettings["online"].toBoolean()) {
            groupPrompt.visibility = View.GONE
            groupEntry.visibility = View.GONE
            groupExplain.visibility = View.GONE
            // If the player is not uploading hide the  group id entry
        }

        val returnButton = findViewById<Button>(R.id.settingsMainButton)
        // Get the return button

        val titleText = findViewById<TextView>(R.id.settingsTitle)
        // Get the title

        val backgroundImage = findViewById<ImageView>(R.id.background)

        SettingsHandler.updateBackground(this, backgroundImage)

        var fonts = TextHandler.setFont(this)
        // Get the current fonts

        if (titleText.typeface != fonts["title"]) {
            // If they are not already used, use them
            updateFonts(
                gold, plus, requiem, warp, promo, altArt, borderText, borderSpinner,
                backgroundText, backgroundSpinner, returnButton, editionTitle, titleText, online,
                groupPrompt, groupEntry, groupExplain
            )
        }

        borderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // When a border item is selected
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentSettings = updateSave(
                    gold, plus, requiem, warp, promo, altArt, easyFont,
                    borderSpinner, backgroundSpinner, online, groupEntry
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
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentSettings = updateSave(
                    gold, plus, requiem, warp, promo, altArt, easyFont,
                    borderSpinner, backgroundSpinner, online, groupEntry
                )
                // Update the current settings
                SettingsHandler.updateBackground(backgroundSpinner.context, backgroundImage)
                // Update the background image
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        easyFont.setOnCheckedChangeListener { _, _ ->
            // When the font slider is changed
            currentSettings = updateSave(
                gold, plus, requiem, warp, promo, altArt, easyFont,
                borderSpinner, backgroundSpinner, online, groupEntry
            )
            updateFonts(
                gold, plus, requiem, warp, promo, altArt, borderText, borderSpinner,
                backgroundText, backgroundSpinner, returnButton, editionTitle, titleText, online,
                groupPrompt, groupEntry, groupExplain
            )
            // Change all the fonts
        }

        online.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // If the online switch is showing
                groupPrompt.visibility = View.VISIBLE
                groupEntry.visibility = View.VISIBLE
                groupExplain.visibility = View.VISIBLE
                // Make all prompts visible
            } else {
                // If the switch is off
                groupPrompt.visibility = View.GONE
                groupEntry.visibility = View.GONE
                groupExplain.visibility = View.GONE
                // Hide all prompts, and ignore the space
            }
        }

        returnButton.setOnClickListener {
            // When the return button is clicked
            currentSettings = updateSave(
                gold, plus, requiem, warp, promo, altArt, easyFont,
                borderSpinner, backgroundSpinner, online, groupEntry
            )
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
                    dataBase.clearAllTables()
                    for (char in CharacterList.charList) {
                        // Iterates through the characters
                        gameDao.updateCharacter(char)
                        // Add the character, replacing existing versions
                    }
                }
            }
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from", "settings")
            startActivity(backToMain)
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
                        fonts = TextHandler.setFont(this)
                        val entryDialog =
                            ChangeGroupDialog(this, groupEntry, oldId!!, fonts["body"]!!)
                        entryDialog.show(supportFragmentManager, "groupID")

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

    private fun updateSave(gold: SwitchCompat,
                           plus: SwitchCompat,
                           requiem: SwitchCompat,
                           warp: SwitchCompat,
                           promo: SwitchCompat,
                           altArt: SwitchCompat,
                           easyFont: SwitchCompat,
                           borderSpinner: Spinner,
                           backgroundSpinner: Spinner,
                           online: SwitchCompat,
                           groupID: EditText): Map<String, String>{
        val newMap = mapOf(
            "gold" to gold.isChecked.toString(),
            "plus" to plus.isChecked.toString(),
            "requiem" to requiem.isChecked.toString(),
            "warp" to warp.isChecked.toString(),
            "promo" to promo.isChecked.toString(),
            "alt_art" to altArt.isChecked.toString(),
            "readable_font" to easyFont.isChecked.toString(),
            "border" to borderList[borderSpinner.selectedItem.toString()]!!,
            "background" to backgroundList[backgroundSpinner.selectedItem.toString()]!!,
            "online" to online.isChecked.toString(),
            "groupID" to groupID.text.toString().uppercase(),
            "first_open" to "false"
        )
        // Save all the values to a map
        return SettingsHandler.saveToFile(this, newMap)
        // Save the map
    }

    private fun updateFonts(gold: SwitchCompat,
                            plus: SwitchCompat,
                            requiem: SwitchCompat,
                            warp: SwitchCompat,
                            promo: SwitchCompat,
                            altArt: SwitchCompat,
                            borderText: TextView,
                            borderSpinner: Spinner,
                            backgroundText: TextView,
                            backgroundSpinner: Spinner,
                            returnButton: Button,
                            editionTitle: TextView,
                            titleText: TextView,
                            online : SwitchCompat,
                            groupPrompt : TextView,
                            groupEntry: EditText,
                            groupExplain: TextView){
        val fonts = TextHandler.setFont(this)
        gold.typeface = fonts["body"]
        plus.typeface = fonts["body"]
        requiem.typeface = fonts["body"]
        warp.typeface = fonts["body"]
        promo.typeface = fonts["body"]
        altArt.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        editionTitle.typeface = fonts["body"]
        titleText.typeface = fonts["title"]
        borderText.typeface = fonts["body"]
        backgroundText.typeface = fonts["body"]
        online.typeface = fonts["body"]
        groupPrompt.typeface = fonts["body"]
        groupEntry.typeface = fonts["body"]
        groupExplain.typeface = fonts["body"]
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