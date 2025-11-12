package com.example.audiotranscription.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _selectedModel = MutableStateFlow("tiny")
    val selectedModel: StateFlow<String> = _selectedModel

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _selectedProcessingMode = MutableStateFlow("streaming")
    val selectedProcessingMode: StateFlow<String> = _selectedProcessingMode

    fun setModel(model: String) {
        _selectedModel.value = model
    }

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun setProcessingMode(mode: String) {
        _selectedProcessingMode.value = mode
    }
}