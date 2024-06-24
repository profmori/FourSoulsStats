package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.profmori.foursoulsstatistics.R


class ImageHandler {

    companion object {
        fun returnImage(context: Context, imageName: String): Bitmap {
            val filePath = "$imageName.png"
            // Find the image save file path
            val bitmapFile = context.getFileStreamPath(filePath)
            // Get the filestream of the image
            val bitmapBytes = bitmapFile.readBytes()
            // Read the data
            return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.size)
            // Return the bitmap
        }

        fun setButtonImage(): Int {
            val backgroundList = arrayOf(
                R.drawable.button_event,
                R.drawable.button_item,
                R.drawable.button_loot
            )
            // Create an array of the possible button images
            val randPos = (backgroundList.indices).random()
            // Pick a random index in the list
            return backgroundList[randPos]
            // Return the random drawable file
        }

        fun writeImage(context: Context, imageName: String, imageBitmap: Bitmap) {
            val filePath = "$imageName.png"
            // Find the image save file path
            val bitmapFile = context.getFileStreamPath(filePath)
            // Get the filestream of the image
            if (!bitmapFile.exists()) {
                // if the file doesn't exist
                bitmapFile.createNewFile()
                // Create a blank file
            }
            context.openFileOutput(filePath, AppCompatActivity.MODE_PRIVATE).use {
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 95, it)
                // Write the compressed image
            }
        }

        fun getIcon(iconName: String): Int {
            val iconMap = mapOf(
                "alt_art" to R.drawable.icon_alt_art,
                "base" to R.drawable.icon_base,
                "bumbo" to R.drawable.icon_bum,
                "custom" to R.drawable.icon_custom,
                "gfuel" to R.drawable.icon_gfuel,
                "gish" to R.drawable.icon_gish,
                "gold" to R.drawable.icon_gold,
                "plus" to R.drawable.icon_plus,
                "promo" to R.drawable.icon_promo,
                "requiem" to R.drawable.icon_requiem,
                "retro" to R.drawable.icon_retro,
                "retro_off" to R.drawable.icon_retro_off,
                "summer" to R.drawable.icon_soi,
                "summer_off" to R.drawable.icon_soi_off,
                "tapeworm" to R.drawable.icon_tapeworm,
                "target" to R.drawable.icon_target,
                "unboxing" to R.drawable.icon_unboxing,
                "warp" to R.drawable.icon_warp,
                "youtooz" to R.drawable.icon_ytz
            )
            // Create a map of all the different set icon
            return if (iconMap.containsKey(iconName)) {
                // If the icon is on the list, return it
                iconMap[iconName]!!
            } else {
                R.drawable.icon_custom
                // Return the card as a default
            }
        }

        fun randomBlank(): Int {
            //31 blank_char, 19 blank_char_tainted, 1 blank_char_mines, 5 blank_char_arcade,
            //5 "unique" (baba, blind johnny, dark judas, steven, tapeworm only uses old)
            val randInt = (1..56).random()
            // Generate a random number from 1 to 56 (number of backgrounds excluding unique)
            return when (randInt) {
                in 32..50 -> R.drawable.blank_char_tainted
                in 51..55 -> R.drawable.blank_char_arcade
                56 -> R.drawable.blank_char_mines
                else -> R.drawable.blank_char
            }
            // Select returned value based on chosen number
        }

        fun getLimitedAlt(charName: String): Int {
            when (charName) {
                "isaac" -> {
                    return R.drawable.box_isaac
                }

                "cain" -> {
                    return R.drawable.box_cain
                }

                "the lost" -> {
                    return R.drawable.box_the_lost
                }

                "bum-bo" -> {
                    return R.drawable.bum_bum_bo
                }

                "guppy" -> {
                    return R.drawable.ytz_guppy
                }
            }
            return -1
        }

        fun randomReroll(context: Context): Drawable {
            val returnImage: Int
            // Initialise the variable
            val images = getRerollList()
            // Get the full list of images
            val imageFile = context.getFileStreamPath("reroll_images.txt")
            // Find the reroll image settings file
            returnImage = if (imageFile.exists()) {
                // If the reroll image settings file exists
                val rerollPrefs = readRerollFile(context)
                // Get the reroll settings
                val imageChoices = rerollPrefs.filter {
                    it.value
                }.keys
                // Filter the keys to remove all the ones with  a value of false
                imageChoices.random()
                // Select a random image from the remaining list
            } else {
                // If the reroll image settings file doesn't exist
                createRerollFile(context)
                // Create it
                images.random()
                // Select a random reroll image
            }

            return AppCompatResources.getDrawable(context, returnImage)!!
            // Return the appropriate drawable rather than the integer
        }

        fun readRerollFile(context: Context): MutableMap<Int, Boolean> {
            val imageFile = context.getFileStreamPath("reroll_images.txt")
            // Find the reroll image settings file
            if (!imageFile.exists()) {
                // If the reroll image settings file doesn't exist
                createRerollFile(context)
                // Create it
            }
            val images = getRerollList()
            // Get the full list of possible icons
            val fullList = mutableMapOf<Int, Boolean>()
            // Create a mutable list to store the images
            val imageLines = imageFile.readLines()
            // Read every line of the image file
            imageLines.forEach {
                // In every line extract the data to the full list map
                val splitText = it.split(":")
                // Split at the colon
                if (images.contains(splitText[0].toInt())) {
                    fullList[splitText[0].toInt()] = splitText[1].toBoolean()
                }
            }
            if (fullList.keys.size < images.size) {
                // If the full list is smaller than all the icons
                images.forEach {
                    // Check every icon
                    if (!fullList.keys.contains(it)) {
                        // If it's not on the list
                        fullList[it] = true
                        // Add it, and make it a option to pop up
                    }
                }
                writeRerollFile(context, fullList)
                // Write the updated preference file
            }
            return fullList
            // Return the full list of images
        }

        private fun createRerollFile(context: Context) {
            val imageFile = context.getFileStreamPath("reroll_images.txt")
            // Find the reroll image settings file
            imageFile.createNewFile()
            // Create a new file
            val rerollMap = mutableMapOf<Int, Boolean>()
            // Create a map to store the images
            getRerollList().forEach {
                // For every icon in the list
                rerollMap[it] = true
                // Make it a valid possible icon
            }
            writeRerollFile(context, rerollMap)
            // Write the settings file
        }

        fun writeRerollFile(context: Context, rerollPrefs: MutableMap<Int, Boolean>) {
            val imageFile = context.getFileStreamPath("reroll_images.txt")
            // Find the reroll image settings file
            val writer = context.openFileOutput(imageFile.name, AppCompatActivity.MODE_PRIVATE)
            // Create the file output stream writer

            val keys = rerollPrefs.keys
            // Get the list of keys used for the list

            writer.use { stream ->
                keys.forEach { imageId ->
                    // Write each key with it's choice using a colon separator
                    val choice = rerollPrefs[imageId]
                    val writeString = "$imageId:$choice\n"
                    stream.write(writeString.toByteArray())
                    // Add it to the text file
                }
            }
        }

        private fun getRerollList(): Array<Int> {
            return arrayOf(
                R.drawable.reroll_d6,
                R.drawable.reroll_classic_roller,
                R.drawable.reroll_clicker,
                R.drawable.reroll_dice_shard,
                R.drawable.reroll_d100,
                R.drawable.reroll_d20,
                R.drawable.reroll_spindown_dice,
                R.drawable.reroll_d10,
                R.drawable.reroll_d4,
                R.drawable.reroll_perthro,
                R.drawable.reroll_chaos,
                R.drawable.reroll_restock,
                R.drawable.reroll_glitch,
                R.drawable.reroll_undefined,
                R.drawable.reroll_questionmark_card,
                R.drawable.reroll_r_key
            )
            // Return this array of icons
        }
    }
}