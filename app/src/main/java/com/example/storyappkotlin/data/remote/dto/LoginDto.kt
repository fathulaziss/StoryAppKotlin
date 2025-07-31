package com.example.storyappkotlin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("userId")
    val userId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("token")
    val token: String
)
