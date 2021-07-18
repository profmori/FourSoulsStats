package com.example.foursoulsstatistics.online_database

data class OnlineGameInstance(
    val groupID: String = "",
    val gameID: String = "",
    val gameSize: Int = 0,
    val treasureNum: Int = -1,
    val playerName: String = "",
    val charName: String = "",
    val eternal: String? = "",
    val souls: Int = -1,
    val winner: Boolean = false
)

data class OnlineGroupID(
    val id: String = ""
)
