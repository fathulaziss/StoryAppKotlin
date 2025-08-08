package com.example.storyappkotlin.data.remote.dto

import com.example.storyappkotlin.data.local.entity.Story
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

fun StoryDto.toEntity(): Story {
    return Story(
        id = id,
        name = name,
        description = description,
        photoUrl = photoUrl,
        createdAt = createdAt,
        lat = lat,
        lon = lon
    )
}

fun List<StoryDto>.toEntityList(): List<Story> = this.map { it.toEntity() }