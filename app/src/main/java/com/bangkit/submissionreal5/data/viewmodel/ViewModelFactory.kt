package com.bangkit.submissionreal5.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.submissionreal5.data.api.ApiService
import com.bangkit.submissionreal5.data.database.StoriesDatabase
import com.bangkit.submissionreal5.data.database.repository.StoriesRepository
import java.util.prefs.Preferences

class ViewModelFactory(
    private val context: Context,
    private val pref: com.bangkit.submissionreal5.utility.Preferences,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthenticationViewmodel::class.java) -> {
                AuthenticationViewmodel(context) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(pref) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(pref) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
