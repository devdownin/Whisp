package com.example.audiotranscription.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    private val _autoSave = MutableStateFlow(prefs.getBoolean("auto_save", true))
    val autoSave: StateFlow<Boolean> = _autoSave

    private val _profanityFilter = MutableStateFlow(prefs.getBoolean("profanity_filter", false))
    val profanityFilter: StateFlow<Boolean> = _profanityFilter

    private val _punctuation = MutableStateFlow(prefs.getBoolean("punctuation", true))
    val punctuation: StateFlow<Boolean> = _punctuation

    private val _selectedLanguage = MutableStateFlow(prefs.getString("language", "en") ?: "en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _selectedModel = MutableStateFlow(prefs.getString("model", "Enhanced") ?: "Enhanced")
    val selectedModel: StateFlow<String> = _selectedModel

    fun setAutoSave(enabled: Boolean) {
        prefs.edit().putBoolean("auto_save", enabled).apply()
        _autoSave.value = enabled
    }

    fun setProfanityFilter(enabled: Boolean) {
        prefs.edit().putBoolean("profanity_filter", enabled).apply()
        _profanityFilter.value = enabled
    }

    fun setPunctuation(enabled: Boolean) {
        prefs.edit().putBoolean("punctuation", enabled).apply()
        _punctuation.value = enabled
    }

    fun setLanguage(language: String) {
        prefs.edit().putString("language", language).apply()
        _selectedLanguage.value = language
    }

    fun setModel(model: String) {
        prefs.edit().putString("model", model).apply()
        _selectedModel.value = model
    }
}
