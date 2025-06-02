package com.example.gametracker.data.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun registerUser(email: String, password: String, username: String): Result<FirebaseUser> =
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(username).build()
            authResult.user?.updateProfile(profileUpdates)?.await()

            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val userData = mapOf(
                    "uid" to firebaseUser.uid,
                    "displayName" to username,
                    "email" to email,
                    "role" to "user",
                    "createdAt" to com.google.firebase.Timestamp.now(),
                    "hasReadWarning" to false,
                    "warningMessage" to ""
                )

                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(firebaseUser.uid)
                    .set(userData)
                    .await()

                Result.success(firebaseUser)
            } else {
                Result.failure(Exception("No se pudo obtener el usuario después del registro."))
            }

        } catch (ex: Exception) {
            Result.failure(ex)
        }



    suspend fun loginsUser(email: String, password: String): Result<FirebaseUser> =
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (ex: Exception) {
            Result.failure(ex)
        }

    suspend fun loginWithGoogle(idToken: String): Result<Unit> =
        try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }


    fun signOut(context: Context) {
     auth.signOut()

        // Para google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}
