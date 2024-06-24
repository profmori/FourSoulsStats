package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.text.buildSpannedString
import androidx.fragment.app.FragmentManager
import com.profmori.foursoulsstatistics.BuildConfig
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.custom_adapters.dialogs.PatchNotesDialog
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsHandler {

    companion object {
        fun getBackground(context: Context): Map<String, String> {
            val settings = readSettings(context)
            // Read the settings
            val background = setFromKey(context, settings["background"]!!)
            // Set the background text from the key

            val border = setFromKey(context, settings["border"]!!)
            // Set the foreground text from the key

            return mapOf(
                "background" to background,
                "border" to border
            )
            // Return a map of the text
        }

        fun getEditions(context: Context): Array<String> {
            val settings = readSettings(context)
            // Get the settings
            var editionArray = arrayOf("base")
            // As a default include the base game
            val playableSets = getSets()
            // Get all the sets in the game
            for (set in playableSets) {
                if (set != "base") {
                    if (settings[set].toBoolean()) {
                        editionArray += set
                    }
                }
            }
            // Check every setting and add the right editions
            return editionArray
        }

        fun getSets(): Array<String> {
            return arrayOf(
                "base",
                "gold",
                "plus",
                "requiem",
                "alt_art",
                "bumbo",
                "gfuel",
                "gish",
                "promo",
                "retro",
                "summer",
                "tapeworm",
                "target",
                "unboxing",
                "warp",
                "youtooz",
                "custom"
            )
        }

        private fun initialiseSettings(context: Context) {

            OnlineDataHandler.signIn()
            // Sign into the online database
            val settingsFile = context.getFileStreamPath("settings.txt")
            // Get the settings file

            if (!settingsFile.exists()) {
                // If there is no settings file
                createSettings(context)
            }
        }

        private fun createSettings(context: Context): MutableMap<String, String> {
            val settingsFile = context.getFileStreamPath("settings.txt")
            // Get the settings file
            settingsFile.createNewFile()
            // Create a new file
            var existingIDs = emptyArray<String>()
            CoroutineScope(Dispatchers.IO).launch {
                existingIDs = OnlineDataHandler.getGroupIDs()
            }
            // Get any existing ids
            val charPool: List<Char> = ('A'..'Z') + ('0'..'9')
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
                "readable_font" to "false",
                "pixel_font" to "false",
                "background" to "loot_back",
                "border" to "monster_back",
                "multi_eden" to "true",
                "version_no" to BuildConfig.VERSION_CODE.toString(),
                "online" to "false",
                "duplicate_characters" to "false",
                "duplicate_eden" to "false",
                "random_eden" to "false"
            )
            // Set the settings

            val gameSets = getSets()
            // Get all the sets in the game

            for (set in gameSets) {
                settings[set] = "false"
            }

            saveToFile(context, settings)
            // Save the settings file

            return settings
        }

        fun readSettings(context: Context): MutableMap<String, String> {

            initialiseSettings(context)
            // Create a new settings file if appropriate
            val settingsFile = context.getFileStreamPath("settings.txt")
            // Find the settings file
            val settingStrings = settingsFile.readLines()
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

            if (settingMap.isEmpty()) {
                settingsFile.delete()
                settingMap = createSettings(context)
            }

            return settingMap
            // Return the updated map
        }

        fun sanitiseGroupID(id: String): String {
            var returnID = id
            // Store the old ID to return if it is valid
            if (id.contains('O', true)) {
                // If the id contains the letter o
                returnID = id.replace('O', '0', true)
            }
            // Get rid of any o characters for 0
            return returnID
            // Return the sanitised ID
        }

        private fun setFromKey(context: Context, key: String): String {
            return when (key) {
                "character_back" -> context.resources.getString(R.string.character_back)
                "eternal_back" -> context.resources.getString(R.string.eternal_back)
                "loot_back" -> context.resources.getString(R.string.loot_back)
                "monster_back" -> context.resources.getString(R.string.monster_back)
                "soul_back" -> context.resources.getString(R.string.soul_back)
                "treasure_back" -> context.resources.getString(R.string.treasure_back)
                "room_back" -> context.resources.getString(R.string.room_back)
                else -> context.resources.getString(R.string.loot_back)
            }
            // Use a when statement on all the possible text options to match the key
        }

        fun saveToFile(
            context: Context,
            settings: MutableMap<String, String>
        ): MutableMap<String, String> {
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

        fun updateBackground(context: Context, backgroundView: ImageView) {
            val settings = readSettings(context)
            // Read the settings file
            when (settings["background"]) {
                "character_back" -> backgroundView.setBackgroundResource(R.drawable.bg_character_back)
                "eternal_back" -> backgroundView.setBackgroundResource(R.drawable.bg_tiled_eternal_back)
                "loot_back" -> backgroundView.setBackgroundResource(R.drawable.bg_tiled_loot_back)
                "treasure_back" -> backgroundView.setBackgroundResource(R.drawable.bg_tiled_treasure_back)
                "monster_back" -> backgroundView.setBackgroundResource(R.drawable.bg_monster_back)
            }
            // Set the background from the settings file

            when (settings["border"]) {
                "character_back" -> backgroundView.setImageResource(R.drawable.bg_border_character_back)
                "eternal_back" -> backgroundView.setImageResource(R.drawable.bg_border_eternal_back)
                "loot_back" -> backgroundView.setImageResource(R.drawable.bg_border_loot_back)
                "monster_back" -> backgroundView.setImageResource(R.drawable.bg_border_monster_back)
                "soul_back" -> backgroundView.setImageResource(R.drawable.bg_border_soul_back)
                "treasure_back" -> backgroundView.setImageResource(R.drawable.bg_border_treasure_back)
                "room_back" -> backgroundView.setImageResource(R.drawable.bg_border_room_back)
            }
            // Set the border from the settings file

            backgroundView.scaleType = ImageView.ScaleType.FIT_XY
            // Make everything scale correctly
        }

        fun versionCheck(
            versionNo: String?,
            context: Context,
            fragmentManager: FragmentManager
        ): String {
            if (!versionNo.isNullOrBlank()) {
                // Otherwise set the variable to the current version number

                val versions = BuildConfig.VERSION_CODE downTo 0
                // Get a list of all versions up to this one
                var versionNotes = buildSpannedString {
                    this.append("")
                    // Create a blank entry for the version notes
                }
                for (version in versions) {
                    // Iterate through every version
                    if (versionNo.toInt() < version) {
                        // If the current version is before a version
                        val name = "version_$version"
                        // Generate the string name for the version
                        val stringID = context.resources.getIdentifier(
                            name,
                            "string",
                            "com.profmori.foursoulsstatistics"
                        )
                        // Get the version resource ID
                        if (stringID > 0) {
                            // If the string exists
                            val versionNote = HtmlCompat.fromHtml(
                                context.resources.getString(stringID),
                                HtmlCompat.FROM_HTML_MODE_COMPACT
                            )
                            // Get it using HTMLCompat for formatting
                            versionNotes = buildSpannedString {
                                this.append(versionNotes)
                                // Add the existing notes to the start of the string
                                this.append(versionNote)
                                // Add the new version notes to the end
                            }
                        }
                    }
                }

                if (versionNotes.length > 2) {
                    // If any version notes exist
                    val fonts = TextHandler.setFont(context)
                    // Get the button font
                    val notesDialog =
                        PatchNotesDialog(versionNotes, fonts)
                    // Create the patch notes dialog
                    notesDialog.show(fragmentManager, "patchNotes")
                    // Show the patch notes dialog as a popup
                }
            }

            return BuildConfig.VERSION_CODE.toString()
            // Return the current version code so the settings can be updated
        }
    }
}