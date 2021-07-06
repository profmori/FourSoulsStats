package com.example.foursoulsstatistics

import android.content.Context
import android.graphics.Typeface

class TextHandler {
    companion object{
        fun setFont(context: Context): Map<String, Typeface> {
            // Allows the font to be set from a central place
            val fontMap: Map<String, Typeface>
            // Creates a map for the font
            val readableFont = SettingsHandler.readSettings(context)["readable_font"]
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
            // Remove any leading or trailingg whitespace
            return newString
        }
    }
}
