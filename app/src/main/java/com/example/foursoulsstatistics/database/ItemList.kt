package com.example.foursoulsstatistics.database

import android.content.Context
import com.example.foursoulsstatistics.R
import com.example.foursoulsstatistics.data_handlers.SettingsHandler

class ItemList {
// A class to store all the current class data and update necessary characters

    companion object{
        fun getItems(context: Context):Array<String>{
            val settings = SettingsHandler.readSettings(context)
            var treasureList = readFile(context, R.raw.base_treasures)
            if (settings["gold"].toBoolean()){ treasureList += readFile(context, R.raw.gold_treasures) }
            if (settings["plus"].toBoolean()){ treasureList += readFile(context, R.raw.plus_treasures) }
            //if (settings["requiem"].toBoolean()){ treasureList += readFile(context, R.raw.requiem_treasures) }
            if (settings["warp"].toBoolean()){ treasureList += readFile(context, R.raw.warp_treasures) }
            if (settings["promo"].toBoolean()){ treasureList += readFile(context, R.raw.promo_treasures) }
            return treasureList.sortedArray()
        }

        fun readFile(context: Context,fileName: Int):Array<String>{
            val fileReader = context.resources.openRawResource(fileName).bufferedReader()
            val items = fileReader.readLines()
            return items.toTypedArray()
        }
    }
}