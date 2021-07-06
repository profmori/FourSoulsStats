package com.example.foursoulsstatistics

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class DropDownAdapter(context: Context, dropdownArray: Array<String>, val font: Typeface):
    ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, dropdownArray) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.typeface = font
        val textString = textView.text.toString()
        textView.text = TextHandler.capitalise(textString)
        return view
    }
}