package com.example.storyappkotlin.ui.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyappkotlin.data.remote.Result
import com.example.storyappkotlin.data.remote.repository.AuthRepository
import com.example.storyappkotlin.data.remote.response.LoginResponse
import com.example.storyappkotlin.data.remote.response.RegisterResponse

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val registerResult = MutableLiveData<Result<RegisterResponse>>()
    private val loginResult = MutableLiveData<Result<LoginResponse>>()

    fun getRegisterResult(): LiveData<Result<RegisterResponse>> = registerResult

    fun getLoginResult(): LiveData<Result<LoginResponse>> = loginResult

    fun register(lifecycleOwner: LifecycleOwner, name: String, email: String, password: String) {
        authRepository.register(name, email, password).observe(lifecycleOwner) {
            registerResult.postValue(it)
        }
    }

    fun login(lifecycleOwner: LifecycleOwner, email: String, password: String) {
        authRepository.login(email, password).observe(lifecycleOwner) {
            loginResult.postValue(it)
        }
    }
}