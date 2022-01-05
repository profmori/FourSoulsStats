package com.profmori.foursoulsstatistics.custom_adapters

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.profmori.foursoulsstatistics.data_handlers.TextHandler

class DropDownAdapter(context: Context, dropdownArray: Array<String>, val font: Typeface) :
    ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, dropdownArray) {
    // Drop down extends the array adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // When getting the view
        var view = super.getView(position, convertView, parent)
        // Do everything as normal
        view = updateView(view)
        // Update it
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View = super.getDropDownView(position, convertView, parent)
        // When getting spinner dropdowns
        view = updateView(view)
        // Update the view
        return view
    }

    private fun updateView(view: View): View {
        // Function to update the view
        val textView = view.findViewById<TextView>(android.R.id.text1)
        // The equivalent text view is here
        val textString = textView.text.toString()
        // Gets the text string from the view
        textView.text = TextHandler.capitalise(textString)
        // Capitalise the text
        textView.typeface = font
        // Set the font
        return view
    }
}