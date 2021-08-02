package com.profmori.foursoulsstatistics.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "players")
// Creates a table to store all players
data class Player(
    @PrimaryKey(autoGenerate = false)
    val playerName: String
)

@Entity(tableName = "characters")
// Creates a table to store all characters, with the requisite image files and game editions (for filtering)
data class CharEntity(
    @PrimaryKey(autoGenerate = false)
    val charName: String,
    var image: Int,
    val imageAlt: Int?,
    val edition: String
)

@Entity(tableName = "games")
// Table to store the individual game details
data class Game(
    @PrimaryKey(autoGenerate = false)
    val gameID: String,
    val playerNo: Int,
    val treasureNo: Int,
    val uploaded: Boolean
)

@Entity(tableName = "game_instances")
// Table to store the instances of each game
data class GameInstance(
    @PrimaryKey(autoGenerate = true)
    val instanceID: Int,
    val gameID: String,
    val playerName: String,
    val charName: String,
    val eternal: String?,
    val souls: Int,
    val winner: Boolean
)

data class PlayerWithInstance(
    @Embedded val player: Player,
    @Relation(
        parentColumn = "playerName",
        entityColumn = "playerName"
    )
    val gameInstances: List<GameInstance>
)

data class CharacterWithInstance(
    @Embedded val character: CharEntity,
    @Relation(
        parentColumn = "charName",
        entityColumn = "charName"
    )
    val gameInstances: List<GameInstance>
)

data class GameWithInstance(
    @Embedded val game: Game,
    @Relation(
        parentColumn = "gameID",
        entityColumn = "gameID"
    )
    val gameInstances: List<GameInstance>
)