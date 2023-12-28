package com.bangkit.submissionreal5.data.database.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.submissionreal5.data.api.ApiService
import com.bangkit.submissionreal5.data.database.StoriesDatabase
import com.bangkit.submissionreal5.data.response.Story

class StoriesRepository(
    private val storiesDatabase: StoriesDatabase,
    private val apiService: ApiService,
    private val token: String
    ) {
    fun getStory(): LiveData<PagingData<Story>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storiesDatabase, apiService, token),
            pagingSourceFactory = {storiesDatabase.storyData().getAllStory()}
        ).liveData
    }
}