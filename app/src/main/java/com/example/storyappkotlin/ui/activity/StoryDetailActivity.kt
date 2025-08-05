package com.example.storyappkotlin.ui.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.storyappkotlin.R
import com.example.storyappkotlin.data.remote.Result
import com.example.storyappkotlin.databinding.ActivityStoryDetailBinding
import com.example.storyappkotlin.ui.viewmodel.StoryViewModel
import com.example.storyappkotlin.ui.viewmodel.ViewModelFactory
import com.example.storyappkotlin.utils.SharedPreferenceUtil

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)

        val transitionName = intent.getStringExtra("transitionName")
        ViewCompat.setTransitionName(binding.ivPhoto, transitionName)

        setContentView(binding.root)

        val toolbar = binding.appBar.toolbarTitleAppBar
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.story_detail)

        val pref = SharedPreferenceUtil(this)

        val id = intent.getStringExtra("id")
        val token = "Bearer ${pref.getToken()}"

        val factory = ViewModelFactory.getInstance(this)
        val storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

        if (id != null) {
            storyViewModel.getDetailStory(this, token, id)
        }
        storyViewModel.getDetailStoryResult().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.pbLoading.visibility = View.GONE
                        val detailStory = result.data.story

                        if (detailStory != null) {
                            // Capitalize each word in name
                            val nameCapitalized = detailStory.name
                                .split(" ")
                                .joinToString(" ") { s: String ->
                                    s.replaceFirstChar { s.uppercase() }
                                }

                            binding.tvName.text = nameCapitalized
                            binding.tvDesc.text = detailStory.description

                            supportPostponeEnterTransition()
                            Glide.with(this)
                                .load(detailStory.photoUrl)
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean,
                                    ): Boolean {
                                        supportStartPostponedEnterTransition()
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: com.bumptech.glide.load.DataSource?,
                                        isFirstResource: Boolean,
                                    ): Boolean {
                                        supportStartPostponedEnterTransition()
                                        return false
                                    }
                                })
                                .into(binding.ivPhoto)
                        }
                    }

                    is Result.Error -> {
                        binding.tvDesc.text = getString(R.string.data_not_found)
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(
                            this,
                            getString(R.string.failed) + ": ${result.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
//            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}