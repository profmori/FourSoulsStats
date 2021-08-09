package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import com.profmori.foursoulsstatistics.R

class ImageHandler {

    companion object{
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

        fun writeImage(context: Context, imageName: String, imageBitmap: Bitmap){
            val filePath = "$imageName.png"
            // Find the image save file path
            val bitmapFile = context.getFileStreamPath(filePath)
            // Get the filestream of the image
            if(!bitmapFile.exists()){
            // if the file doesn't exist
                bitmapFile.createNewFile()
                // Create a blank file
            }
            context.openFileOutput(filePath,AppCompatActivity.MODE_PRIVATE).use{
                imageBitmap.compress(Bitmap.CompressFormat.PNG,95,it)
                // Write the compressed image
            }
        }

        fun setButtonImage():Int{
            val backgroundList = arrayOf(
                R.drawable.button_event,
                R.drawable.button_item,
                R.drawable.button_loot
            )
            val randPos = (backgroundList.indices).random()
            return backgroundList[randPos]
        }
    }
}