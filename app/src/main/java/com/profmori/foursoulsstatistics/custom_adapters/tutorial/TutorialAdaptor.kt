package com.profmori.foursoulsstatistics.custom_adapters.tutorial

import android.app.Activity
import android.view.View
import android.widget.ScrollView
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ActionItemTarget
import com.github.amlcurran.showcaseview.targets.PointTarget
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.profmori.foursoulsstatistics.R

class TutorialAdaptor {
    companion object {
        fun runTutorialStep(
            parentActivity: Activity,
            targetItem: View,
            tutorialText: Int,
            nextStep: Any? = null,
            scrollView: ScrollView? = null
        ): ShowcaseView.Builder {
            println("${targetItem.top} ${targetItem.bottom}")
            val tutorialStep =
                ShowcaseView.Builder(parentActivity).setTarget(ViewTarget(targetItem))
                    .setStyle(R.style.CustomShowcaseTheme).setContentText(tutorialText)
                    .hideOnTouchOutside()
            if (scrollView != null) {
                tutorialStep.setShowcaseEventListener(
                    TutorialScrollEventListener(
                        nextStep, parentActivity, scrollView, targetItem
                    )
                )
            } else {
                tutorialStep.setShowcaseEventListener(
                    TutorialShowcaseEventListener(
                        nextStep, parentActivity
                    )
                )
            }
            return tutorialStep
        }
    }
}