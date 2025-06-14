package com.example.gametracker.data.repository

import com.example.gametracker.model.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val usersCollection = db.collection("users")


    suspend fun createUserInFirestore(user: UserModel, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
       val userRef = db.collection("users").document(userId)


        val userData = mapOf(
            "displayName" to user.displayName,
            "email" to user.email,
            "role" to user.role,
            "bio" to user.bio,
            "profilePicUrl" to user.profilePicUrl,
            "createdAt" to user.createdAt,
            "followersCount" to user.followersCount,
            "followingCount" to user.followingCount,
            "isBanned" to user.isBanned,
            "bannedUntil" to user.bannedUntil,
            "blockMessage" to user.blockMessage,
            "warningMessage" to user.warningMessage
        )

        try {
            userRef.set(userData).await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun getUserData(userId: String, onResult: (UserModel?) -> Unit, onError: (Exception) -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserModel::class.java)?.copy(uid = document.id)
                onResult(user)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    fun getAllUsers(
        onSuccess: (List<UserModel>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        FirebaseFirestore.getInstance().collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.mapNotNull { doc ->
                    val user = doc.toObject(UserModel::class.java)?.copy(uid = doc.id)
                    if (user != null) {
                        val isBannedFromData = doc.getBoolean("isBanned") ?: false
                        user.copy(isBanned = isBannedFromData)
                    } else null
                }
                onSuccess(users)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun sendUserWarning(userId: String, warningMessage: String, onComplete: (Boolean) -> Unit) {
        val userRef = db.collection("users").document(userId)
        val updateMap = mapOf("warningMessage" to warningMessage)
        userRef.update(updateMap)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun reportUser(userId: String, untilDate: Timestamp, blockMessage: String, onComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val updates = mapOf(
            "isBanned" to true,
            "bannedUntil" to untilDate,
            "blockMessage" to blockMessage
        )

        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    suspend fun getUserById(userId: String): UserModel? {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(UserModel::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

}