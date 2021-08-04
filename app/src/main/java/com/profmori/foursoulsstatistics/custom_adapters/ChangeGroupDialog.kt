package com.profmori.foursoulsstatistics.custom_adapters

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.profmori.foursoulsstatistics.R

class ChangeGroupDialog(private val groupEntry: EditText, private val oldID: String, private val font: Typeface): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Create the dialog
            val titleText = TextView(context)
            // Create the title text object
            titleText.setText(R.string.settings_overwrite_data)
            titleText.typeface = font
            // Set the title text and font
            //titleText.textSize = 20f
            builder.setCustomTitle(titleText)
                .setPositiveButton(R.string.settings_confirm_overwrite){ _, _ ->}
                // When you click change just close the dialog
                .setNegativeButton(R.string.settings_cancel_overwrite
                ) { _, _ ->
                    groupEntry.setText(oldID)
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
}