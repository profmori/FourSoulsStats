package com.example.foursoulsstatistics.online_database

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.example.foursoulsstatistics.database.Game
import com.example.foursoulsstatistics.database.GameDataBase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OnlineDataHandler {

    companion object{
        fun saveGames(context: Context){
            val onlineDatabase = Firebase.firestore.collection("game_instances")
            val localDB = GameDataBase.getDataBase(context)
            val localDAO = localDB.gameDAO
            CoroutineScope(Dispatchers.IO).launch {
                val unsavedGames = localDAO.getUploadGames()
                unsavedGames.forEach { game ->
                    val gameInstances = localDAO.getGameWithInstance(game.gameID)
                    gameInstances.forEach {gameInstance ->
                        gameInstance.gameInstances.forEach { instance ->
                            println(instance)
                            val newOnlineGameInstance = OnlineGameInstance(
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
                            onlineDatabase.add(newOnlineGameInstance).await()
                            println(newOnlineGameInstance)
                        }
                    }
                    val updatedGame = Game(game.gameID, game.playerNo, game.treasureNo, true)
                    localDAO.updateGame(updatedGame)
                }
            }
        }
    }
}