package com.profmori.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.custom_adapters.CustomCharDialog
import com.profmori.foursoulsstatistics.custom_adapters.CustomItemDialog
import com.profmori.foursoulsstatistics.custom_adapters.CustomListAdapter
import com.profmori.foursoulsstatistics.data_handlers.TextHandler
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CustomCardEntry : AppCompatActivity() {


    interface confirmInterface {
        fun onTextEntered(text: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_card_entry)

        val fonts = TextHandler.setFont(this)

        val characterRecycler = findViewById<RecyclerView>(R.id.customCharacters)
        val itemRecycler = findViewById<RecyclerView>(R.id.customItems)

        val title = findViewById<TextView>(R.id.customTitle)
        val charTitle = findViewById<TextView>(R.id.customCharacterTitle)
        val charButton = findViewById<Button>(R.id.customAddChar)
        val itemTitle = findViewById<TextView>(R.id.customItemTitle)
        val itemButton = findViewById<Button>(R.id.customAddItem)
        val returnButton = findViewById<Button>(R.id.backToSettings)

        title.typeface = fonts["title"]
        charTitle.typeface = fonts["body"]
        charButton.typeface = fonts["body"]
        itemTitle.typeface = fonts["body"]
        itemButton.typeface = fonts["body"]
        returnButton.typeface = fonts["body"]
        // Set all the fonts

        val dataBase = GameDataBase.getDataBase(this)
        val gameDao = dataBase.gameDAO

        var charAdapter = CustomListAdapter(emptyArray(),fonts["body"]!!)

        var charList = emptyArray<String>()

        CoroutineScope(Dispatchers.IO).launch {
            val characterList = gameDao.getCharacterList("custom")

            charList = characterList.map{charEntity -> charEntity.charName }.toTypedArray()

            charAdapter = CustomListAdapter(charList, fonts["body"]!!)
            // Attach the adapter to the recyclerview to populate items
            characterRecycler.adapter = charAdapter
            // Set layout manager to position the items
            characterRecycler.layoutManager = LinearLayoutManager(this@CustomCardEntry)
            // Lay the recycler out as a list
        }

        val itemList = ItemList.readCustom(this)

        var itemAdapter = CustomListAdapter(itemList, fonts["body"]!!)
        // Attach the adapter to the recyclerview to populate items
        itemRecycler.adapter = itemAdapter
        // Set layout manager to position the items
        itemRecycler.layoutManager = LinearLayoutManager(this)
        // Lay the recycler out as a list

        charButton.setOnClickListener {
            var chars = charAdapter.getItems()
            // Get the current list of characters
            val returnInterface = object : confirmInterface{
                override fun onTextEntered(text: String) {
                    chars += arrayOf(text.lowercase().trim())
                    // Add the new item
                    chars.sort()
                    charAdapter = CustomListAdapter(chars, fonts["body"]!!)
                    // Attach the adapter to the recyclerview to populate items
                    characterRecycler.adapter = charAdapter
                    // Set layout manager to position the items
                }
            }
            val entryDialog =
                CustomCharDialog(this, returnInterface, fonts["body"]!!)
            entryDialog.show(supportFragmentManager, "newChar")
        }

        itemButton.setOnClickListener {
            var items = itemAdapter.getItems()
            // Get the current list of characters
            val returnInterface = object : confirmInterface{
                override fun onTextEntered(text: String) {
                    items += arrayOf(text.lowercase().trim())
                    // Add the new item
                    items.sort()
                    itemAdapter = CustomListAdapter(items, fonts["body"]!!)
                    // Attach the adapter to the recyclerview to populate items
                    itemRecycler.adapter = itemAdapter
                    // Set layout manager to position the items
                }
            }
            val entryDialog =
                CustomItemDialog(this, returnInterface, fonts["body"]!!)
            entryDialog.show(supportFragmentManager, "newItem")
        }

        returnButton.setOnClickListener {
            val chars = charAdapter.getItems().toMutableList()
            // Get the current list of characters
            val items = itemAdapter.getItems()
            // Get the current list of characters
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
                        gameDao.deleteCharacter(CharEntity(
                            it,
                            R.drawable.blank_char,
                            null,
                            "custom"
                        ))
                        // Remove it from the database
                    }
                }
                chars.forEach {
                // For all the remaining characters
                    gameDao.addCharacter(
                        CharEntity(
                        it,
                        R.drawable.blank_char,
                        null,
                        "custom"
                    ))
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