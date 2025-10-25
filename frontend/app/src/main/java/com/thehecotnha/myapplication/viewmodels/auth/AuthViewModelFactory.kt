package com.thehecotnha.myapplication.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thehecotnha.myapplication.repository.UserRepository

class AuthViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       return AuthViewModel(repository) as T
    }
}