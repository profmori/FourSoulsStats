package com.profmori.foursoulsstatistics.statistics_pages
import android.content.Context
import com.profmori.foursoulsstatistics.R

class TableHeader(val headerName: String, var sortPriority: Int, var sortDescending: Boolean = true)
{
    fun getSortButton(): Int {
        return if (sortDescending){
            R.drawable.cross_inverted
        }else{
            R.drawable.cross
        }
    }
}