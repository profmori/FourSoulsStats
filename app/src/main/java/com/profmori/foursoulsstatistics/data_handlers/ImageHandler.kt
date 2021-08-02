package com.profmori.foursoulsstatistics.data_handlers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity

class ImageHandler {

    companion object{
        fun returnImage(context : Context, imageName : String): Bitmap {
            val filePath = "$imageName.png"
            val bitmapFile = context.getFileStreamPath(filePath)
            val bitmapBytes = bitmapFile.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.size)
            return bitmap
        }

        fun writeImage(context: Context, imageName: String, imageBitmap: Bitmap){
            val filePath = "$imageName.png"
            val bitmapFile = context.getFileStreamPath(filePath)
            if(!bitmapFile.exists()){
                bitmapFile.createNewFile()
            }
            context.openFileOutput(filePath,AppCompatActivity.MODE_PRIVATE).use{
                imageBitmap.compress(Bitmap.CompressFormat.PNG,95,it)
            }
        }
    }
}