package com.profmori.foursoulsstatistics

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
        val supportMe = findViewById<TextView>(R.id.thanksSupport)
        val kofiButton = findViewById<Button>(R.id.kofiButton)
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
        supportMe.typeface = fonts["body"]
        // Update the fonts

        thanksFont.movementMethod = LinkMovementMethod.getInstance()
        thanksBG.movementMethod = LinkMovementMethod.getInstance()
        // Allows the hyperlinks to be followed

        kofiButton.setOnClickListener {
            val kofiLink = Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/I2I36755M"))
            startActivity(kofiLink)
        }


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