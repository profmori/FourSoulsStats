package com.example.foursoulsstatistics.online_database

data class OnlineGameInstance(
    val groupID: String,
    val gameID: String,
    val gameSize: Int,
    val treasureNum: Int,
    val playerName: String,
    val charName: String,
    val eternal: String?,
    val souls: Int,
    val winner: Boolean
)
