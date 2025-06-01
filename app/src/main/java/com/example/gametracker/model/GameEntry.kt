package com.example.gametracker.model

data class GameEntry (
   val id: String = "",
   val gameId: Int = 0,
    val name: String = "",
    val imageUrl: String? = "",
    val status: String = "",
    val rating: Int? = null,
    val hoursPlayed: Int = 0,
    val genres: List<String>? = null,
    val addedAt: Long = System.currentTimeMillis(),
    val review: String? = null
)
