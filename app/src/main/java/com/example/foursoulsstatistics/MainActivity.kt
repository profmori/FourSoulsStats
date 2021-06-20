package com.example.foursoulsstatistics

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        //val dpHeight = displayMetrics.heightPixels / displayMetrics.density
        //val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        setContentView(R.layout.activity_main)
    }

    fun goto_dataEntry(view: View) {
        val goToData = Intent(this, enterData::class.java)
        startActivity(goToData)
    }

    fun goto_statsPage(view: View) {
        //val goToStats = Intent(this, statsView::class.java)
        //startActivity(goToStats)
    }

    fun goto_settings(view: View) {
        //val goToSettings = Intent(this, settingsEdit::class.java)
        //startActivity(goToSettings)
    }
}
