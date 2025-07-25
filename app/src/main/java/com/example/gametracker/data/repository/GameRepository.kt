package com.example.gametracker.data.repository

import android.util.Log
import com.example.gametracker.data.remote.RetrofitInstance
import com.example.gametracker.data.remote.RetrofitInstance.api
import com.example.gametracker.model.GameModel
import com.example.gametracker.model.Screenshot

class GameRepository {
        suspend fun getTopRatedGames(apiKey: String): List<GameModel.Game> {
            return try {
                val response = RetrofitInstance.api.getTopRatedGames(apiKey)
                response.results
            } catch (e: Exception) {
                emptyList()
            }
        }

        suspend fun getPopularGames(apiKey: String): List<GameModel.Game> {
            return try {
                val response = RetrofitInstance.api.getPopularGames(apiKey)
                response.results
            } catch (e: Exception) {
                emptyList()
            }
        }

        suspend fun getGameDetail(apiKey: String, gameId: Int): GameModel.Game? {
            return try {
                RetrofitInstance.api.getGameDetail(gameId, apiKey)
            } catch (e: Exception) {
                null
            }
        }

        suspend fun getGameScreenshots(apiKey: String, gameId: Int): List<Screenshot> {
            return try {
                RetrofitInstance.api.getGameScreenshots(gameId, apiKey).results
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun searchGames(query: String, apiKey: String): List<GameModel.Game> {
        val cleanQuery = query.trim().replace(" ", "+")  // ← AÑADE ESTA LÍNEA
        Log.d("GameRepository", "Buscando juegos con: $cleanQuery")

        val response = api.searchGames(apiKey, cleanQuery)  // ← USA cleanQuery

        return response.results
    }


}
