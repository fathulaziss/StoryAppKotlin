package com.example.storyappkotlin.utils.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.example.storyappkotlin.data.local.entity.Story
import com.example.storyappkotlin.data.remote.dto.StoryDto

object StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
}