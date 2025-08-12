package com.example.storyappkotlin.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.storyappkotlin.data.remote.response.LoginResponse
import com.example.storyappkotlin.data.remote.response.RegisterResponse
import com.example.storyappkotlin.data.remote.retrofit.ApiService
import com.example.storyappkotlin.data.remote.Result
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AuthRepository private constructor(private val apiService: ApiService) {
    private val TAG = AuthRepository::class.java.simpleName

    private val registerResult = MediatorLiveData<Result<RegisterResponse>>()
    private val loginResult = MediatorLiveData<Result<LoginResponse>>()

    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> {
        registerResult.value = Result.Loading

        val client = apiService.register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG, "Response body: $it")
                        registerResult.value = Result.Success(it)
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.d(TAG, "Error body: $errorBody")
                        val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                        registerResult.value = Result.Error(errorResponse.message)
                    } catch (e: IOException) {
                        Log.e(TAG, "Error parsing response", e)
                        registerResult.value = Result.Error("Unexpected error occurred")
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = Result.Error(t.localizedMessage ?: "Unknown error")
            }
        })

        return registerResult
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> {
        loginResult.value = Result.Loading

        val client = apiService.login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG,"Response body: $it")
                        loginResult.value = Result.Success(it)
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.d(TAG, "error body: $errorBody")
                        val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                        loginResult.value = Result.Error(errorResponse.message)
                    } catch (e: IOException) {
                        Log.e(TAG, "Error parsing response", e)
                        loginResult.value = Result.Error("Unexpected error occurred")
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(t.localizedMessage ?: "Unknown error")
            }

        })

        return loginResult
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(apiService: ApiService): AuthRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(apiService).also { INSTANCE = it }
            }
    }
}