package com.profmori.foursoulsstatistics

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.custom_adapters.RerollSelectionAdapter
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import java.text.SimpleDateFormat
import java.util.Locale


class RerollPrefSelect : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reroll_pref_select)

        val fonts = TextHandler.setFont(this)
        // Set the fonts

        val iconRecycler = findViewById<RecyclerView>(R.id.randomisePrefList)
        // Get the recycler view

        val title = findViewById<TextView>(R.id.randomisePrefTitle)
        val suggestButton = findViewById<Button>(R.id.randomiseSuggestButton)
        val returnButton = findViewById<Button>(R.id.randomiseBackButton)
        // get all the used buttons

        title.typeface = fonts["title"]
        suggestButton.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        // Set all the fonts

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        suggestButton.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        val background = findViewById<ImageView>(R.id.background)
        SettingsHandler.updateBackground(this, background)
        // Set the background image

        val iconList = ImageHandler.readRerollFile(this)
        // Get the list of reroll icons with their preferences

        val rerollAdapter = RerollSelectionAdapter(iconList.keys.toTypedArray(), iconList, this)
        // Create the list adapter
        iconRecycler.adapter = rerollAdapter
        iconRecycler.layoutManager = GridLayoutManager(this, 4)
        // Lay the recycler out as a grid with width 4


        returnButton.setOnClickListener {
            // When the button to go back to settings is clicked
            ImageHandler.writeRerollFile(this, iconList)
            // Save all the reroll icon preferences
            val backToSettings = Intent(this, EditSettings::class.java)
            startActivity(backToSettings)
            // Go back to the settings page
        }

        suggestButton.setOnClickListener {
            // When the button to suggest an icon is pressed
            ImageHandler.writeRerollFile(this, iconList)
            // Save all the reroll icon preferences
            val date =
                SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(System.currentTimeMillis())
            // Get the current date in the form day/month/year
            val subject = "Randomise Icon Suggestion $date"
            // Create the subject line
            val email = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "mailto:foursoulsstats@gmail.com?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(
                        ""
                    )
                )
            )
            // Makes an intent to send the email
            startActivity(email)
            // Set up the email screen
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