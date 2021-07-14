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
        var view = super.getView(position, convertView, parent)
        view = updateView(view)
        return view
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View = super.getDropDownView(position, convertView, parent)
        view = updateView(view)
        return view
    }
    private fun updateView(view: View): View{
        val textView = view.findViewById<TextView>(android.R.id.text1)
        val textString = textView.text.toString()
        textView.text = TextHandler.capitalise(textString)
        textView.typeface = font
        return view
    }
}