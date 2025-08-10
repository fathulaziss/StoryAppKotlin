package com.example.storyappkotlin

import com.example.storyappkotlin.data.local.entity.Story

object DataDummy {

    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val quote = Story(
                i.toString(),
                "name + $i",
                "description $i",
                photoUrl = "photoUrl $i",
                createdAt = "createdAt $i",
                lat = 0.0,
                lon = 0.0
            )
            items.add(quote)
        }
        return items
    }
}