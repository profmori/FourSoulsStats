package com.profmori.foursoulsstatistics.custom_adapters.tutorial

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import com.github.amlcurran.showcaseview.OnShowcaseEventListener
import com.github.amlcurran.showcaseview.ShowcaseView
import com.profmori.foursoulsstatistics.EditSettings
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler

class TutorialScrollEventListener(
    private var nextStep: Any?,
    private val parent: Activity,
    private val scrollView: ScrollView,
    private val targetItem: View
) : OnShowcaseEventListener {

    override fun onShowcaseViewHide(showcaseView: ShowcaseView) {
        if (SettingsHandler.getTutorial(parent)[0]) {
            val objectAnimator =
                ObjectAnimator.ofInt(scrollView, "scrollY", scrollView.scrollY, targetItem.top)
                    .setDuration(500)
            // Scroll to the chosen y position in 500 milliseconds
            objectAnimator.start()
            // Start the animation
        }
    }

    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView) {
        when (nextStep) {
            is ShowcaseView.Builder -> {
                if (SettingsHandler.getTutorial(parent)[0]) {
                    (nextStep as ShowcaseView.Builder).build()
                }
            }

            is EditSettings -> {
                SettingsHandler.setTutorial(parent, false)
                val returnButton = parent.findViewById<Button>(R.id.settingsMainButton)
                // Get the return button
                returnButton.performClick()
            }

        }
    }

    override fun onShowcaseViewShow(showcaseView: ShowcaseView) {
        showcaseView.hideButton()
    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {
    }
}