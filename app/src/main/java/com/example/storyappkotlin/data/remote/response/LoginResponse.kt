package com.example.storyappkotlin.data.remote.response

import com.example.storyappkotlin.data.remote.dto.LoginDto
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("loginResult")
    val loginResult: LoginDto
)
