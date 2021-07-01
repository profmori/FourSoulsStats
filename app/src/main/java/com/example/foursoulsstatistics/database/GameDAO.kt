package com.example.foursoulsstatistics.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
//Data Access object for operations on the game database
interface GameDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // If there is a user which already exists don't do anything
    suspend fun addPlayer(player: Player)
    // Uses a suspended function for co-routine usage, allows for list of players to be added

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // If the same character is added multiple times, don't do it
    suspend fun addCharacter(character: CharEntity)
    // Allows for a list of characters to be added

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGameInstance(gameInstance: GameInstance)

    @Query("SELECT * FROM players")
    // The SQL query the function should call
    suspend fun getPlayers(): Array<Player>
    // Returns a live data list of player entities

    @Query("SELECT * FROM characters WHERE edition = :requestedEdition")
    // SQL query to get the characters who's edition field matches the input requestedEdition
    suspend fun getCharacterList(requestedEdition: String): Array<CharEntity>

    @Query("SELECT * FROM characters WHERE charName = :charName")
    suspend fun getCharData(charName: String): CharEntity

    @Transaction
    @Query("SELECT * FROM players WHERE playerName = :playerName")
    suspend fun getPlayerWithInstance(playerName: String): Array<PlayerWithInstance>

    @Transaction
    @Query("SELECT * FROM characters WHERE charName = :charName")
    suspend fun getCharacterWithInstance(charName: String): Array<CharacterWithInstance>

    @Transaction
    @Query("SELECT * FROM games WHERE gameID = :gameID")
    suspend fun getGameWithInstance(gameID: Long): Array<GameWithInstance>
}