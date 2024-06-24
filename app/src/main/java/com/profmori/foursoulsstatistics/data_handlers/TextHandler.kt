package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.profmori.foursoulsstatistics.R

class TextHandler {
    companion object {
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

        fun getIconText(iconName: String): Int {
            val iconMap = mapOf(
                "alt_art" to R.string.icon_alt_art,
                "base" to R.string.icon_base,
                "bumbo" to R.string.icon_bumbo,
                "custom" to R.string.icon_custom,
                "gfuel" to R.string.icon_gfuel,
                "gish" to R.string.icon_gish,
                "gold" to R.string.icon_gold,
                "plus" to R.string.icon_plus,
                "promo" to R.string.icon_promo,
                "requiem" to R.string.icon_requiem,
                "retro" to R.string.icon_retro,
                "summer" to R.string.icon_summer,
                "tapeworm" to R.string.icon_tapeworm,
                "target" to R.string.icon_target,
                "unboxing" to R.string.icon_unboxing,
                "warp" to R.string.icon_warp,
                "youtooz" to R.string.icon_youtooz
            )
            // Create a map of all the different set icons
            return if (iconMap.containsKey(iconName)) {
                // If the set is in the map
                iconMap[iconName]!!
                // Return the set icon
            } else {
                -1
                // Return an invalid input as default
            }
        }

        fun setFont(context: Context): Map<String, Typeface> {
            // Allows the font to be set from a central place
            val fontMap: Map<String, Typeface>
            // Creates a map for the font
            val readableFont = SettingsHandler.readSettings(context)["readable_font"]
            // Get whether the font should be readable
            fontMap = if (readableFont.toBoolean()) {
                // Sets the font map based on the settings file
                mapOf(
                    "body" to ResourcesCompat.getFont(
                        context,
                        R.font.atkinson_hyperlegible_regular
                    )!!,
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

        fun updateRetroFont(
            context: Context,
            oldMap: Map<String, Typeface>
        ): Map<String, Typeface> {
            // Allows the retro font to be set to retro from a central place
            val fontMap: Map<String, Typeface>
            // Creates a map for the font
            val pixelFont = SettingsHandler.readSettings(context)["pixel_font"]
            // Get whether the font should be readable
            fontMap = if (pixelFont.toBoolean()) {
                // Sets the font map based on the settings file
                mapOf(
                    "body" to ResourcesCompat.getFont(
                        context,
                        R.font.four_souls_pixel
                    )!!,
                    "title" to oldMap["title"]!!
                )
                // Use the pixel font
            } else {
                oldMap
                // Use the original font
            }
            return fontMap
            // Return the current font mapping
        }

        fun wordLength(text: String): Int {
            val words = text.split(" ")
            // Gets the individual words of the text
            var maxLen = 0
            // Holds the maximum word length
            words.forEach {
                // Iterate through the words of the string
                if (it.length > maxLen) {
                    // If this is the longest word so far
                    maxLen = it.length
                    // Increase the maximum recorded length
                }
            }
            return maxLen
            // Return the maximum found length
        }
    }
}
