package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.profmori.foursoulsstatistics.R

class TextHandler {
    companion object{
        fun setFont(context: Context): Map<String, Typeface> {
            // Allows the font to be set from a central place
            val fontMap: Map<String, Typeface>
            // Creates a map for the font
            val readableFont = SettingsHandler.readSettings(context)["readable_font"]
            // Get whether the font should be readable
            fontMap = if (readableFont.toBoolean()){
                mapOf(
                    "body" to ResourcesCompat.getFont(context, R.font.atkinson_hyperlegible_regular)!!,
                    "title" to ResourcesCompat.getFont(context, R.font.atkinson_hyperlegible_bold)!!
                )
                // Use the readable fonts
            } else {
                mapOf(
                    "body" to ResourcesCompat.getFont(context, R.font.four_souls_body)!!,
                    "title" to ResourcesCompat.getFont(context, R.font.four_souls_title)!!
                )
                // Use the stylised font
            }
            return fontMap
            // Return the current font mapping
        }

        fun capitalise(text: String): String {
            // Capitalise the string
            val words = text.split(" ")
            // Gets the individual words of the text
            var newString = ""
            // Create a new string variable
            words.forEach { word ->
                // Iterate through all words
                val newWord = word.replaceFirstChar { it.uppercase() }
                // Get the capitalised word
                newString += " $newWord"
                // Add the capitalised string to the new word
            }
            newString = newString.trim()
            // Remove any leading or trailing whitespace
            return newString
        }
    }
}
