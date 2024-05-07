package com.notsatria.videoscroll.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "web_view_preferences")

class WebViewPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val URL_KEY = stringPreferencesKey("url")

    suspend fun saveUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[URL_KEY] = url
        }
    }

    fun getUrl(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[URL_KEY] ?: ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: WebViewPreferences? = null

        fun getInstance(context: Context): WebViewPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = WebViewPreferences(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}