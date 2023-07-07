package com.profmori.foursoulsstatistics.statistics_pages

data class StatsPageProperties(
    val buttonName: Int,
    val firstColumn: String,
    val online: Boolean = false,
    val coop: Boolean = false
)
