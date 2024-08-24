package com.profmori.foursoulsstatistics.online_database

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.database.CharEntity
import com.profmori.foursoulsstatistics.database.Game
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.database.GameInstance
import com.profmori.foursoulsstatistics.database.ItemList
import com.profmori.foursoulsstatistics.database.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class OnlineDataHandler {

    companion object {
        fun checkWifi(context: Context): Boolean {
            // This is magic off the internet and will probably break in the future
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            } else {
                return true
            }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
            return false
        }

        suspend fun deleteOnlineGameInstances(gameID: String) {
            signIn()
            // Sign into the database
            val onlineDB = Firebase.firestore.collection("game_instances")
            // Get the online database
            val onlineQuery = onlineDB.whereEqualTo("gameID", gameID).get().await()
            // Get all the game instances of the game
            onlineQuery.documents.forEach { document ->
                document.reference.delete()
                // Delete each instance
            }

        }

        private fun generateOnlineName(instance: GameInstance, index: Int): String {
            var obfName = instance.playerName.toCharArray().map { c -> c.code }.joinToString("")
            // Create the obfuscated name from the player's name

            if (instance.solo) {
                obfName += index.toString()
            }

            return instance.gameID.substring(7) + obfName
            // Set the online id to the timecode followed by the obfuscated player

        }

        suspend fun getAllEternals(
            context: Context, searchCoop: Boolean
        ): Array<OnlineGameInstance> {
            signIn()
            // Sign into the database
            var games = emptyArray<OnlineGameInstance>()
            // Creates an object to store all the games every played
            if (checkWifi(context)) {
                val onlineDB = Firebase.firestore.collection("game_instances")
                // Get the online database
                val allEternals = onlineDB.whereNotEqualTo("eternal", null).get().await()
                // Creates a query to read all games where the eternal is not null
                allEternals.documents.forEach { document ->
                    val documentObj = document.toObject<OnlineGameInstance>()!!
                    if (documentObj.coop == searchCoop) {
                        games += arrayOf(documentObj)
                    }
                    // Adds all non co-op game instances with eternals to the list
                }
            }
            return games
        }

        suspend fun getAllGames(context: Context, searchCoop: Boolean): Array<OnlineGameInstance> {
            signIn()
            // Sign into the database
            var games = emptyArray<OnlineGameInstance>()
            // Creates an object to store all the games every played
            if (checkWifi(context)) {
                val onlineDB = Firebase.firestore.collection("game_instances")
                // Get the online database
                val readAll = onlineDB.get().await()
                // Creates a query to read all games
                readAll.documents.forEach { document ->
                    val documentObj = document.toObject<OnlineGameInstance>()!!
                    if (documentObj.coop == searchCoop) {
                        games += arrayOf(documentObj)
                    }
                    // Adds all game instances to the list
                }
            }
            return games
        }

        fun getGroupGames(context: Context) {
            signIn()
            // Sign into the database
            if (checkWifi(context)) {
                val settings = SettingsHandler.readSettings(context)
                // Read the current settings
                val groupID = settings["groupID"]
                // Get the id of the group
                val onlineDB = Firebase.firestore.collection("game_instances")
                // Get the online database
                val localDB = GameDataBase.getDataBase(context)
                // Gets the local database
                val localDAO = localDB.gameDAO
                // Gets the local DAO
                CoroutineScope(Dispatchers.IO).launch {
                    // Launch a coroutine
                    saveGames(context)
                    // Save any games which aren't saved
                    localDAO.clearGameInstances()
                    localDAO.clearGames()
                    // Clear any local games
                    val onlineQuery = onlineDB.whereEqualTo("groupID", groupID).get().await()
                    // Get all the games of the group
                    onlineQuery.documents.forEach { document ->
                        val gameInstance = document.toObject<OnlineGameInstance>()!!
                        // Creates the online game instance object from the online database entry
                        if ((gameInstance.groupID != "") and (gameInstance.gameID != "") and (gameInstance.gameSize != 0) and (gameInstance.treasureNum != -1) and (gameInstance.playerName != "") and (gameInstance.charName != "") and (gameInstance.eternal != "") and (gameInstance.souls != -1)) {
                            // If the game instance had all entries valid
                            localDAO.addPlayer(Player(gameInstance.playerName))
                            // Try to add the player to the database
                            localDAO.addCharacter(
                                CharEntity(
                                    gameInstance.charName, R.drawable.blank_char, null, "custom"
                                )
                            )
                            // Add any custom characters that don't exist

                            localDAO.addGameInstance(
                                // Add / update the game instance to the local database
                                GameInstance(
                                    0,
                                    gameInstance.gameID,
                                    gameInstance.playerName,
                                    gameInstance.charName,
                                    gameInstance.eternal,
                                    gameInstance.souls,
                                    gameInstance.winner,
                                    gameInstance.solo
                                )
                            )

                            localDAO.updateGame(
                                // Add / update the stored game in the local database
                                Game(
                                    gameInstance.gameID,
                                    gameInstance.gameSize,
                                    gameInstance.treasureNum,
                                    true,
                                    gameInstance.coop,
                                    gameInstance.turnsLeft
                                )
                            )
                        }
                    }
                }
            }
        }

        suspend fun getGroupIDs(): Array<String> {
            // Get the list of all group ids
            var idList = emptyArray<String>()
            // Create an empty list
            val onlineDatabase = Firebase.firestore.collection("group_ids")
            // Access the online database
            val data = onlineDatabase.get()
            // Get all data from the database
            data.addOnSuccessListener { groupIDs ->
                // When data is retrieved
                groupIDs.documents.forEach {
                    // Iterate through each id
                    val idObject = it.toObject<OnlineGroupID>()!!
                    // Create an online group id object
                    if (!idList.contains(idObject.id)) {
                        idList += arrayOf(idObject.id)
                        // Add it to the list of ids
                    }
                }
            }.await()
            return idList
            // Return the id list

        }

        fun getOnlineItems(context: Context) {
            // Get the list of all items
            val items = emptyMap<String, Array<String>>().toMutableMap()
            // Create an empty map
            if (checkWifi(context)) {
                val onlineDatabase = Firebase.firestore.collection("items")
                // Access the online database
                val data = onlineDatabase.get()
                // Get all data from the database
                data.addOnSuccessListener { itemDocuments ->
                    // When data is retrieved
                    itemDocuments.documents.forEach {
                        // Iterate through each item
                        val onlineItem = it.toObject<OnlineItem>()!!
                        // Create an online item object
                        var itemArray = items[onlineItem.set]
                        // Get the current set of items in that set
                        if (itemArray.isNullOrEmpty()) {
                            // If there are none
                            itemArray = arrayOf(onlineItem.name)
                            // Create an array with just that item
                        } else {
                            itemArray += arrayOf(onlineItem.name)
                            // Otherwise add the item to the array
                        }
                        items[onlineItem.set] = itemArray
                        // Set the array for that set to be updated
                    }
                    ItemList.saveToFile(context, items)
                    // Save to a file
                }
            }
        }

        fun saveGames(context: Context) {
            signIn()
            // Sign into the database
            val settings = SettingsHandler.readSettings(context)
            // Read the current settings
            if (settings["online"].toBoolean()) {
                // If the user has allowed data to be uploaded
                val localDB = GameDataBase.getDataBase(context)
                // Get the local database
                val localDAO = localDB.gameDAO
                // Get the local DAO
                CoroutineScope(Dispatchers.IO).launch {
                    // Launch a coroutine
                    val unsavedGames = localDAO.getUploadGames()
                    // Get the list of games which are local only
                    unsavedGames.forEach { game ->
                        // Iterates through each unsaved game
                        val gameInstances = localDAO.getGameWithInstance(game.gameID)
                        // Get the list of instances for this game
                        gameInstances.forEach { gameInstance ->
                            gameInstance.gameInstances.forEachIndexed { index, instance ->
                                // Iterates through each game instance
                                saveOnlineGameInstance(game, instance, index)
                                // Save the online game instance
                            }
                        }
                        val updatedGame = Game(
                            game.gameID,
                            game.playerNo,
                            game.treasureNo,
                            true,
                            game.coop,
                            game.turnsLeft
                        )
                        // Create the updated game object
                        localDAO.updateGame(updatedGame)
                        // Update the game to be marked as uploaded
                    }
                }
            }
        }

        fun saveGroupID(newID: String) {
            val id = SettingsHandler.sanitiseGroupID(newID)
            // Sanitise the id so it doesn't contain ambiguous characters
            val onlineDatabase = Firebase.firestore.collection("group_ids")
            // Get the online database
            onlineDatabase.document(id).set(OnlineGroupID(id))
            // Add it as an online group id object
        }

        suspend fun saveOnlineGameInstance(game: Game, gameInstance: GameInstance, index: Int) {
            signIn()
            // Sign into the database
            val onlineDB = Firebase.firestore.collection("game_instances")
            // Get the online game database
            val docName = generateOnlineName(gameInstance, index)
            val newOnlineGameInstance = OnlineGameInstance(
                // Creates a new online game instance
                gameInstance.gameID.subSequence(0, 6).toString(),
                gameInstance.gameID,
                game.playerNo,
                game.treasureNo,
                gameInstance.playerName,
                gameInstance.charName,
                gameInstance.eternal,
                gameInstance.souls,
                gameInstance.winner,
                gameInstance.solo,
                game.coop,
                game.turnsLeft
            )
            onlineDB.document(docName).set(newOnlineGameInstance).await()
            // Add the game instance to the online database
        }

        fun signIn() {
            val auth = Firebase.auth
            // Get the firebase authorisation
            auth.signInAnonymously()
            // Sign in anonymously
        }
    }
}