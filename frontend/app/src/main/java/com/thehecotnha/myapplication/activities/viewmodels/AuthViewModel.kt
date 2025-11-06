package com.thehecotnha.myapplication.activities.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.thehecotnha.myapplication.models.User
import com.thehecotnha.myapplication.repository.UserRepository
import com.thehecotnha.myapplication.utils.Response
import kotlinx.coroutines.launch

class AuthViewModel (): ViewModel() {


    private val repo = UserRepository()

    private val _signUpState: MutableLiveData<Response<User>> = MutableLiveData(Response.Idle)
    val signUpState : LiveData<Response<User>> = _signUpState

    private val _signInState: MutableLiveData<Response<FirebaseUser>> =
        MutableLiveData(Response.Idle)
    val signInState : LiveData<Response<FirebaseUser>> = _signInState

    private val _userState: MutableLiveData<Response<User>> = MutableLiveData()
    val userState : LiveData<Response<User>> = _userState

    fun signUp(user: User) = viewModelScope.launch {
        if (_signUpState.value is Response.Idle)
            _signUpState.value = Response.Loading
        val result = repo.signUp(user)
        _signUpState.value = result
    }

    fun signIn(email: String, password: String) = viewModelScope.launch {

        _signInState.value = Response.Loading
        _signInState.value = repo.signIn(email, password)
    }

    fun getUserData() = viewModelScope.launch {
        _userState.value = Response.Loading
        val result = repo.getUserData()
        _userState.value = result
    }

}