package com.profmori.foursoulsstatistics.statistics_pages

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.profmori.foursoulsstatistics.R

class StatsMenuAdapter(
    private val menuItems: Array<StatsPageProperties>,
    private val font: Typeface,
    private val background: Int
) :
    RecyclerView.Adapter<StatsMenuAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val menuButton: AppCompatButton = itemView.findViewById(R.id.statsMenuButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsMenuAdapter.ViewHolder {
        val context = parent.context
        // Gets the context of the view

        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.stats_menu_button, parent, false)
        // Return a new holder instance

        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: StatsMenuAdapter.ViewHolder, position: Int) {
        val buttonType = menuItems[position]

        val menuButton = viewHolder.menuButton

        menuButton.typeface = font
        menuButton.setBackgroundResource(background)
        menuButton.setText(buttonType.buttonName)

        menuButton.setOnClickListener {
            // When the community eternal stats button is clicked
            val statsPage = Intent(menuButton.context, ViewStatisticsPage::class.java)
            statsPage.putExtra("firstColumn", buttonType.firstColumn)
            statsPage.putExtra("online", buttonType.online)
            statsPage.putExtra("coop", buttonType.coop)
            startActivity(menuButton.context, statsPage, null)
            // Go to the appropriate stats page
        }
    }

    override fun getItemCount(): Int {
        return menuItems.size
        // Returns the player list size element
    }
}