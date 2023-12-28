package com.bangkit.submissionreal5.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.submissionreal5.data.database.repository.StoriesRepository
import com.bangkit.submissionreal5.data.response.Story
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class StoryPagerViewModel(private val storyRepository: StoriesRepository) : ViewModel() {
    val story: LiveData<PagingData<Story>> =
        storyRepository.getStory().cachedIn(viewModelScope)
}