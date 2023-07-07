package com.profmori.foursoulsstatistics.statistics_pages

import android.content.Context
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.ItemList
import com.profmori.foursoulsstatistics.database.Player

class TableItem(var name: String) {
    companion object {
        fun convert(playerList: Array<Player>): Array<TableItem> {
            var convertedArray = emptyArray<TableItem>()
            for (player in playerList) {
                convertedArray += TableItem(player.playerName)
            }
            return convertedArray
        }

        fun convert(charList: Array<CharEntity>): Array<TableItem> {
            var convertedArray = emptyArray<TableItem>()
            for (char in charList) {
                convertedArray += TableItem(char.charName)
            }
            return convertedArray
        }

        fun convert(eternalList: List<String?>, context: Context): Array<TableItem> {
            var convertedArray = emptyArray<TableItem>()
            val distinctEternals = eternalList.distinct()
            println(distinctEternals)
            val indexList = emptyList<Int>().toMutableList()
            // Creates a list of all the indices of chosen elements of the list
            ItemList.getItems(context).forEach {
                // Iterate through all the selected eternal items
                val index = distinctEternals.indexOf(it.lowercase())
                // Get the index of the eternal in the list of online eternals
                if (index >= 0) {
                    println(it)
                    // If it is in the list
                    indexList += index
                    // Add its index to the list
                } else if (it == "\"I Can't Believe It's Not Butter Bean\"") {
                    println(it)
                    // "I Can't Believe It's Not Butter Bean" - the cause of every special case in the app
                    indexList += distinctEternals.indexOf("\"i can't believe it's not")
                    // Add the cut off version's index to the list
                }
                // Add the index to the list
            }

            val tableEternals = distinctEternals.slice(indexList)
            // Gets the intersection between the currently selected treasures and the treasures with data

            for (eternal in tableEternals) {
                convertedArray += TableItem(eternal!!)
            }
            return convertedArray
        }
    }
}