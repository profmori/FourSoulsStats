package com.profmori.foursoulsstatistics.custom_adapters.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.profmori.foursoulsstatistics.EditGames
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.database.GameDataBase
import com.profmori.foursoulsstatistics.online_database.OnlineDataHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteGameEdit(val gameID: String, val parent: Activity, val font: Typeface): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Create the dialog
            val titleText = TextView(context)
            // Create the title text object
            titleText.setText(R.string.adjust_check_delete)
            titleText.typeface = font
            // Set the title text and font
            builder.setCustomTitle(titleText)
                .setPositiveButton(R.string.adjust_confirm_delete){ _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        OnlineDataHandler.deleteOnlineGameInstances(gameID)
                        // Clear the online data
                        val gameDatabase: GameDataBase = GameDataBase.getDataBase(parent)
                        // Gets the game database
                        val gameDao = gameDatabase.gameDAO
                        // Gets the database access object
                        gameDao.clearSingleGame(gameID)
                        gameDao.clearSingleGameInstance(gameID)
                        // Clear the local versions of the data
                        withContext(Dispatchers.Main){
                            val backToList = Intent(parent, EditGames::class.java)
                            parent.startActivity(backToList)
                            // Go back the list of games, reloading it
                        }
                    }
                }
                // When you click ignore changes
                .setNegativeButton(R.string.adjust_cancel_delete
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