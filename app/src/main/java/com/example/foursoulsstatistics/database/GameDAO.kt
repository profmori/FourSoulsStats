package com.example.foursoulsstatistics.database

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // If the same character is updated replace it
    suspend fun updateCharacter(character: CharEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    // Should never happen
    suspend fun addGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Overwrite when upating the game database
    suspend fun updateGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Add a game instance, identical entries should be replaced
    suspend fun addGameInstance(gameInstance: GameInstance)

    @Query("SELECT * FROM players")
    // The SQL query the function should call
    suspend fun getPlayers(): Array<Player>
    // Returns a live data list of player entities

    @Query("SELECT * FROM characters WHERE edition = :requestedEdition")
    // SQL query to get the characters who's edition field matches the input requestedEdition
    suspend fun getCharacterList(requestedEdition: String): Array<CharEntity>

    @Query("SELECT * FROM characters")
    // SQL query to get all characters
    suspend fun getFullCharacterList(): Array<CharEntity>

    @Query("SELECT * FROM characters WHERE charName = :charName")
    // Gets the data of the exact character required
    suspend fun getCharData(charName: String): CharEntity

    @Query("SELECT * FROM games")
    // Gets all the games data table
    suspend fun getGames(): Array<Game>

    @Query("SELECT * FROM games WHERE uploaded = 0")
    suspend fun getUploadGames(): Array<Game>


    @Query("SELECT * FROM game_instances WHERE eternal NOT null")
    // Gets all the eternals with the game data of their games
    suspend fun getEternalList(): Array<GameInstance>

    @Transaction
    @Query("SELECT * FROM players WHERE playerName = :playerName")
    // Gets player game data
    suspend fun getPlayerWithInstance(playerName: String): Array<PlayerWithInstance>

    @Transaction
    @Query("SELECT * FROM characters WHERE charName = :charName")
    // Gets character game data
    suspend fun getCharacterWithInstance(charName: String): Array<CharacterWithInstance>

    @Transaction
    @Query("SELECT * FROM games WHERE gameID = :gameID")
    // Gets data for a single game
    suspend fun getGameWithInstance(gameID: String): Array<GameWithInstance>
}