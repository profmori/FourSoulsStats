package com.example.foursoulsstatistics

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity

class SettingsHandler {
    companion object {
        fun initialiseSettings(context: Context) {
            val settingsFile = context.getFileStreamPath("settings.txt")
            // Get the settings file
            settingsFile.createNewFile()
            // Create a new file
            val settings = mapOf(
                "gold" to true,
                "plus" to false,
                "requiem" to false,
                "warp" to false,
                "alt_art" to true,
                "readable_font" to false
            )
            // Set the settings

            saveToFile(context, settings)
            // Save the settings file
        }

        fun saveToFile(context: Context, settings: Map<String, Boolean>) {
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
                    val settingVal = settings[settingKey].toString()
                    // Get the setting boolean as a string
                    val writeText = "$settingKey:$settingVal\n"
                    // Write it to the text file with no spaces and a new line
                    stream.write(writeText.toByteArray())
                    // Add it to the text file
                }
            }
        }

        fun readSettings(context: Context): Map<String, Boolean> {
            val settingsFile = context.getFileStreamPath("settings.txt")
                // Find the settings file

            val reader = context.openFileInput(settingsFile.name).bufferedReader()
            // Create a file stream input reader
            val settingStrings = reader.readLines()
            // Get the settings as a list of lines

            val settingMap = emptyMap<String, Boolean>().toMutableMap()
            // Make an empty, editable map

            settingStrings.forEach { string ->
            // For every line of the settings file
                val keyVal = string.split(":")
                // Split into key and value at the colon
                settingMap += mutableMapOf(keyVal[0] to keyVal[1].toBoolean())
                // Add it to the map as a key-value pair
            }

            return settingMap
            // Return the updated map
        }

        fun setFont(context: Context): Map<String, Typeface> {
        // Allows the font to be set from a central place
            val fontMap: Map<String, Typeface>
            // Creates a map for the font
            val readableFont = readSettings(context)["readable_font"]
            // Get whether the font should be readable
            fontMap = if (readableFont == true) {
                mapOf(
                    "body" to context.resources.getFont(R.font.roboto_regular),
                    "title" to context.resources.getFont(R.font.roboto_black)
                )
                // Use the readable fonts
            } else {
                mapOf(
                    "body" to context.resources.getFont(R.font.four_souls_body),
                    "title" to context.resources.getFont(R.font.four_souls_title)
                )
                // Use the stylised font
            }
        return fontMap
        // Return the current font mapping
        }
    }
}