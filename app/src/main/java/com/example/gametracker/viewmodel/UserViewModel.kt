package com.example.gametracker.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gametracker.data.repository.UserRepository
import com.example.gametracker.model.GameEntry
import com.example.gametracker.model.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user
    private val _allUsers = MutableStateFlow<List<UserModel>>(emptyList())
    val allUsers: StateFlow<List<UserModel>> = _allUsers

    private val _userGameEntries = mutableStateOf<List<GameEntry>>(emptyList())
    val userGameEntries: State<List<GameEntry>> get() = _userGameEntries

    private val _searchResults = mutableStateOf<List<UserModel>>(emptyList())
    val searchResults: State<List<UserModel>> = _searchResults

    private val currentUserId = Firebase.auth.currentUser?.uid

    private val _selectedUserProfile = mutableStateOf<UserModel?>(null)
    val selectedUserProfile: State<UserModel?> = _selectedUserProfile

    val publicUser = mutableStateOf<UserModel?>(null)


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

    fun loadUserGameEntries(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("gameList")
                    .get()
                    .await()

                val entries = snapshot.toObjects(GameEntry::class.java)
                _userGameEntries.value = entries
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error loading user game entries: ${e.message}")
            }
        }
    }

    fun searchUsers(query: String) {
        Log.d("SearchUsers", "Query: $query")
        Log.d("SearchUsers", "Current UID: ${Firebase.auth.currentUser?.uid}")
        if (query.isBlank() || query.length < 2) {
            _searchResults.value = emptyList()
            return
        }

        val currentUid = Firebase.auth.currentUser?.uid

        FirebaseFirestore.getInstance()
            .collection("users")
            .whereGreaterThanOrEqualTo("displayName", query)
            .whereLessThanOrEqualTo("displayName", query + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val users = result.mapNotNull { it.toObject(UserModel::class.java) }
                Log.d("SearchUsers", "Resultados sin filtrar: ${users.size}")
                _searchResults.value = users
            }
            .addOnFailureListener {
                _searchResults.value = emptyList()
            }
    }


    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    fun loadUserProfileById(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                _selectedUserProfile.value = doc.toObject(UserModel::class.java)
            }
            .addOnFailureListener {
                _selectedUserProfile.value = null
            }
    }

    fun loadUserById(userId: String) {
        viewModelScope.launch {
            val userData = userRepository.getUserById(userId)
            publicUser.value = userData
        }
    }



}

