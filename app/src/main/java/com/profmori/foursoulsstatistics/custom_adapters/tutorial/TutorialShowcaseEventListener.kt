package com.profmori.foursoulsstatistics.custom_adapters.tutorial

import android.app.Activity
import android.view.MotionEvent
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.profmori.foursoulsstatistics.MainActivity
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler

class TutorialShowcaseEventListener(private var nextStep: Any?, val parent: Activity) :
    OnShowcaseEventListener {

    override fun onShowcaseViewHide(showcaseView: ShowcaseView) {
    }

    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView) {
        when (nextStep) {
            is ShowcaseView.Builder -> {
                (nextStep as ShowcaseView.Builder).build()
            }

            is DialogFragment -> {
                println("tutorial menu")
            }

            is MainActivity -> {
                SettingsHandler.setInputLock(nextStep as MainActivity, false)
                val settingsButton = (nextStep as MainActivity).findViewById<Button>(R.id.mainSettings)
                settingsButton.performClick()
            }
        }
    }

    override fun onShowcaseViewShow(showcaseView: ShowcaseView) {
        showcaseView.hideButton()

//        Looper.myLooper()?.let { Handler(it).postDelayed({showcaseView.hide()}, 10000) }
    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {
    }
}