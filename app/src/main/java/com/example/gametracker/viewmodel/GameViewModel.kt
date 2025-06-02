package com.example.gametracker.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gametracker.data.remote.RetrofitInstance
import com.example.gametracker.data.repository.GameRepository
import com.example.gametracker.model.GameModel
import com.example.gametracker.model.Screenshot
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _topRatedGames = mutableStateOf<List<GameModel.Game>>(emptyList())
    val topRatedGames: State<List<GameModel.Game>> get() = _topRatedGames

    private val _popularGames = mutableStateOf<List<GameModel.Game>>(emptyList())
    val popularGames: State<List<GameModel.Game>> get() = _popularGames

    private val _gameDetail = mutableStateOf<GameModel.Game?>(null)
    val gameDetail: State<GameModel.Game?> get() = _gameDetail

    private val _screenshots = mutableStateOf<List<Screenshot>>(emptyList())
    val screenshots: State<List<Screenshot>> get() = _screenshots

    private val _genreGamesMap = mutableStateOf<Map<String, List<GameModel.Game>>>(emptyMap())
    val genreGamesMap: State<Map<String, List<GameModel.Game>>> get() = _genreGamesMap

    private val _searchResults = mutableStateOf<List<GameModel.Game>>(emptyList())
    val searchResults: State<List<GameModel.Game>> get() = _searchResults

    private val _gamesByYear = mutableStateOf<Map<String, List<GameModel.Game>>>(emptyMap())
    val gamesByYear: State<Map<String, List<GameModel.Game>>> get() = _gamesByYear


    fun loadTopRatedGames(apiKey: String) {
        viewModelScope.launch {
            try {
                val result = repository.getTopRatedGames(apiKey)
                _topRatedGames.value = result
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error loading top rated games: ${e.message}")
            }
        }
    }

    fun loadPopularGames(apiKey: String) {
        viewModelScope.launch {
            try {
                val result = repository.getPopularGames(apiKey)
                _popularGames.value = result
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error loading popular games: ${e.message}")
            }
        }
    }

    fun loadGameDetail(apiKey: String, gameId: Int) {
        viewModelScope.launch {
            try {
                val detail = repository.getGameDetail(apiKey, gameId)
                _gameDetail.value = detail
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error loading game detail: ${e.message}")
            }
        }
    }

    fun loadGameScreenshots(apiKey: String, gameId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getGameScreenshots(apiKey, gameId)
                _screenshots.value = result
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error loading screenshots: ${e.message}")
            }
        }
    }

    fun getGameById(id: Int): GameModel.Game? {
        return topRatedGames.value.find { it.id == id }
            ?: popularGames.value.find { it.id == id }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun loadGamesByGenres(apiKey: String) {
        if (_genreGamesMap.value.isNotEmpty()) return

        viewModelScope.launch {
            val genres = listOf("action", "adventure", "indie", "role-playing-games-rpg")
            val results = mutableMapOf<String, List<GameModel.Game>>()

            for (genre in genres) {
                try {
                    val response = RetrofitInstance.api.getGamesByGenre(apiKey, genre)
                    val displayName = when (genre) {
                        "r" +
                                "ole-playing-games-rpg" -> "RPG"

                        else -> genre.replaceFirstChar { it.uppercase() }
                    }
                    results[displayName] = response.results
                } catch (e: Exception) {
                    Log.e("GameViewModel", "Error al cargar juegos de $genre: ${e.message}")
                }
            }

            _genreGamesMap.value = results
        }
    }


    fun searchGames(apiKey: String, query: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchGames(apiKey, query)
                _searchResults.value = response.results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }

    fun loadGamesByYear(apiKey: String) {
        viewModelScope.launch {
            val years = listOf("2024", "2023", "2022")
            val results = mutableMapOf<String, List<GameModel.Game>>()

            for (year in years) {
                try {
                    val ordering = if (year == "2024") "-added" else "-metacritic"
                    val dates = "$year-01-01,$year-12-31"

                    val response = RetrofitInstance.api.getGamesByYear(
                        apiKey = apiKey,
                        dates = dates,
                        ordering = ordering,
                        pageSize = 20
                    )

                    val sortedGames = if (year == "2024") {
                        response.results.sortedByDescending { it.added }
                    } else {
                        response.results.filter { it.metacritic != null }
                            .sortedByDescending { it.metacritic }
                    }

                    Log.d("GameViewModel", "Cargados ${sortedGames.size} juegos para $year")
                    results["Mejores de $year"] = sortedGames

                } catch (e: Exception) {
                    Log.e("GameViewModel", "Error al cargar juegos del año $year: ${e.message}")
                }
            }

            _gamesByYear.value = results
        }
    }

}
