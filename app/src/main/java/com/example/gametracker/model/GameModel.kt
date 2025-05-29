package com.example.gametracker.model

import com.google.gson.annotations.SerializedName

class GameModel {

    data class GameResponse(
        val results: List<Game>
    )

    data class Game(
        val id: Int,
        val name: String,
        @SerializedName("description_raw")
        val description: String,
        @SerializedName("background_image")
        val imageUrl: String?,
        val rating: Float,
        val metacritic: Int?,
        val developers: List<Developer>,
        val publishers: List<Publisher>,
        val genres: List<Genre>,
        val website: String?,
        val released: String?
    )

    data class Developer(val id: Int, val name: String)
    data class Publisher(val id: Int, val name: String)
    data class Genre(val id: Int, val name: String)

}