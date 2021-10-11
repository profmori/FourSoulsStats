package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.runBlocking

class SettingsHandler {

    companion object {
        fun getBackground(context: Context): Map<String, String>{
            val settings = readSettings(context)
            // Read the settings
            val background = setFromKey(context, settings["background"]!!)
            // Set the background text from the key

            val border = setFromKey(context, settings["border"]!!)
            // Set the foreground text from the key

            return mapOf(
                "background" to background,
                "border" to border)
            // Return a map of the text
        }

        fun getEditions(context: Context):Array<String>{
            val settings = readSettings(context)
            // Get the settings
            var editionArray = arrayOf("base")
            // As a default include the base game
            if (settings["gold"].toBoolean()){editionArray += "gold"}
            if (settings["plus"].toBoolean()){editionArray += "plus"}
            if (settings["requiem"].toBoolean()){editionArray += "requiem"}
            if (settings["warp"].toBoolean()){editionArray += "warp"}
            if (settings["promo"].toBoolean()){editionArray += "promo"}
            if (settings["custom"].toBoolean()){editionArray += "custom"}
            // Check every setting and add the right editions
            return editionArray
        }

        private fun initialiseSettings(context: Context) {

            OnlineDataHandler.signIn()
            // Sign into the online database
            val settingsFile = context.getFileStreamPath("settings.txt")
            // Get the settings file

            if(!settingsFile.exists()) {
                // If there is no settings file
                createSettings(context)
            }
        }

        private fun createSettings(context: Context): MutableMap<String, String>{
            val settingsFile = context.getFileStreamPath("settings.txt")
            // Get the settings file
            settingsFile.createNewFile()
            // Create a new file
            var existingIDs = emptyArray<String>()
            runBlocking{ existingIDs = OnlineDataHandler.getGroupIDs(context) }
            // Get any existing ids
            val charPool : List<Char> = ('A'..'Z') + ('0'..'9')
            // Sets the list of possible characters to use
            var randID = (1..6)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
            // Generate a random 6 length string

            randID = sanitiseGroupID(randID)
            // Sanitise the id to remove ambiguous characters

            while (existingIDs.contains(randID)) {
                // If the random string is already an id
                randID = (1..6)
                    .map { kotlin.random.Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString("")
                // Generate a new random 6 length string

                randID = sanitiseGroupID(randID)
                // Sanitise the id to remove ambiguous characters
            }

            val settings = mutableMapOf(
                "groupID" to randID,
                "online" to "true",
                "gold" to "false",
                "plus" to "false",
                "requiem" to "false",
                "warp" to "false",
                "promo" to "false",
                "custom" to "false",
                "alt_art" to "true",
                "readable_font" to "false",
                "background" to "loot_back",
                "border" to "monster_back",
                "first_open" to "true"
            )
            // Set the settings

            saveToFile(context, settings)
            // Save the settings file

            return settings
        }

        fun readSettings(context: Context): MutableMap<String, String> {

            initialiseSettings(context)
            // Create a new settings file if appropriate
            val settingsFile = context.getFileStreamPath("settings.txt")
            // Find the settings file
            val reader = context.openFileInput(settingsFile.name).bufferedReader()
            // Create a file stream input reader
            val settingStrings = reader.readLines()
            // Get the settings as a list of lines

            var settingMap = emptyMap<String, String>().toMutableMap()
            // Make an empty, editable map

            settingStrings.forEach { string ->
                // For every line of the settings file
                val keyVal = string.split(":")
                // Split into key and value at the colon
                settingMap += mutableMapOf(keyVal[0] to keyVal[1])
                // Add it to the map as a key-value pair
            }

            if (settingMap.isEmpty()){
                settingsFile.delete()
                settingMap = createSettings(context)
            }

            return settingMap
            // Return the updated map
        }

        fun sanitiseGroupID(id: String): String{
            var returnID = id
            // Store the old ID to return if it is valid
            if(id.contains('O',true)){
                // If the id contains the letter o
                returnID = id.replace('O','0',true)
            }
            // Get rid of any o characters for 0
            return returnID
            // Return the sanitised ID
        }

        private fun setFromKey(context: Context, key: String): String{
            return when(key){
                "character_back" -> context.resources.getString(R.string.character_back)
                "eternal_back" -> context.resources.getString(R.string.eternal_back)
                "loot_back" -> context. resources.getString(R.string.loot_back)
                "monster_back" -> context.resources.getString(R.string.monster_back)
                "soul_back" -> context.resources.getString(R.string.soul_back)
                "treasure_back" -> context.resources.getString(R.string.treasure_back)
                else -> context. resources.getString(R.string.loot_back)
            }
            // Use a when statement on all the possible text options to match the key
        }

        fun saveToFile(context: Context, settings: MutableMap<String, String>): MutableMap<String, String> {
            // Save the settings given
            val settingsFile = context.getFileStreamPath("settings.txt")
            // Find the settings file

            val keys = settings.keys
            // Get the keys for the settings
            val writer = context.openFileOutput(settingsFile.name, AppCompatActivity.MODE_PRIVATE)
            // Create the file output stream writer

            writer.use { stream ->
                keys.forEach { settingKey ->
                    // For every key in the settings map
                    val settingVal = settings[settingKey]
                    // Get the setting as a string
                    val writeText = "$settingKey:$settingVal\n"
                    // Write it to the text file with no spaces and a new line
                    stream.write(writeText.toByteArray())
                    // Add it to the text file
                }
            }

            return settings
            // Returns the settings saved
        }

        fun updateBackground(context: Context, backgroundView: ImageView){
            val settings = readSettings(context)
            // Read the settings file
            when(settings["background"]){
                "character_back" -> backgroundView.setBackgroundResource(R.drawable.bg_character_back)
                "eternal_back" -> backgroundView.setBackgroundResource(R.drawable.bg_tiled_eternal_back)
                "loot_back" -> backgroundView.setBackgroundResource(R.drawable.bg_tiled_loot_back)
                "treasure_back" -> backgroundView.setBackgroundResource(R.drawable.bg_tiled_treasure_back)
            }
            // Set the background from the settings file

            when(settings["border"]){
                "character_back" -> backgroundView.setImageResource(R.drawable.bg_border_character_back)
                "eternal_back" -> backgroundView.setImageResource(R.drawable.bg_border_eternal_back)
                "loot_back" -> backgroundView.setImageResource(R.drawable.bg_border_loot_back)
                "monster_back" -> backgroundView.setImageResource(R.drawable.bg_border_monster_back)
                "soul_back" -> backgroundView.setImageResource(R.drawable.bg_border_soul_back)
                "treasure_back" -> backgroundView.setImageResource(R.drawable.bg_border_treasure_back)
            }
            // Set the border from the settings file

            backgroundView.scaleType = ImageView.ScaleType.FIT_XY
            // Make everything scale correctly
        }
    }
}