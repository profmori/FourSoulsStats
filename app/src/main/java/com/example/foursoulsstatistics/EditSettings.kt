package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class EditSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_settings)

        val currentSettings = SettingsHandler.readSettings(this)

        val gold = findViewById<SwitchCompat>(R.id.goldSwitch)
        gold.isChecked = currentSettings["gold"] == true

        val plus = findViewById<SwitchCompat>(R.id.plusSwitch)
        plus.isChecked = currentSettings["plus"] == true

        val requiem = findViewById<SwitchCompat>(R.id.requiemSwitch)
        requiem.isChecked = currentSettings["requiem"] == true

        val warp = findViewById<SwitchCompat>(R.id.warpSwitch)
        warp.isChecked = currentSettings["warp"] == true

        val altArt = findViewById<SwitchCompat>(R.id.altSwitch)
        altArt.isChecked = currentSettings["alt_art"] == true

        val easyFont = findViewById<SwitchCompat>(R.id.readableSwitch)
        easyFont.isChecked = currentSettings["readable_font"] == true

        val returnButton = findViewById<Button>(R.id.settingsMainButton)

        val editionTitle = findViewById<TextView>(R.id.settingsEditionPrompt)

        val titleText = findViewById<TextView>(R.id.settingsTitle)

        val fonts = TextHandler.setFont(this)

        if(titleText.typeface != fonts["title"]){
            updateFonts(gold, plus, requiem, warp, altArt, returnButton, editionTitle, titleText)
        }

        easyFont.setOnCheckedChangeListener { _, _ ->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                updateSave(gold, plus, requiem, warp, altArt, easyFont)
                updateFonts(gold, plus, requiem, warp, altArt, returnButton, editionTitle, titleText)
            }
        }

        returnButton.setOnClickListener {
            updateSave(gold, plus, requiem, warp, altArt, easyFont)
            // Save the new settings file
            finish()
        }
    }

    private fun updateSave(gold: SwitchCompat,
                           plus: SwitchCompat,
                           requiem: SwitchCompat,
                           warp: SwitchCompat,
                           altArt: SwitchCompat,
                           easyFont: SwitchCompat){
        val newMap = mapOf(
            "gold" to gold.isChecked,
            "plus" to plus.isChecked,
            "requiem" to requiem.isChecked,
            "warp" to warp.isChecked,
            "alt_art" to altArt.isChecked,
            "readable_font" to easyFont.isChecked
        )
        SettingsHandler.saveToFile(this, newMap)
    }

    private fun updateFonts(gold: SwitchCompat,
                            plus: SwitchCompat,
                            requiem: SwitchCompat,
                            warp: SwitchCompat,
                            altArt: SwitchCompat,
                            returnButton: Button,
                            editionTitle: TextView,
                            titleText: TextView){
        val fonts = TextHandler.setFont(this)
        gold.typeface = fonts["body"]
        plus.typeface = fonts["body"]
        requiem.typeface = fonts["body"]
        warp.typeface = fonts["body"]
        altArt.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        editionTitle.typeface = fonts["body"]
        titleText.typeface = fonts["title"]
    }
}