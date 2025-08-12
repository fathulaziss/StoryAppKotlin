package com.example.storyappkotlin.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyappkotlin.data.local.dao.RemoteKeysDao
import com.example.storyappkotlin.data.local.dao.StoryDao
import com.example.storyappkotlin.data.local.entity.RemoteKeys
import com.example.storyappkotlin.data.local.entity.Story
import com.example.storyappkotlin.utils.AppDatabaseConstants

@Database(
    entities = [Story::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class DatabaseApp : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseApp? = null

        @JvmStatic
        fun getDatabase(context: Context): DatabaseApp {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseApp::class.java, AppDatabaseConstants.APP_DB
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
