package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

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
        warp.isChecked = currentSettings["promo"].toBoolean()
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

        val returnButton = findViewById<Button>(R.id.settingsMainButton)
        // Get the return button

        val titleText = findViewById<TextView>(R.id.settingsTitle)
        // Get the title

        val backgroundImage = findViewById<ImageView>(R.id.background)

        SettingsHandler.updateBackground(this, backgroundImage)

        val fonts = TextHandler.setFont(this)
        // Get the current fonts

        if(titleText.typeface != fonts["title"]){
        // If they are not already used, use them
            updateFonts(gold, plus, requiem, warp, promo, altArt, borderText, borderSpinner, backgroundText, backgroundSpinner, returnButton, editionTitle, titleText)
        }

        borderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentSettings = updateSave(gold,plus, requiem, warp, promo, altArt, easyFont, borderSpinner, backgroundSpinner)
                SettingsHandler.updateBackground(borderSpinner.context, backgroundImage)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        backgroundSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                backgroundSpinner.setSelection(position)
                currentSettings = updateSave(gold,plus, requiem, warp, promo, altArt, easyFont, borderSpinner, backgroundSpinner)
                SettingsHandler.updateBackground(backgroundSpinner.context, backgroundImage)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        easyFont.setOnCheckedChangeListener { _, _ ->
        // When the font slider is changed
            currentSettings = updateSave(gold, plus, requiem, warp, promo, altArt, easyFont, borderSpinner, backgroundSpinner)
            updateFonts(gold, plus, requiem, warp, promo, altArt, borderText, borderSpinner, backgroundText, backgroundSpinner, returnButton, editionTitle, titleText)
            // Change all the fonts
        }

        returnButton.setOnClickListener {
        // When the return button is clicked
            updateSave(gold, plus, requiem, warp, promo, altArt, easyFont, borderSpinner, backgroundSpinner)
            // Save the new settings file
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from","settings")
            startActivity(backToMain)
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
                            backgroundSpinner: Spinner): Map<String, String>{
        val newMap = mapOf(
            "gold" to gold.isChecked.toString(),
            "plus" to plus.isChecked.toString(),
            "requiem" to requiem.isChecked.toString(),
            "warp" to warp.isChecked.toString(),
            "promo" to promo.isChecked.toString(),
            "alt_art" to altArt.isChecked.toString(),
            "readable_font" to easyFont.isChecked.toString(),
            "border" to borderList[borderSpinner.selectedItem.toString()]!!,
            "background" to backgroundList[backgroundSpinner.selectedItem.toString()]!!
        )
        println(newMap)
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
                            titleText: TextView){
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