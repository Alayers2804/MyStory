package com.bangkit.submissionreal5.data.database.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.bangkit.submissionreal5.data.api.ApiService
import com.bangkit.submissionreal5.data.database.StoriesDatabase
import com.bangkit.submissionreal5.data.response.Story
import com.bangkit.submissionreal5.data.response.StoryRemoteKeys

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoriesDatabase,

    private val apiService: ApiService,

    private val token: String
    ): RemoteMediator<Int, Story>() {

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when (loadType){
            LoadType.REFRESH -> {
                INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKeys = remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKeys
            }
            LoadType.APPEND -> {
                val remotekeys = getRemoteKeyForLastItem(state)
                val nextKeys = remotekeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remotekeys != null)
                nextKeys
            }

        }
        return try {
            val responseData =
                apiService.getAllStory(token, page, state.config.pageSize).listStory
            val endOfPaginationReached = responseData.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyRemoteKeysData().deleteRemoteKeys()
                    database.storyData().deleteAll()
                }
                val prevKeys = if (page == 1) null else page - 1
                val nextKeys = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    StoryRemoteKeys(id = it.id, prevKey = prevKeys, nextKey = nextKeys )
                }
                database.storyRemoteKeysData().insertAll(keys)
                database.storyData().insertStory(responseData)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception : Exception){
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): StoryRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.storyRemoteKeysData().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): StoryRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.storyRemoteKeysData().getRemoteKeysId(data.id)
        }
    }


}

