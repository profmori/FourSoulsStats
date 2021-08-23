package com.profmori.foursoulsstatistics

import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.custom_adapters.CustomCharDialog
import com.profmori.foursoulsstatistics.custom_adapters.CustomCharListAdapter
import com.profmori.foursoulsstatistics.custom_adapters.CustomItemDialog
import com.profmori.foursoulsstatistics.custom_adapters.CustomItemListAdapter
import com.profmori.foursoulsstatistics.data_handlers.ImageHandler
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CustomCardEntry : AppCompatActivity() {


    interface ConfirmInterface {
        fun onTextEntered(text: String)
        // Create an interface which runs an editable function
    }

    lateinit var imageChangeButton: Button
    // Allows the invisible test button to be set outside this class
    var currentChar = -1
    // Sets the current character being edited to -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_card_entry)

        val fonts = TextHandler.setFont(this)
        // Set the fonts

        val characterRecycler = findViewById<RecyclerView>(R.id.customCharacters)
        val itemRecycler = findViewById<RecyclerView>(R.id.customItems)
        // Get the two list recycler views

        val title = findViewById<TextView>(R.id.customTitle)
        val charTitle = findViewById<TextView>(R.id.customCharacterTitle)
        val charButton = findViewById<Button>(R.id.customAddChar)
        val itemTitle = findViewById<TextView>(R.id.customItemTitle)
        val itemButton = findViewById<Button>(R.id.customAddItem)
        val returnButton = findViewById<Button>(R.id.backToSettings)
        // get all the used buttons

        imageChangeButton = findViewById(R.id.customImageChange)
        imageChangeButton.visibility = View.GONE
        // Get and hide the image change button

        title.typeface = fonts["title"]
        charTitle.typeface = fonts["body"]
        charButton.typeface = fonts["body"]
        itemTitle.typeface = fonts["body"]
        itemButton.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        // Set all the fonts

        val buttonBG = ImageHandler.setButtonImage()
        // Get a random button from the possible options

        charButton.setBackgroundResource(buttonBG)
        itemButton.setBackgroundResource(buttonBG)
        returnButton.setBackgroundResource(buttonBG)
        // Set all the buttons to the same background

        val background = findViewById<ImageView>(R.id.background)
        SettingsHandler.updateBackground(this, background)
        // Set the background image

        val dataBase = GameDataBase.getDataBase(this)
        val gameDao = dataBase.gameDAO
        // Access the database

        var charAdapter = CustomCharListAdapter(emptyArray(),fonts["body"]!!,this)
        // Create the list adapter
        var charList = emptyArray<CharEntity>()
        // Create the empty custom character list

        CoroutineScope(Dispatchers.IO).launch {
            charList = gameDao.getCharacterList("custom")
            // Get the custom characters

            charAdapter = CustomCharListAdapter(charList, fonts["body"]!!,this@CustomCardEntry)
            // Attach the adapter to the recyclerview to populate items
            characterRecycler.adapter = charAdapter
            // Set layout manager to position the items
            characterRecycler.layoutManager = LinearLayoutManager(this@CustomCardEntry)
            // Lay the recycler out as a list
        }

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {imageURI ->
            val chars = charAdapter.getItems().toMutableList()
            // Get the current list of characters
            try {
                val image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(this.contentResolver, imageURI)
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(this.contentResolver, imageURI)
                }
                // Get the image from the URI

                ImageHandler.writeImage(this, chars[currentChar].charName, image)
                // Store the image in local storage

                chars += arrayOf(
                    CharEntity(
                        chars[currentChar].charName,
                        -1,
                        null,
                        "custom"
                    )
                )
                // Add the character to the end of the list
                chars.removeAt(currentChar)
                // Remove the old version
                charAdapter = CustomCharListAdapter(
                    chars.toTypedArray(),
                    fonts["body"]!!,
                    this@CustomCardEntry
                )
                // Attach the adapter to the recyclerview to populate items
                characterRecycler.adapter = charAdapter
                // Set layout manager to position the items
            }catch (e: NullPointerException) {}
        }

        imageChangeButton.setOnClickListener {
            getContent.launch("image/*")
            // Launch a call of an image
        }

        val itemList = ItemList.readCustom(this)
        var itemAdapter = CustomItemListAdapter(itemList, fonts["body"]!!)
        // Attach the adapter to the recyclerview to populate items
        itemRecycler.adapter = itemAdapter
        // Set layout manager to position the items
        itemRecycler.layoutManager = LinearLayoutManager(this)
        // Lay the recycler out as a list

        charButton.setOnClickListener {
            var chars = charAdapter.getItems()
            // Get the current list of characters
            val returnInterface = object : ConfirmInterface{
                override fun onTextEntered(text: String) {
                    chars += arrayOf(CharEntity(
                        text.lowercase().trim(),
                        R.drawable.blank_char,
                        null,
                        "custom"
                    ))
                    // Add the new item
                    charAdapter = CustomCharListAdapter(chars, fonts["body"]!!,this@CustomCardEntry)
                    // Attach the adapter to the recyclerview to populate items
                    characterRecycler.adapter = charAdapter
                    // Set layout manager to position the items
                }
            }
            val entryDialog =
                CustomCharDialog(returnInterface, fonts["body"]!!)
            entryDialog.show(supportFragmentManager, "newChar")
            // Create and show the new character input
        }

        itemButton.setOnClickListener {
            var items = itemAdapter.getItems()
            // Get the current list of items
            val returnInterface = object : ConfirmInterface{
                override fun onTextEntered(text: String) {
                    items += arrayOf(text.lowercase().trim())
                    // Add the new item
                    items.sort()
                    itemAdapter = CustomItemListAdapter(items, fonts["body"]!!)
                    // Attach the adapter to the recyclerview to populate items
                    itemRecycler.adapter = itemAdapter
                    // Set layout manager to position the items
                }
            }
            val entryDialog =
                CustomItemDialog(returnInterface, fonts["body"]!!)
            entryDialog.show(supportFragmentManager, "newItem")
            // Create and show the new item input
        }


        returnButton.setOnClickListener {
            val chars = charAdapter.getItems().toMutableList()
            // Get the current list of characters
            val items = itemAdapter.getItems()
            // Get the current list of items
            ItemList.writeCustom(this,items)

            CoroutineScope(Dispatchers.IO).launch {
                charList.forEach {
                // Iterate through all the current custom characters
                    if (chars.contains(it)){
                    // If it hasn't been removed
                        val charPos = chars.indexOf(it)
                        chars.removeAt(charPos)
                        // Remove it from the character list
                    }
                    else{
                        gameDao.deleteCharacter(it)
                        // Remove it from the database
                    }
                }
                chars.forEach {
                // For all the remaining characters
                    gameDao.addCharacter(it)
                    // Add the character to the database
                }
            }
            val backToSettings = Intent(this, EditSettings::class.java)
            startActivity(backToSettings)
            // Go back to the settings page
        }
    }

    override fun onBackPressed() {
        // When the back arrow is pressed
        val returnButton = findViewById<Button>(R.id.backToSettings)
        returnButton.performClick()
        // Do everything that would happen from pressing the actual button
    }
}