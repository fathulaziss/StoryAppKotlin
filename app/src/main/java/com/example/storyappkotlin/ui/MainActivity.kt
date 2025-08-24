package com.example.storyappkotlin.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.storyappkotlin.R
import com.example.storyappkotlin.data.local.entity.Story
import com.example.storyappkotlin.databinding.ActivityMainBinding
import com.example.storyappkotlin.ui.activity.LoginActivity
import com.example.storyappkotlin.ui.activity.MapActivity
import com.example.storyappkotlin.ui.activity.StoryDetailActivity
import com.example.storyappkotlin.ui.activity.StoryFormActivity
import com.example.storyappkotlin.ui.adapter.LoadingStateAdapter
import com.example.storyappkotlin.ui.adapter.list.StoriesAdapter
import com.example.storyappkotlin.ui.adapter.list.StoriesPagingAdapter
import com.example.storyappkotlin.ui.viewmodel.StoryViewModel
import com.example.storyappkotlin.ui.viewmodel.ViewModelFactory
import com.example.storyappkotlin.utils.IntentKeys
import com.example.storyappkotlin.utils.SharedPreferenceUtil

class MainActivity : AppCompatActivity(), StoriesPagingAdapter.OnItemClickListener {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var storiesPagingAdapter: StoriesPagingAdapter
    private lateinit var pref: SharedPreferenceUtil
    private lateinit var addStoryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate MainActivity")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.appBar.toolbarTitleAppBar
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.app_name)

        pref = SharedPreferenceUtil(this)

        val factory = ViewModelFactory.getInstance(this)
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

//        binding.rvStories.apply {
//            layoutManager = GridLayoutManager(this@MainActivity, 2)
//            setHasFixedSize(true)
//        }

//        storiesAdapter = StoriesAdapter(this, this)
//        binding.rvStories.adapter = storiesAdapter

        val token = "Bearer ${pref.getToken()}"
        val location = 0

        storiesPagingAdapter = StoriesPagingAdapter(this, this)
        binding.rvStories.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = storiesPagingAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storiesPagingAdapter.retry() }
            )
        }

        storyViewModel.loadStories(token, location)
        storyViewModel.getStoryResultRM.observe(this) { pagingData ->
            storiesPagingAdapter.submitData(lifecycle, pagingData)
        }

//        storyViewModel.getStories(this, token, page, size, location)
//        storyViewModel.getStoryResult().observe(this) { result ->
//            if (result != null) {
//                when (result) {
//                    is Result.Loading -> {
//                        binding.pbLoading.visibility = View.VISIBLE
//                    }
//                    is Result.Success -> {
//                        binding.pbLoading.visibility = View.GONE
//                        val stories = result.data.listStory.orEmpty()
//                        Log.d(TAG,"stories size = " + stories.size)
//                        storiesAdapter.submitList(stories)
//                    }
//                    is Result.Error -> {
//                        binding.pbLoading.visibility = View.GONE
//                        Toast.makeText(
//                            this,
//                            getString(R.string.failed) + ": ${result.error}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }

        addStoryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                storiesPagingAdapter.refresh()
            }
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, StoryFormActivity::class.java)
            addStoryLauncher.launch(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_out -> {
                pref.removeToken()
                Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
                return true
            }
            R.id.action_map -> {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onItemClicked(story: Story, sharedImageView: ImageView) {
        val intent = Intent(this, StoryDetailActivity::class.java).apply {
            putExtra(IntentKeys.STORY_ID, story.id)
            putExtra(IntentKeys.TRANSITION_NAME, ViewCompat.getTransitionName(sharedImageView))
        }
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            sharedImageView,
            ViewCompat.getTransitionName(sharedImageView) ?: ""
        )
        startActivity(intent, options.toBundle())
    }
}