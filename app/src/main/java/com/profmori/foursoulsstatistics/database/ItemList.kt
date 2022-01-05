package com.profmori.foursoulsstatistics.database

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler

class ItemList {
// A class to store all the current class data and update necessary characters

    companion object {
        fun getItems(context: Context): Array<String> {
            // A function to get all the items
            val settings = SettingsHandler.readSettings(context)
            // Get the settings for the app
            var treasureList = readFile(context, R.raw.base_treasures)
            // As a base use the base game treasures
            if (settings["alt_art"].toBoolean()) {
                treasureList += readFile(context, R.raw.alt_treasures)
            }
            if (settings["gish"].toBoolean()) {
                treasureList += readFile(context, R.raw.gish_treasures)
            }
            if (settings["gold"].toBoolean()) {
                treasureList += readFile(context, R.raw.gold_treasures)
            }
            if (settings["plus"].toBoolean()) {
                treasureList += readFile(context, R.raw.plus_treasures)
            }
            if (settings["requiem"].toBoolean()) {
                treasureList += readFile(context, R.raw.requiem_treasures)
            }
            if (settings["tapeworm"].toBoolean()) {
                treasureList += readFile(context, R.raw.tapeworm_treasures)
            }
            if (settings["target"].toBoolean()) {
                treasureList += readFile(context, R.raw.target_treasures)
            }
            if (settings["warp"].toBoolean()) {
                treasureList += readFile(context, R.raw.warp_treasures)
            }
            // Add the correct treasures from sets
            if (settings["custom"].toBoolean()) {
                treasureList += readCustom(context)
            }
            // Add any other treasures included, including custom treasures
            return treasureList.sortedArray()
        }

        fun readCustom(context: Context): Array<String> {
            val customFile = context.getFileStreamPath("custom_treasures.txt")
            // Find the custom treasures file

            if (!customFile.exists()) {
                // If it doesn't exist
                customFile.createNewFile()
                // Create it if it doesn't exist
            }

            val reader = context.openFileInput(customFile.name).bufferedReader()
            // Create a file stream input reader

            return reader.readLines().toTypedArray()
            // Get the settings as a list of lines and return it
        }

        private fun readFile(context: Context, fileName: Int): Array<String> {
            val fileReader = context.resources.openRawResource(fileName).bufferedReader()
            // Open the specified file as a buffered reader
            val items = fileReader.readLines()
            // Read the entire file as a list of lines
            return items.toTypedArray()
            // Return the items as an array of strings
        }

        fun writeCustom(context: Context, items: Array<String>) {
            val customFile = context.getFileStreamPath("custom_treasures.txt")
            // Find the custom treasures file

            val writer = context.openFileOutput(customFile.name, AppCompatActivity.MODE_PRIVATE)
            // Create the file output stream writer

            writer.use { stream ->
                items.forEach { item ->
                    // For every item in the list
                    val writeText = "$item \n"
                    // Compose the line
                    stream.write(writeText.toByteArray())
                    // Write the item
                }
            }
        }
    }
}