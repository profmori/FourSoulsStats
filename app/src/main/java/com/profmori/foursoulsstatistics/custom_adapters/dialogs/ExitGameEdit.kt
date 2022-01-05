package com.profmori.foursoulsstatistics.custom_adapters.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.profmori.foursoulsstatistics.R

class ExitGameEdit(val parent: Activity, val font: Typeface) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Create the dialog
            val titleText = TextView(context)
            // Create the title text object
            titleText.setText(R.string.adjust_confirm_exit)
            titleText.typeface = font
            // Set the title text and font
            builder.setCustomTitle(titleText)
                .setPositiveButton(R.string.adjust_ignore_changes) { _, _ ->
                    parent.finish()
                }
                // When you click ignore changes
                .setNegativeButton(
                    R.string.adjust_cancel_exit
                ) { _, _ ->
                    dismiss()
                }
            // When you click the cancel button do nothing
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
}