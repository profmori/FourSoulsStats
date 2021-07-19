package com.example.foursoulsstatistics.custom_adapters

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.foursoulsstatistics.R

class ChangeGroupDialog(private val ctxt: Context, private val groupEntry: EditText, private val oldID: String, private val font: Typeface): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val titleText = TextView(ctxt)
            titleText.setText(R.string.settings_overwrite_data)
            titleText.typeface = font
            //titleText.textSize = 20f
            builder.setCustomTitle(titleText)
                .setPositiveButton(R.string.settings_confirm_overwrite){ _, _ ->}
                .setNegativeButton(R.string.settings_cancel_overwrite
                ) { _, _ ->
                    groupEntry.setText(oldID)
                }
            // Create the AlertDialog object and return it
            val dialog = builder.create()

            dialog.show()

            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).typeface = font
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).typeface = font
            // Set the button fonts
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}