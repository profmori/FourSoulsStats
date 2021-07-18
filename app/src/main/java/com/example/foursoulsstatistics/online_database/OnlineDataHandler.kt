package com.example.foursoulsstatistics.online_database

import android.content.Context
import com.example.foursoulsstatistics.data_handlers.SettingsHandler
import com.example.foursoulsstatistics.database.Game
import com.example.foursoulsstatistics.database.GameDataBase
import com.example.foursoulsstatistics.database.GameInstance
import com.example.foursoulsstatistics.database.Player
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class OnlineDataHandler {

    companion object {
        fun saveGames(context: Context) {
            val settings = SettingsHandler.readSettings(context)
            // Read the current settings
            if (settings["online"].toBoolean()) {
            // If the user has allowed data to be uploaded
                val onlineDB = Firebase.firestore.collection("game_instances")
                // Get the online game database
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
                            gameInstance.gameInstances.forEach { instance ->
                            // Iterates through each game instance
                                val newOnlineGameInstance = OnlineGameInstance(
                                // Creates a new online game instance
                                    instance.gameID.subSequence(0, 6).toString(),
                                    instance.gameID,
                                    game.playerNo,
                                    game.treasureNo,
                                    instance.playerName,
                                    instance.charName,
                                    instance.eternal,
                                    instance.souls,
                                    instance.winner
                                )
                                onlineDB.add(newOnlineGameInstance).await()
                                // Add the game instance to the online database
                            }
                        }
                        val updatedGame = Game(game.gameID, game.playerNo, game.treasureNo, true)
                        // Create the updated game object
                        localDAO.updateGame(updatedGame)
                        // Update the game to be marked as uploaded
                    }
                }
            }
        }

        fun getGroupGames(context: Context) {
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
                val localGames = localDAO.getGames().map { game -> game.gameID }
                // Get all games stored locally by id
                val onlineQuery = onlineDB.whereEqualTo("groupID", groupID).get().await()
                // Get all the games of the group
                onlineQuery.documents.forEach { document ->
                    val gameInstance = document.toObject<OnlineGameInstance>()!!
                    // Greates the online game instance object from the online database entry
                    if (
                        (gameInstance.groupID != "") and (gameInstance.gameID != "") and
                        (gameInstance.gameSize != 0) and (gameInstance.treasureNum != -1) and
                        (gameInstance.playerName != "") and (gameInstance.charName != "") and
                        (gameInstance.eternal != "") and (gameInstance.souls != -1)
                    ) {
                    // If the game insntance had all entries valid
                        if (!localGames.contains(gameInstance.gameID)) {
                        // If the game is not stored locally
                            localDAO.addPlayer(Player(gameInstance.playerName))
                            // Try to add the player to the database
                            localDAO.addGameInstance(
                            // Add the game instance to the local database
                                GameInstance(
                                    0,
                                    gameInstance.gameID,
                                    gameInstance.playerName,
                                    gameInstance.charName,
                                    gameInstance.eternal,
                                    gameInstance.souls,
                                    gameInstance.winner
                                )
                            )

                            localDAO.addGame(
                            // Try to add the game to the local database
                                Game(
                                    gameInstance.gameID, gameInstance.gameSize,
                                    gameInstance.treasureNum, true
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
            // Acces the online database
            val groupIDs = onlineDatabase.get().await()
            // Get all data from the database
            groupIDs.documents.forEach {
            // Iterate through each id
                val idObject = it.toObject<OnlineGroupID>()!!
                // Create an online group id object
                idList += arrayOf(idObject.id)
                // Add it to the list of ids
            }
            return idList
            // Return the id list
        }

        fun saveGroupID(newID: String){
            val onlineDatabase = Firebase.firestore.collection("group_ids")
            // Get the online database
            onlineDatabase.add(OnlineGroupID(newID))
            // Add it as an online group id object
        }
    }
}