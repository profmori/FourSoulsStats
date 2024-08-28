package com.profmori.foursoulsstatistics.custom_adapters.tutorial

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.amlcurran.showcaseview.ShowcaseView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.data_handlers.SettingsHandler

class TutorialDialog(
    private val context: Context,
    private val font: Typeface,
    val nextStep: ShowcaseView.Builder,
    private val fragmentManager: FragmentManager
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Create the dialog
            val titleText = TextView(context)
            // Create the title text object
            titleText.setText(R.string.tutorial_show_tutorial)
            titleText.typeface = font
            // Set the title text and font
            builder.setCustomTitle(titleText).setPositiveButton(R.string.yes) { _, _ ->
                    SettingsHandler.setTutorial(context, true)
                }
                // When you click change just close the dialog
                .setNegativeButton(
                    R.string.no
                ) { _, _ ->
                    SettingsHandler.setTutorial(context, false)
                }
            // When you click the reset button set it to the old id
            val dialog = builder.create()
            // Create the AlertDialog object and return it

            dialog.show()
            // Show the dialog

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).typeface = font
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).typeface = font
            // Set the button fonts

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")


    }

    fun showDialog() {
        this.show(fragmentManager, "tutorialDialog")
    }
}