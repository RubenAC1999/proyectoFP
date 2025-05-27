package com.example.gametracker.model

import com.google.gson.annotations.SerializedName

class GameModel {

    data class GameResponse(
        val results: List<Game>
    )

    data class Game(
        val id: Int,
        val name: String,
        @SerializedName("background_image")
        val imageUrl: String?,
        val developer: String?
    )
}