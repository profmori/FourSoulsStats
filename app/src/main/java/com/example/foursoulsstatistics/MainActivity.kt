package com.example.foursoulsstatistics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goto_dataEntry(view: View) {
        val goToData = Intent(this, enterData::class.java)
        startActivity(goToData)
    }
}
