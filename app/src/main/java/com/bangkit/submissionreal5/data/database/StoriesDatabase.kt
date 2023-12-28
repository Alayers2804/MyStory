package com.bangkit.submissionreal5.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bangkit.submissionreal5.data.response.Story
import com.bangkit.submissionreal5.data.response.StoryRemoteKeys


@Database(
    entities =[Story::class, StoryRemoteKeys::class],
    version = 1,
    exportSchema = false
)

abstract class StoriesDatabase : RoomDatabase() {

    abstract fun storyData(): StoryDao
    abstract fun storyRemoteKeysData(): StoryRemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoriesDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoriesDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoriesDatabase::class.java, "story_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}