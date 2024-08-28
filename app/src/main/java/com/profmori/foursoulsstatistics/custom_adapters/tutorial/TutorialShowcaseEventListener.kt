package com.profmori.foursoulsstatistics.custom_adapters.tutorial

import android.app.Activity
import android.view.MotionEvent
import android.widget.Button
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
                if (SettingsHandler.getTutorial(parent)[0]) (nextStep as ShowcaseView.Builder).build()
            }

            is TutorialDialog -> {
                if (SettingsHandler.getTutorial(parent)[1]) {
                    (nextStep as TutorialDialog).showDialog()
                }

                if (SettingsHandler.getTutorial(parent)[0]) {
                    (nextStep as TutorialDialog).nextStep.build()
                }
            }

            is MainActivity -> {
                SettingsHandler.setInputLock(nextStep as MainActivity, false)
                val settingsButton =
                    (nextStep as MainActivity).findViewById<Button>(R.id.mainSettings)
                settingsButton.performClick()
            }
        }
    }

    override fun onShowcaseViewShow(showcaseView: ShowcaseView) {
        showcaseView.hideButton()
    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {
    }
}