package com.example.foursoulsstatistics.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foursoulsstatistics.R

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
                    )
                        //.createFromAsset("database/characters.db")
                        .build()
                    // Use the database builder to create the database
                    val charList = arrayOf(
                        // Base Characters
                        CharEntity("blue baby", R.drawable.blue_baby, R.drawable.blue_baby_alt,"base"),
                        CharEntity("cain", R.drawable.cain, R.drawable.cain, "base"),
                        CharEntity("eden", R.drawable.eden, R.drawable.eden, "base"),
                        CharEntity("eve", R.drawable.eve, R.drawable.eve, "base"),
                        CharEntity("the forgotten", R.drawable.forgotten, R.drawable.forgotten, "base"),
                        CharEntity("isaac", R.drawable.isaac, R.drawable.isaac_alt, "base"),
                        CharEntity("judas", R.drawable.judas, R.drawable.judas_alt, "base"),
                        CharEntity("lazarus", R.drawable.lazarus, R.drawable.lazarus, "base"),
                        CharEntity("lilith",R.drawable.lilith, R.drawable.lilith_alt, "base"),
                        CharEntity("maggy",R.drawable.maggy,R.drawable.maggy,"base"),
                        CharEntity("samson",R.drawable.maggy,R.drawable.maggy,"base")
                        )
                instance = dataBase
                // Set the instance variable
                return instance as GameDataBase
                // Return the new database
                }
            }
        }
    }
}