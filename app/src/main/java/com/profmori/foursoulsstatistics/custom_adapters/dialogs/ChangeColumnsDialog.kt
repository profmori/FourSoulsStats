package com.profmori.foursoulsstatistics.custom_adapters.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannedString
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R
import com.profmori.foursoulsstatistics.statistics_pages.HeaderChecklistAdaptor
import com.profmori.foursoulsstatistics.statistics_pages.StatsTable
import com.profmori.foursoulsstatistics.statistics_pages.TableHeader

class ChangeColumnsDialog(
    private val table: StatsTable,
    private val fonts: Map<String, Typeface>
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val dialogLayout = layoutInflater
                .inflate(R.layout.choose_header_dialog, view as ViewGroup?, false)
            // Set up the header choice view

            val title = dialogLayout.findViewById<TextView>(R.id.headerSelectTitle)
            val body = dialogLayout.findViewById<RecyclerView>(R.id.headerSelectList)
            // Find the two components of the view
            val checkListAdaptor = HeaderChecklistAdaptor(table)
            body.adapter = checkListAdaptor
            body.layoutManager = LinearLayoutManager(context)
            title.typeface = fonts["title"]
            // Set the title font correctly

            builder.setView(dialogLayout)
                .setPositiveButton(R.string.generic_dismiss) {_,_ ->}
            // Set the positive button to return the name to the interface
            val dialog = builder.create()
            // Create the AlertDialog object and return it



            dialog.show()
            // Show the dialog

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).typeface = fonts["body"]
            // Set the button font
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                if (checkListAdaptor.checkedArray.size == 3){
                    var newHeaderArray = arrayOf(table.headers[0])
                    var headerCount = 1
                    table.getAllHeaders().forEach {headerString ->
                        if (headerString in checkListAdaptor.checkedArray){
                            newHeaderArray += TableHeader(headerString, table.headers[headerCount].sortPriority)
                            headerCount += 1
                        }
                    }
                    table.headers = newHeaderArray
                    table.sortData()
                    dialog.dismiss()
                }else{
                    val wrongCountHeader = Toast.makeText(
                        context,
                        R.string.select_3_headers,
                        Toast.LENGTH_LONG
                    )
                    // Create the Toast
                    wrongCountHeader.show()
                    // Show the Toast
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}