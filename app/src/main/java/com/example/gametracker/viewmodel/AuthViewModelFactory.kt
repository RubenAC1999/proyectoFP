package com.example.gametracker.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gametracker.data.repository.AuthRepository

class AuthViewModelFactory(
    private val application: Application,
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(application, AuthRepository, userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
