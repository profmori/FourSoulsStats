package com.profmori.foursoulsstatistics.online_database

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler
import com.profmori.foursoulsstatistics.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.NullPointerException

class OnlineDataHandler {

    companion object {
        fun saveGames(context: Context) {
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
                            gameInstance.gameInstances.forEach { instance ->
                            // Iterates through each game instance
                                saveOnlineGameInstance(game, instance)
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

        suspend fun deleteOnlineGameInstances(gameID: String){
            val onlineDB = Firebase.firestore.collection("game_instances")
            // Get the online database
            val onlineQuery = onlineDB.whereEqualTo("gameID", gameID).get().await()
            // Get all the game instances of the game
            onlineQuery.documents.forEach { document ->
                document.reference.delete()
            }

        }

        suspend fun saveOnlineGameInstance(game: Game, gameInstance: GameInstance){
            val onlineDB = Firebase.firestore.collection("game_instances")
            // Get the online game database
            val docName = generateOnlineName(gameInstance)
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
                gameInstance.winner
            )
            onlineDB.document(docName).set(newOnlineGameInstance).await()
            // Add the game instance to the online database
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
                    if (
                        (gameInstance.groupID != "") and (gameInstance.gameID != "") and
                        (gameInstance.gameSize != 0) and (gameInstance.treasureNum != -1) and
                        (gameInstance.playerName != "") and (gameInstance.charName != "") and
                        (gameInstance.eternal != "") and (gameInstance.souls != -1)
                    ) {
                    // If the game instance had all entries valid
                        localDAO.addPlayer(Player(gameInstance.playerName))
                        // Try to add the player to the database
                        localDAO.addCharacter(
                            CharEntity(
                                gameInstance.charName,
                                R.drawable.blank_char,
                                null,
                                "custom"
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
                                gameInstance.winner
                            )
                        )

                        localDAO.updateGame(
                        // Add / update the stored game in the local database
                            Game(
                                gameInstance.gameID, gameInstance.gameSize,
                                gameInstance.treasureNum, true
                            )
                        )
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

        private fun generateOnlineName(instance: GameInstance):String{
            val obfName =  instance.playerName.toCharArray().map { c -> c.code }
                .joinToString("")
            // Create the obfuscated name from the player's name

            return instance.gameID.substring(7) + obfName
            // Set the online id to the timecode followed by the obfuscated player

        }
    }
}