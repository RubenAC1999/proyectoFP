package com.example.gametracker.data.repository

import com.example.gametracker.data.remote.RetrofitInstance
import com.example.gametracker.model.GameModel

class GameRepository {
    suspend fun fetchLastestGames(apiKey: String): List<GameModel.Game> {
        return try {
            val response = RetrofitInstance.api.getGames(apiKey)
            response.results
        } catch (e: Exception) {
            emptyList()
        }
    }
}