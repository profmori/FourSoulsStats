package com.profmori.foursoulsstatistics.custom_adapters

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.profmori.foursoulsstatistics.CustomCardEntry
import com.profmori.foursoulsstatistics.R


class CustomCharDialog(private var returnInterface: CustomCardEntry.ConfirmInterface, private val font: Typeface): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val textInputView = LayoutInflater.from(context).inflate(R.layout.new_char_dialog, view as ViewGroup?, false)
            // Set up the input

            val textInput = textInputView.findViewById<EditText>(R.id.customCharacterName)
            // Find the text input box
            textInput.typeface = font
            // Set the text input font

            builder.setView(textInputView)
                .setPositiveButton(R.string.custom_add_character){ _, _ ->
                    returnInterface.onTextEntered(textInput.text.toString())
                }
                // Set the positive button to return the name to the interface
                .setNegativeButton(R.string.custom_cancel) { _, _ ->}
            // Set the negative button up to do nothing
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