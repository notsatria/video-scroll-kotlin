package com.notsatria.videoscroll.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.notsatria.videoscroll.datastore.WebViewPreferences
import kotlinx.coroutines.launch

class WebViewViewModel(private val pref: WebViewPreferences) : ViewModel() {
    fun getUrl(): LiveData<String> {
        return pref.getUrl().asLiveData()
    }

    fun saveUrl(url: String) {
        viewModelScope.launch {
            pref.saveUrl(url)
        }
    }
}