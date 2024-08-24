package com.profmori.foursoulsstatistics.custom_adapters.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannedString
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.profmori.foursoulsstatistics.R

class PatchNotesDialog(
    private val patchNotes: SpannedString,
    private val fonts: Map<String, Typeface>
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val notesView = layoutInflater
                .inflate(R.layout.patch_notes_dialog, view as ViewGroup?, false)
            // Set up the patch notes view

            val body = notesView.findViewById<TextView>(R.id.patchNotesBody)
            // Find the main body patch notes view
            body.text = patchNotes
            // Sets the text of the body to the patch notes

            builder.setView(notesView)
                .setPositiveButton(R.string.generic_dismiss) { _, _ -> }
            // Set the positive button to return the name to the interface
            val dialog = builder.create()
            // Create the AlertDialog object and return it

            dialog.show()
            // Show the dialog

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).typeface = fonts["body"]
            // Set the button font
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}