package com.profmori.foursoulsstatistics.database

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler

class ItemList {
// A class to store all the current class data and update necessary characters

    companion object{
        fun getItems(context: Context):Array<String>{
            val settings = SettingsHandler.readSettings(context)
            var treasureList = readFile(context, R.raw.base_treasures)
            if (settings["gold"].toBoolean()){ treasureList += readFile(context, R.raw.gold_treasures) }
            if (settings["plus"].toBoolean()){ treasureList += readFile(context, R.raw.plus_treasures) }
            if (settings["requiem"].toBoolean()){ treasureList += readFile(context, R.raw.requiem_treasures) }
            if (settings["warp"].toBoolean()){ treasureList += readFile(context, R.raw.warp_treasures) }
            if (settings["promo"].toBoolean()){ treasureList += readFile(context, R.raw.promo_treasures) }
            if (settings["custom"].toBoolean()){ treasureList += readCustom(context) }
            return treasureList.sortedArray()
        }

        private fun readFile(context: Context, fileName: Int):Array<String>{
            val fileReader = context.resources.openRawResource(fileName).bufferedReader()
            val items = fileReader.readLines()
            return items.toTypedArray()
        }

        fun readCustom(context: Context): Array<String>{
            val customFile = context.getFileStreamPath("custom_treasures.txt")
            // Find the custom treasures file

            if(!customFile.exists()) { customFile.createNewFile() }
            // Create it if it doesn't exist

            val reader = context.openFileInput(customFile.name).bufferedReader()
            // Create a file stream input reader
            val customTreasures = reader.readLines().toTypedArray()
            // Get the settings as a list of lines

            return customTreasures
        }

        fun writeCustom(context: Context, items: Array<String>){
            val customFile = context.getFileStreamPath("custom_treasures.txt")
            // Find the custom treasures file

            val writer = context.openFileOutput(customFile.name, AppCompatActivity.MODE_PRIVATE)
            // Create the file output stream writer

            writer.use { stream ->
                items.forEach { item ->
                    val writeText = "$item \n"
                    // Compose the line
                    stream.write(writeText.toByteArray())
                    // Write the item
                }
            }
        }
    }
}