package com.profmori.foursoulsstatistics.online_database

import com.google.firebase.firestore.PropertyName

data class OnlineGameInstance(

    @PropertyName("groupID")
    val groupID: String = "",
    @PropertyName("gameID")
    val gameID: String = "",
    @PropertyName("gameSize")
    val gameSize: Int = 0,
    @PropertyName("treasureNum")
    val treasureNum: Int = -1,
    @PropertyName("playerName")
    val playerName: String = "",
    @PropertyName("charName")
    val charName: String = "",
    @PropertyName("eternal")
    val eternal: String? = "",
    @PropertyName("souls")
    val souls: Int = -1,
    @PropertyName("winner")
    val winner: Boolean = false
)

data class OnlineGroupID(
    @PropertyName("id")
    val id: String = ""
)
