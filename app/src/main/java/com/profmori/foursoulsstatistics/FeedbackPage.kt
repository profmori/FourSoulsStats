package com.profmori.foursoulsstatistics

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import java.text.SimpleDateFormat
import java.util.*

class FeedbackPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_page)

        val title = findViewById<TextView>(R.id.feedbackTitle)
        val preferences = findViewById<TextView>(R.id.feedbackPreference)
        val github = findViewById<Button>(R.id.githubFeedback)
        val bugReport = findViewById<Button>(R.id.emailBug)
        val suggestion = findViewById<Button>(R.id.emailSuggestion)
        val translate = findViewById<Button>(R.id.emailTranslate)
        val returnButton = findViewById<Button>(R.id.feedbackMainButton)
        // Get all the main foreground elements

        val background = findViewById<ImageView>(R.id.background)
        // Gets the background image view
        SettingsHandler.updateBackground(this, background)
        // Updates the background

        val fonts = TextHandler.setFont(this)
        // Get the right font type (readable or not)

        title.typeface =fonts["title"]
        preferences.typeface =fonts["body"]
        github.typeface =fonts["body"]
        bugReport.typeface =fonts["body"]
        suggestion.typeface =fonts["body"]
        translate.typeface =fonts["body"]
        returnButton.typeface =fonts["body"]
        // Sets all the fonts correctly

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        github.setBackgroundResource(buttonBG)
        bugReport.setBackgroundResource(buttonBG)
        suggestion.setBackgroundResource(buttonBG)
        translate.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to look correct

        github.setOnClickListener {
            val githubIssues = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/profmori/FourSoulsStats/issues"))
            // Create an intent for the github link
            startActivity(githubIssues)
            // Actually go to it
        }

        bugReport.setOnClickListener {
            prepareEmail("Bug Report")
        }

        suggestion.setOnClickListener {
            prepareEmail("Suggestion")
        }

        translate.setOnClickListener {
            prepareEmail("Translation Offer")
        }

        returnButton.setOnClickListener {
            val backToMain = Intent(this, MainActivity::class.java)
            // Create an intent back to the main screen
            backToMain.putExtra("from", "feedback")
            startActivity(backToMain)
        }
    }

    private fun prepareEmail(emailType: String){
        val date = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(System.currentTimeMillis())
        val subject = "$emailType $date"
        val email = Intent(Intent.ACTION_VIEW,
            Uri.parse("mailto:foursoulsstats@gmail.com?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(""))
        )
        startActivity(email)
    }
}