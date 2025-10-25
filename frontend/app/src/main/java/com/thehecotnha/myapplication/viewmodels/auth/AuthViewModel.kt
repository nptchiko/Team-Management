package com.thehecotnha.myapplication.viewmodels.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.repository.UserRepository
import com.thehecotnha.myapplication.utils.Response
import kotlinx.coroutines.launch

class AuthViewModel (private val repo: UserRepository): ViewModel() {

    private val _signUpState: MutableLiveData<Response<User>> = MutableLiveData(Response.Idle)
    val signUpState : LiveData<Response<User>> = _signUpState

    private val _signInState: MutableLiveData<Response<FirebaseUser>> = MutableLiveData(Response.Idle)
    val signInState : LiveData<Response<FirebaseUser>> = _signInState

    fun signUp(user: User) = viewModelScope.launch {
        _signUpState.value = Response.Loading
        val result = repo.signUp(user)
        _signUpState.value = result
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {
        _signInState.value = Response.Loading
        _signInState.value = repo.signIn(email, password)
    }

}