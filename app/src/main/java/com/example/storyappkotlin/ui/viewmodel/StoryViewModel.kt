package com.example.storyappkotlin.ui.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyappkotlin.data.local.entity.Story
import com.example.storyappkotlin.data.remote.Result
import com.example.storyappkotlin.data.remote.dto.StoryDto
import com.example.storyappkotlin.data.remote.repository.StoryRepository
import com.example.storyappkotlin.data.remote.response.StoryResponse
import java.io.File

class StoryViewModel(private val storyRepository: StoryRepository): ViewModel() {

    private val getStoryResult = MutableLiveData<Result<StoryResponse>>()
    private val uploadStoryResult = MutableLiveData<Result<StoryResponse>>()
    private val getDetailStoryResult = MutableLiveData<Result<StoryResponse>>()

    private var _pagedStories : LiveData<PagingData<StoryDto>> = MutableLiveData()
    val getPagedStoryResult: LiveData<PagingData<StoryDto>> get() = _pagedStories

    fun getPagedStory(token: String, location: Int) {
        _pagedStories = storyRepository.getPagedStories(token, location)
    }

    private var _pagedStoriesRM: LiveData<PagingData<Story>> = MutableLiveData()
    val getStoryResultRM: LiveData<PagingData<Story>> get() = _pagedStoriesRM

    fun loadStories(token: String, location: Int) {
        _pagedStoriesRM = storyRepository
            .getRemoteMediatorPagedStories(token, location)
            .cachedIn(viewModelScope)
    }

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