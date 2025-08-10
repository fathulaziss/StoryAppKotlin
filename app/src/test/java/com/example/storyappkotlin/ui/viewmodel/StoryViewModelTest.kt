package com.example.storyappkotlin.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyappkotlin.DataDummy
import com.example.storyappkotlin.MainDispatcherRule
import com.example.storyappkotlin.data.local.entity.Story
import com.example.storyappkotlin.data.remote.repository.StoryRepository
import com.example.storyappkotlin.getOrAwaitValue
import com.example.storyappkotlin.utils.diffcallback.StoryDiffCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when getPagedStory and loadStories Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val pagingData: PagingData<Story> = StoryPagingSource.snapshot(dummyStory)

        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = pagingData

        Mockito.`when`(storyRepository.getRemoteMediatorPagedStories("", 0)).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository)
        storyViewModel.loadStories("", 0)
        val remoteMediatorResult = storyViewModel.getStoryResultRM.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(remoteMediatorResult)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getRemoteMediatorPagedStories("",0)).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository)
        storyViewModel.loadStories("", 0)
        val actualStory: PagingData<Story> = storyViewModel.getStoryResultRM.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<Story>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<Story>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<Story>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}