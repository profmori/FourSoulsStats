package com.profmori.foursoulsstatistics.custom_adapters.tutorial

import android.app.Activity
import android.view.View
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.profmori.foursoulsstatistics.R

class TutorialAdaptor {
    companion object {
        fun runTutorialStep(
            parentActivity: Activity,
            targetItem: View,
            tutorialText: Int,
            nextStep: Any? = null
        ): ShowcaseView.Builder {
            val tutorialStep =
                ShowcaseView.Builder(parentActivity)
                    .setTarget(ViewTarget(targetItem))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setContentText(tutorialText)
                    .setShowcaseEventListener(TutorialShowcaseEventListener(nextStep, parentActivity))
                    .hideOnTouchOutside()
            return tutorialStep
        }
    }
}