package com.example.foursoulsstatistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class enterResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_result)

        val playerList = intent.getSerializableExtra("players")
        // Pull the player list from the extra pass in the intent

        val gameId = System.currentTimeMillis()
        // Get the current time for a unique game identifier

    }
}