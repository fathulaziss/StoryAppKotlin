package com.example.storyappkotlin.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.storyappkotlin.data.local.database.DatabaseApp
import com.example.storyappkotlin.data.local.entity.Story
import com.example.storyappkotlin.data.remote.dto.StoryDto
import com.example.storyappkotlin.data.remote.response.LoginResponse
import com.example.storyappkotlin.data.remote.response.RegisterResponse
import com.example.storyappkotlin.data.remote.response.StoryResponse
import com.example.storyappkotlin.data.remote.retrofit.ApiService
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {
    private var mockApi: ApiService = FakeApiService()
    private var mockDb: DatabaseApp = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        DatabaseApp::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
            "",
            0,
        )
        val pagingState = PagingState<Int, Story>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override fun register(name: String, email: String, password: String): Call<RegisterResponse> {
        TODO("Not yet implemented")
    }

    override fun login(email: String, password: String): Call<LoginResponse> {
        TODO("Not yet implemented")
    }

    override fun getStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int,
    ): Call<StoryResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getPagedStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int,
    ): StoryResponse {
        val stories = mutableListOf<StoryDto>()
        for (i in 0..100) {
            val story = StoryDto(
                id = i.toString(),
                name = "Author $i",
                description = "Story description $i",
                photoUrl = "https://example.com/story_$i.jpg",
                createdAt = "2025-08-10T00:00:00Z",
                lat = null,
                lon = null
            )
            stories.add(story)
        }

        // Handle null page/size defaults
        val currentPage = page ?: 1
        val pageSize = size ?: 10

        val pagedStories = stories.subList(
            (currentPage - 1) * pageSize,
            minOf((currentPage - 1) * pageSize + pageSize, stories.size)
        )

        return StoryResponse(
            error = false,
            message = "Success",
            story = null,
            listStory = pagedStories
        )
    }

    override fun getStoryDetail(token: String, storyId: String): Call<StoryResponse> {
        TODO("Not yet implemented")
    }

    override fun uploadStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part?,
        lat: RequestBody?,
        lon: RequestBody?,
    ): Call<StoryResponse> {
        TODO("Not yet implemented")
    }
}