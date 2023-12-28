package com.bangkit.submissionreal5.utility

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Settings")

class Preferences private constructor(private val dataStore: DataStore<Preferences>){

    private val token = stringPreferencesKey(ObjectConstanta.UserPreferences.User_Token.name)
    private val id = stringPreferencesKey(ObjectConstanta.UserPreferences.UserId.name)
    private val name = stringPreferencesKey(ObjectConstanta.UserPreferences.Username.name)
    private val email = stringPreferencesKey(ObjectConstanta.UserPreferences.User_Email.name)
    private val historyLog = stringPreferencesKey(ObjectConstanta.UserPreferences.User_Loggedin.name)

    fun getUserToken(): Flow<String> = dataStore.data.map { it[token] ?: ObjectConstanta.preferenceDefaultValue }

    fun getUserId(): Flow<String> = dataStore.data.map { it[id] ?: ObjectConstanta.preferenceDefaultValue }

    fun getUserName(): Flow<String> = dataStore.data.map { it[name] ?: ObjectConstanta.preferenceDefaultValue }

    fun getUserEmail(): Flow<String> = dataStore.data.map { it[email] ?: ObjectConstanta.preferenceDefaultValue }

    fun getUserLastLogin(): Flow<String> = dataStore.data.map { it[historyLog] ?: ObjectConstanta.preferenceDefaultValue}

    suspend fun saveLoginSession(user_token: String, username: String, userId : String, user_email: String){
        dataStore.edit { preferences ->
            preferences[token] = user_token
            preferences[id] = userId
            preferences[name] = username
            preferences[email] = user_email
            preferences[historyLog] = FileUtility.getCurrentDateString()
        }
    }

    suspend fun clearLoginSession(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE:com.bangkit.submissionreal5.utility.Preferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): com.bangkit.submissionreal5.utility.Preferences {
            return INSTANCE ?: synchronized(this){
                val instance = Preferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}