package com.example.foursoulsstatistics.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
    // Holds all the different tables in the database
        Player::class,
        CharEntity::class,
        Game::class,
        GameInstance::class
    ],
    version = 1,
    // Current database version, needs to be changed on updates
    exportSchema = false
)
abstract class GameDataBase: RoomDatabase() {
    // All the database access objects in the database
    abstract val gameDAO: GameDAO
    companion object{
        @Volatile
        // Volatile so it is updated in all threads when it is updated once
        // This means the database is singular, and can only be created once
        private var instance: GameDataBase? = null
        // Initialises the instance variable as null

        fun getDataBase(context: Context): GameDataBase{
        // Function any class can call to access the database
            val tempInstance = instance
            // Sets a temporary variable to the current instance value
            if (tempInstance != null){
            // If it is non-null, a database exists
                return tempInstance
                // Return that database
            }
            else{
            // Otherwise if there is no database
                synchronized(this){
                // Ensure that only one database is created at a time
                    val dataBase = Room.databaseBuilder(
                        context.applicationContext,
                        GameDataBase::class.java,
                        "game_database"
                    ).build()
                    // Use the database builder to create the database
                instance = dataBase
                // Set the instance variable
                return instance as GameDataBase
                // Return the new database
                }
            }
        }
    }
}