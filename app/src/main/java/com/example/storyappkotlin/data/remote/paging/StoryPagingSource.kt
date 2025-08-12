package com.example.storyappkotlin.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyappkotlin.data.remote.dto.StoryDto
import com.example.storyappkotlin.data.remote.retrofit.ApiService

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String,
    private val location: Int
) : PagingSource<Int, StoryDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryDto> {
        return try {
            val page = params.key ?: 1
            val size = params.loadSize

            val response = apiService.getPagedStories(token, page, size, location)
            val stories = response.listStory

            LoadResult.Page(
                data = stories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryDto>): Int? {
        return state.anchorPosition?.let { anchor ->
            val anchorPage = state.closestPageToPosition(anchor)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}