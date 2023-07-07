package com.profmori.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
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
        val thanksEdmund = findViewById<TextView>(R.id.thanksEdmund)
        val thanksCards = findViewById<TextView>(R.id.thanksCards)
        // Get all the main elements

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image view
        SettingsHandler.updateBackground(this, background)
        // Updates the background

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not

        titleText.typeface = fonts["title"]
        returnButton.typeface = fonts["body"]
        thanksFont.typeface = fonts["body"]
        thanksBG.typeface = fonts["body"]
        thanksFriends.typeface = fonts["body"]
        thanksCards.typeface = fonts["body"]
        thanksEdmund.typeface = fonts["body"]
        // Update the fonts

        thanksFont.movementMethod = LinkMovementMethod.getInstance()
        thanksBG.movementMethod = LinkMovementMethod.getInstance()
        thanksEdmund.movementMethod = LinkMovementMethod.getInstance()
        thanksCards.movementMethod = LinkMovementMethod.getInstance()
        // Allows the hyperlinks to be followed

        returnButton.setOnClickListener {
            // When the return button is clicked
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from", "thanks")
            // Tell the main screen you are coming from the thanks page
            startActivity(backToMain)
            // Go to the main page
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // When the back arrow is pressed
                returnButton.performClick()
                // Clicks the return button
            }
        })
    }
}