package com.profmori.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler

class ShowThanks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_thanks)

        val titleText = findViewById<TextView>(R.id.thanksTitle)
        val returnButton = findViewById<Button>(R.id.thanksMainButton)
        val thanksFont = findViewById<TextView>(R.id.thanksFont)
        val thanksBG = findViewById<TextView>(R.id.thanksBackgrounds)
        val thanksFriends = findViewById<TextView>(R.id.thanksFriends)
        // Get all the main elements


        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image view
        SettingsHandler.updateBackground(this, background)
        // Updates the background

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not

        if (titleText.typeface != fonts["title"]){
            // If the fonts are wrong
            titleText.typeface = fonts["title"]
            returnButton.typeface = fonts["body"]
            thanksFont.typeface = fonts["body"]
            thanksBG.typeface = fonts["body"]
            thanksFriends.typeface = fonts["body"]
            // Update them
        }

        thanksFont.movementMethod = LinkMovementMethod.getInstance()
        thanksBG.movementMethod = LinkMovementMethod.getInstance()
        // Allows the hyperlinks to be followed

        returnButton.setOnClickListener {
            // When the return button is clicked
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from", "thanks")
            startActivity(backToMain)
        }
    }

    override fun onBackPressed() {
        val returnButton = findViewById<Button>(R.id.thanksMainButton)
        // Get the return button
        returnButton.performClick()
        // Clicks the button
    }
}