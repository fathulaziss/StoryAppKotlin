package com.example.storyappkotlin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StoryDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("photoUrl")
    val photoUrl: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("lon")
    val lon: Double?
)
