package com.example.gametracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gametracker.data.repository.UserRepository
import com.example.gametracker.model.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user
    private val _allUsers = MutableStateFlow<List<UserModel>>(emptyList())
    val allUsers: StateFlow<List<UserModel>> = _allUsers

    suspend fun createUserInFirestore(user: FirebaseUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userModel = UserModel(
            displayName = user.displayName?: "Nombre de usuario",
            email = user.email?: "",
            role = "casual",
            bio = "Bienvenido a GameTracker!",
            profilePicUrl = user.photoUrl?.toString() ?: "",
            createdAt = Timestamp.now(),
            followersCount = 0,
            followingCount = 0,
            isBanned = false,
            bannedUntil = null,
            blockMessage = "",
            warningMessage = ""
        )
        userRepository.createUserInFirestore(userModel, user.uid, onSuccess, onFailure)
    }

    fun getUserData(userId: String, onResult: (UserModel?) -> Unit, onError: (Exception) -> Unit) {
        userRepository.getUserData(userId, onResult, onError)
    }

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            userRepository.getUserData(
                userId,
                onResult = { userModel ->
                    _user.value = userModel
                },
                onError = {
                    _user.value = null
                }
            )
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers(
                onSuccess = { users ->
                   users.forEach {
                       Log.d("UserViewModel", "User ${it.displayName} banned: ${it.isBanned}, bannedUntil: ${it.bannedUntil}")
                   }
                    _allUsers.value = users
                },
                onFailure = {
                    _allUsers.value = emptyList()
                }
            )
        }
    }

    fun sendWarningToUser(userId: String, warningMessage: String) {
        userRepository.sendUserWarning(userId, warningMessage) { success ->
        if (success) {
            loadAllUsers()
        } else {
            // Manejar error
        }
        }
    }

    fun markWarningAsRead(uid: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid)
            .update(
                mapOf(
                    "hasReadWarning" to true,
                    "warningMessage" to ""
                )
            )
            .addOnSuccessListener {
                Log.d("UserViewModel", "Aviso marcado como leído")
                loadUserData(uid)
            }
            .addOnFailureListener { e ->
                Log.e("UserViewModel", "Error al marcar el aviso como leído", e)

            }
        }

    fun reportUser(userId: String, untilDate: Timestamp, message: String) {
        userRepository.reportUser(userId, untilDate, message) { success ->
            if (success) {
                loadAllUsers()
            } else {
                Log.e("UserViewModel", "Error al reportar al usuario")
            }
        }
    }
}

