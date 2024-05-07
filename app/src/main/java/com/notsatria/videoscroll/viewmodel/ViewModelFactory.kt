package com.notsatria.videoscroll.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notsatria.videoscroll.datastore.WebViewPreferences

class ViewModelFactory(
    private val pref: WebViewPreferences
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            WebViewViewModel::class.java -> WebViewViewModel(pref) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}