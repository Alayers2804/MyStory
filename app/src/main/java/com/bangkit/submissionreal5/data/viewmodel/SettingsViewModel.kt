package com.bangkit.submissionreal5.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bangkit.submissionreal5.utility.Preferences
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: Preferences): ViewModel() {

    fun getUserPreference(property: ObjectConstanta.UserPreferences): LiveData<String> {
        return when(property){
            ObjectConstanta.UserPreferences.UserId -> pref.getUserId().asLiveData()
            ObjectConstanta.UserPreferences.User_Token -> pref.getUserToken().asLiveData()
            ObjectConstanta.UserPreferences.Username -> pref.getUserName().asLiveData()
            ObjectConstanta.UserPreferences.User_Email -> pref.getUserEmail().asLiveData()
            ObjectConstanta.UserPreferences.User_Loggedin -> pref.getUserLastLogin().asLiveData()
        }
    }
    fun setUserPreferences(userToken: String, userUid: String, userName:String, userEmail: String) {
        viewModelScope.launch {
            pref.saveLoginSession(userToken,userUid,userName,userEmail)
        }
    }
    fun clearUserPreferences() {
        viewModelScope.launch {
            pref.clearLoginSession()
        }
    }
}