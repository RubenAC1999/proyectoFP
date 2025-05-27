package com.example.gametracker.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gametracker.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val authRepository: AuthRepository,
    private val userViewModel: UserViewModel
): AndroidViewModel(application) {

    // Estados UI
    val isLoading = MutableStateFlow(false)
    val errorMessage =  MutableStateFlow<String?>(null)


    // Eventos de navegación
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    sealed class NavigationEvent {
        object NavigateToHome: NavigationEvent()
        data class ShowError(val message: String): NavigationEvent()
    }

    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = authRepository.registerUser(email, password, username)

            result.onSuccess { firebaseUser ->
                // Usamos un launch dentro de viewModelScope para asegurarnos de que emitimos en una corutina
                userViewModel.createUserInFirestore(firebaseUser, {
                    // Emitir dentro de la corutina correctamente
                    launch {
                        _navigationEvent.emit(NavigationEvent.NavigateToHome)
                    }
                }, { error ->
                    errorMessage.value = "Error al crear el usuario en Firestore: ${error.localizedMessage}"
                })
            }.onFailure { error ->
                errorMessage.value = when {
                    error.message?.contains("email") == true -> "Email inválido o ya registrado."
                    error.message?.contains("password") == true -> "Contraseña demasiado débil."
                    else -> "Error en el registro: ${error.localizedMessage}"
                }
            }

            isLoading.value = false
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            authRepository.loginsUser(email, password)
                .onSuccess {
                    // Emitir el evento de navegación en un launch dentro de viewModelScope
                    viewModelScope.launch {
                        _navigationEvent.emit(NavigationEvent.NavigateToHome)
                    }
                }
                .onFailure { error ->
                    errorMessage.value = when {
                        error.message?.contains("invalid") == true -> "Credenciales incorrectas."
                        error.message?.contains("user") == true -> "Usuario no encontrado."
                        else -> "Error al iniciar sesión ${error.localizedMessage}"
                    }
                }

            isLoading.value = false
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            authRepository.loginWithGoogle(idToken)
                .onSuccess {
                    // Emitir el evento de navegación en un launch dentro de viewModelScope
                    viewModelScope.launch {
                        _navigationEvent.emit(NavigationEvent.NavigateToHome)
                    }
                }
                .onFailure { error ->
                    errorMessage.value = "Error al iniciar sesión con Google: ${error.localizedMessage}"
                }

            isLoading.value = false
        }
    }


    // FUNCIONES QUE AÑADIMOS PARA GOOGLE SIGN-IN
    fun launchGoogleSignIn(launcher: ActivityResultLauncher<Intent>) {
        val context = getApplication<Application>().applicationContext
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1016892065400-5pls1csi82jtq9cbus6dv4fd51vh0g1m.apps.googleusercontent.com")  // Pega aquí el clientId web de Firebase
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        launcher.launch(googleSignInClient.signInIntent)
    }

    fun handleGoogleSignInResult(result: ActivityResult) {
        val context = getApplication<Application>().applicationContext
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                loginWithGoogle(idToken)
            } else {
                Toast.makeText(context, "No se pudo obtener el ID Token", Toast.LENGTH_LONG).show()
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Error de autenticación: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun logout(context: Context) {
        AuthRepository.signOut(context)
    }
}