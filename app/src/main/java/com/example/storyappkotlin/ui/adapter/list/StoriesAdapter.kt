package com.example.storyappkotlin.ui.adapter.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyappkotlin.R
import com.example.storyappkotlin.data.remote.dto.StoryDto
import com.example.storyappkotlin.utils.diffcallback.StoryDiffCallback

class StoriesAdapter(
    private val context: Context,
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<StoryDto, StoriesAdapter.StoriesViewHolder>(StoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : StoriesAdapter.StoriesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.story_item, parent, false)
        return StoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        val story = getItem(position) ?: return
        val names = story.name.split(" ")
        val nameCapitalized = StringBuilder()

        for (name in names) {
            if (name.isNotEmpty()) {
                nameCapitalized.append(name.first().uppercaseChar())
                    .append(name.substring(1).lowercase())
                    .append(" ")
            }
        }

        Glide.with(context).load(story.photoUrl).into(holder.ivPhoto)
        holder.tvName.text = nameCapitalized.toString()
        holder.tvDesc.text = story.description
        holder.cvStory.setOnClickListener { }

        ViewCompat.setTransitionName(holder.ivPhoto, "storyImage_${story.id}")

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClicked(story, holder.ivPhoto)
        }
    }

    class StoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_desc)
        val cvStory: CardView = itemView.findViewById(R.id.cv_story)
        val ivPhoto: ImageView = itemView.findViewById(R.id.iv_photo)
    }

    interface OnItemClickListener {
        fun onItemClicked(story: StoryDto, sharedImageView: ImageView)
    }
}