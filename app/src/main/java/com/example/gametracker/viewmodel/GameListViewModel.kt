package com.example.gametracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gametracker.data.repository.UserGameRepository
import com.example.gametracker.model.GameEntry
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameListViewModel: ViewModel() {

    private val _userGameList = MutableStateFlow<List<GameEntry>>(emptyList())
    val userGameList: StateFlow<List<GameEntry>> = _userGameList

    private val repository = UserGameRepository()

    private val _completed = MutableStateFlow(0)
    private val _pending = MutableStateFlow(0)
    private val _dropped = MutableStateFlow(0)

    val completed: StateFlow<Int> = _completed
    val pending: StateFlow<Int> = _pending
    val dropped: StateFlow<Int> = _dropped


    fun loadUserStats(userId: String) {
        repository.getGameStatusCounts(userId) { c, p, d ->
            _completed.value = c
            _pending.value = p
            _dropped.value = d
        }
    }

    fun addGame(userId: String?, game: GameEntry, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        if (userId.isNullOrBlank()) {
            onError(IllegalArgumentException("El UID del usuario es nulo o vacío"))
            return
        }

        repository.addGamesToUserList(userId, game, {
            loadGamesForUser(userId)
            loadUserStats(userId)
            onSuccess()
        }, onError)
    }


    fun loadGamesForUser(userId: String) {
        repository.getGamesForUser(userId) { games ->
            _userGameList.value = games
        }
    }

    fun deleteGameById(gameId: String) {
        val uid = Firebase.auth.currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("gameList")
            .document(gameId)
            .delete()
            .addOnSuccessListener {
                Log.d("GameListViewModel", "Juego eliminado correctamente")
                loadGamesForUser(uid)
            }
            .addOnFailureListener { e ->
                Log.e("GameListViewModel", "Error al eliminar el juego", e)
            }
    }




}