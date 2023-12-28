package com.bangkit.submissionreal5.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.submissionreal5.data.api.ApiService
import com.bangkit.submissionreal5.data.database.StoriesDatabase
import com.bangkit.submissionreal5.data.database.repository.StoriesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ViewModelStoryFactory(val context: Context, private val apiService: ApiService, val token:String) :
    ViewModelProvider.NewInstanceFactory() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryPagerViewModel::class.java)) {
            val database = StoriesDatabase.getDatabase(context)
            return StoryPagerViewModel(
                StoriesRepository(
                    database,
                    apiService, token
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}