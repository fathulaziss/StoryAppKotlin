package com.example.storyappkotlin.utils.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.example.storyappkotlin.data.remote.dto.StoryDto

object StoryDiffCallback : DiffUtil.ItemCallback<StoryDto>() {
    override fun areItemsTheSame(oldItem: StoryDto, newItem: StoryDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StoryDto, newItem: StoryDto): Boolean {
        return oldItem == newItem
    }
}