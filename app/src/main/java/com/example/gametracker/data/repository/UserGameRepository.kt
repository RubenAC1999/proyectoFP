package com.example.gametracker.data.repository

import com.example.gametracker.model.GameEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserGameRepository {
    private val db = Firebase.firestore

    fun addGamesToUserList(
        userId: String,
        game: GameEntry,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (userId.isBlank()) {
            onError(IllegalArgumentException("El userId está vacío o es inválido"))
            return
        }

        db.collection("users")
            .document(userId)
            .collection("gameList")
            .document(game.id)
            .set(game)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }


    fun getGameStatusCounts(userId: String, onResult: (completed: Int, pending: Int, dropped: Int) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("gameList")
            .get()
            .addOnSuccessListener { snapshot ->
                var completed = 0
                var pending = 0
                var dropped = 0

                for (doc in snapshot.documents) {
                    when (doc.getString("status")) {
                        "completado" -> completed++
                        "pendiente" -> pending++
                        "dropeado" -> dropped++
                    }
                }

                onResult(completed, pending, dropped)
            }
    }

    fun getGamesForUser(userId: String, onResult: (List<GameEntry>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .collection("gameList")
            .get()
            .addOnSuccessListener { result ->
                val games = result.documents.mapNotNull { document ->
                    document.toObject(GameEntry::class.java)?.copy(id = document.id)
                }
                onResult(games)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

}