package com.example.gametracker.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gametracker.data.remote.RetrofitInstance
import com.example.gametracker.model.GameModel
import kotlinx.coroutines.launch

class GameViewModel: ViewModel() {
    private val _topRatedGames = mutableStateOf<List<GameModel.Game>>(emptyList())
    val topRatedGames: State<List<GameModel.Game>> get() = _topRatedGames

    private val _popularGames = mutableStateOf<List<GameModel.Game>>(emptyList())
    val popularGames: State<List<GameModel.Game>> get() = _popularGames

    fun loadTopRateGames(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTopRatedGames(
                    apiKey = apiKey,
                    ordering = "-metacritic"
                )
                // Log para inspeccionar la respuesta
                if (response.results.isNotEmpty()) {
                    // Esto imprimirá la respuesta completa de la API, incluyendo el campo developer
                    Log.d("GameViewModel", "Respuesta de juegos mejor valorados: ${response.results}")
                }

                _topRatedGames.value = response.results
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error al cargar los más valorados ${e.message}")
            }
        }
    }

    fun loadMostPopularGames(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPopularGames(
                    apiKey = apiKey,
                    ordering = "-added"
                )
                _popularGames.value = response.results
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error al cargar los más populares: ${e.message}")
            }
        }
    }
}