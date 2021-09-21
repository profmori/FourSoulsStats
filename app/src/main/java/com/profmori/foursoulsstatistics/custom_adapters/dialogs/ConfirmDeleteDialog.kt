package com.profmori.foursoulsstatistics.custom_adapters.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmDeleteDialog(private val activityContext: Context, private val font: Typeface): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Create the dialog
            val titleText = TextView(context)
            // Create the title text object
            titleText.setText(R.string.settings_delete_data)
            titleText.typeface = font
            // Set the title text and font
            builder.setCustomTitle(titleText)
                .setPositiveButton(R.string.settings_cancel_delete){ _, _ ->
                    dismiss()
                }
                // When you click change just close the dialog
                .setNegativeButton(R.string.settings_confirm_delete){ _, _ ->
                    OnlineDataHandler.saveGames(activityContext)
                    // Save all data online if possible
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = GameDataBase.getDataBase(activityContext)
                        // Open the local database
                        database.clearAllTables()
                        // Clear all the data stored
                    }
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