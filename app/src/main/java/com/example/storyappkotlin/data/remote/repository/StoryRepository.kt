package com.example.storyappkotlin.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyappkotlin.data.StoryRemoteMediator
import com.example.storyappkotlin.data.local.database.DatabaseApp
import com.example.storyappkotlin.data.local.entity.Story
import com.example.storyappkotlin.data.remote.response.StoryResponse
import com.example.storyappkotlin.data.remote.retrofit.ApiService
import com.example.storyappkotlin.data.remote.Result
import com.example.storyappkotlin.data.remote.dto.StoryDto
import com.example.storyappkotlin.data.remote.paging.StoryPagingSource
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class StoryRepository private constructor(private val database: DatabaseApp, private val apiService: ApiService) {
    private val TAG = StoryRepository::class.java.simpleName

    private val storiesResult = MediatorLiveData<Result<StoryResponse>>()
    private val uploadResult = MediatorLiveData<Result<StoryResponse>>()
    private val storyDetailResult = MediatorLiveData<Result<StoryResponse>>()

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(database: DatabaseApp, apiService: ApiService): StoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryRepository(database, apiService).also { INSTANCE = it }
        }
    }

    fun getStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int
    ): LiveData<Result<StoryResponse>> {
        storiesResult.value = Result.Loading

        val client = apiService.getStories(token, page, size, location)
        client.enqueue(object: Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG, "Response body: $it")
                        storiesResult.value = Result.Success(it)
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.d(TAG, "Error body : $errorBody")
                        val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
                        storiesResult.value = Result.Error(errorResponse.message)
                    } catch (e: IOException) {
                        Log.e(TAG, "Error reading the response body", e)
                        storiesResult.value = Result.Error("Unexpected error occurred")
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                storiesResult.value = Result.Error(t.localizedMessage ?: "Unknown error")
            }
        })

        return storiesResult
    }

    fun getPagedStories(token: String, location: Int): LiveData<PagingData<StoryDto>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token, location)
            }
        ).liveData
    }

    fun getRemoteMediatorPagedStories(token: String, location: Int): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(database, apiService, token, location),
            pagingSourceFactory = {
                database.storyDao().getStories()
            }
        ).liveData
    }

    fun getDetailStory(token: String, id: String): LiveData<Result<StoryResponse>> {
        storyDetailResult.value = Result.Loading

        val client = apiService.getStoryDetail(token, id)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG, "Response body: $it")
                        storyDetailResult.value = Result.Success(it)
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.d(TAG, "Error body: $errorBody")
                        val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
                        storyDetailResult.value = Result.Error(errorResponse.message)
                    } catch (e: IOException) {
                        Log.e(TAG, "Error reading the response body", e)
                        storyDetailResult.value = Result.Error("Unexpected error occurred")
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                storyDetailResult.value = Result.Error(t.localizedMessage ?: "Unknown error")
            }
        })

        return storyDetailResult
    }

    fun uploadStory(
        token: String,
        description: String,
        photo: File?,
        lat: Float?,
        lon: Float?
    ): LiveData<Result<StoryResponse>> {
        uploadResult.value = Result.Loading

        val descriptionBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
        val latBody = lat?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }
        val lonBody = lon?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it.toString()) }

        val requestFile = photo?.let { RequestBody.create("image/*".toMediaTypeOrNull(), photo) }
        val photoPart = requestFile?.let { MultipartBody.Part.createFormData("photo", photo.name, requestFile) }

        val client = apiService.uploadStory(token, descriptionBody, photoPart, latBody, lonBody)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(TAG, "Response body: $it")
                        uploadResult.value = Result.Success(it)
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.d(TAG, "Error body: $errorBody")
                        val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
                        uploadResult.value = Result.Error(errorResponse.message)
                    } catch (e: IOException) {
                        Log.e(TAG, "Error reading the response body", e)
                        uploadResult.value = Result.Error("Unexpected error occurred")
                    }
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                uploadResult.value = Result.Error(t.localizedMessage ?: "Unknown error")
            }

        })

        return uploadResult
    }
}