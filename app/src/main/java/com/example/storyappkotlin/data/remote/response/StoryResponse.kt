package com.example.storyappkotlin.data.remote.response

import com.example.storyappkotlin.data.remote.dto.StoryDto
import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("story")
    val story: StoryDto?,

    @SerializedName("listStory")
    val listStory: List<StoryDto>?
)
