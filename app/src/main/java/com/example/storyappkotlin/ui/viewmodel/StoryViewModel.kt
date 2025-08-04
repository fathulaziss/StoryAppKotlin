package com.example.storyappkotlin.ui.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyappkotlin.data.remote.Result
import com.example.storyappkotlin.data.remote.repository.StoryRepository
import com.example.storyappkotlin.data.remote.response.StoryResponse
import java.io.File

class StoryViewModel(private val storyRepository: StoryRepository): ViewModel() {

    private val getStoryResult = MutableLiveData<Result<StoryResponse>>()
    private val uploadStoryResult = MutableLiveData<Result<StoryResponse>>()
    private val getDetailStoryResult = MutableLiveData<Result<StoryResponse>>()

    fun getStoryResult(): LiveData<Result<StoryResponse>> = getStoryResult

    fun getDetailStoryResult(): LiveData<Result<StoryResponse>> = getDetailStoryResult

    fun getUploadStoryResult(): LiveData<Result<StoryResponse>> = uploadStoryResult

    fun getStories(
        lifecycleOwner: LifecycleOwner,
        token: String,
        page: Int?,
        size: Int?,
        location: Int
    ) {
        storyRepository.getStories(token, page, size, location).observe(lifecycleOwner) {
            getStoryResult.postValue(it)
        }
    }

    fun getDetailStory(lifecycleOwner: LifecycleOwner, token: String, id: String) {
        storyRepository.getDetailStory(token, id).observe(lifecycleOwner) {
            getDetailStoryResult.postValue(it)
        }
    }

    fun uploadStory(
        lifecycleOwner: LifecycleOwner,
        token: String,
        description: String,
        photo: File?,
        lat: Float?,
        lon: Float?
    ) {
        storyRepository.uploadStory(token, description, photo, lat, lon).observe(lifecycleOwner) {
            uploadStoryResult.postValue(it)
        }
    }
}