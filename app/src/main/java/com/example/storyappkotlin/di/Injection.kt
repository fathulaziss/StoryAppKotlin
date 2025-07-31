package com.example.storyappkotlin.di

import android.content.Context
import com.example.storyappkotlin.data.remote.repository.AuthRepository
import com.example.storyappkotlin.data.remote.repository.StoryRepository
import com.example.storyappkotlin.data.remote.retrofit.ApiConfig
import com.example.storyappkotlin.data.remote.retrofit.ApiService

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }
}