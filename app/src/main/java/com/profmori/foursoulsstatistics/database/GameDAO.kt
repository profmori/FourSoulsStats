package com.profmori.foursoulsstatistics.database

import androidx.room.*

@Dao
//Data Access object for operations on the game database
interface GameDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // If there is a player which already exists don't do anything
    fun addPlayer(player: Player)
    // Uses a suspended function for co-routine usage, allows for list of players to be added

    @Query("DELETE FROM players")
    fun clearPlayers()
    // Clear out the players table

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // If the same character is added multiple times, don't do it
    fun addCharacter(character: CharEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // If the same character is updated replace it
    fun updateCharacter(character: CharEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // If the same game is added, ignore repeats
    fun addGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Overwrite when updating the game database
    fun updateGame(game: Game)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Add a game instance, identical entries should be replaced
    fun addGameInstance(gameInstance: GameInstance)

    @Delete
    fun deleteCharacter(character: CharEntity)
    // Remove the selected character from the database

    @Query("DELETE FROM games")
    fun clearGames()
    // Clear out the games table

    @Query("DELETE FROM games WHERE gameID = :gameID")
    fun clearSingleGame(gameID: String)
    // Delete the given game

    @Query("DELETE FROM game_instances")
    fun clearGameInstances()
    // Clear out the game instances table

    @Query("DELETE FROM game_instances WHERE gameID = :gameID")
    fun clearSingleGameInstance(gameID: String)
    // Remove the game instances for the given game ID

    @Query("SELECT * FROM players")
    // The SQL query the function should call
    fun getPlayers(): Array<Player>
    // Returns a live data list of player entities

    @Query("SELECT * FROM characters WHERE edition = :requestedEdition")
    // SQL query to get the characters who's edition field matches the input requestedEdition
    fun getCharacterList(requestedEdition: String): Array<CharEntity>

    @Query("SELECT * FROM characters")
    // SQL query to get all characters
    fun getFullCharacterList(): Array<CharEntity>

    @Query("SELECT * FROM characters WHERE charName = :charName")
    // Gets the data of the exact character required
    fun getCharData(charName: String): CharEntity

    @Query("SELECT * FROM games WHERE gameID = :gameID")
    // Gets a single game by its id
    fun getGame(gameID: String): Game

    @Query("SELECT * FROM games")
    // Gets all the games data table
    fun getGames(): Array<Game>

    @Query("SELECT * FROM games WHERE uploaded = 0")
    fun getUploadGames(): Array<Game>

    @Query("SELECT * FROM game_instances WHERE eternal NOT null")
    // Gets all the eternals with the game data of their games
    fun getEternalList(): Array<GameInstance>

    @Query("SELECT * FROM game_instances WHERE gameID = :gameID AND playerName = :player AND charName = :character")
    fun findGameInstance(gameID: String, player: String, character: String): GameInstance

    @Transaction
    @Query("SELECT * FROM players WHERE playerName = :playerName")
    // Gets player game data
    fun getPlayerWithInstance(playerName: String): Array<PlayerWithInstance>

    @Transaction
    @Query("SELECT * FROM characters WHERE charName = :charName")
    // Gets character game data
    fun getCharacterWithInstance(charName: String): Array<CharacterWithInstance>

    @Transaction
    @Query("SELECT * FROM games WHERE gameID = :gameID")
    // Gets data for a single game
    fun getGameWithInstance(gameID: String): Array<GameWithInstance>
}