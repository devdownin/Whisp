package com.example.audiotranscription.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiotranscription.data.repository.SettingsRepository
import com.example.audiotranscription.data.repository.TranscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val transcriptionRepository: TranscriptionRepository
) : ViewModel() {

    val selectedModel: StateFlow<String> = settingsRepository.selectedModel
    val selectedLanguage: StateFlow<String> = settingsRepository.selectedLanguage
    val autoSaveTranscriptions: StateFlow<Boolean> = settingsRepository.autoSave
    val profanityFilter: StateFlow<Boolean> = settingsRepository.profanityFilter
    val punctuationAndFormatting: StateFlow<Boolean> = settingsRepository.punctuation

    fun setSelectedModel(model: String) {
        settingsRepository.setModel(model)
    }

    fun setSelectedLanguage(language: String) {
        settingsRepository.setLanguage(language)
    }

    fun setAutoSave(enabled: Boolean) {
        settingsRepository.setAutoSave(enabled)
    }

    fun setProfanityFilter(enabled: Boolean) {
        settingsRepository.setProfanityFilter(enabled)
    }

    fun setPunctuation(enabled: Boolean) {
        settingsRepository.setPunctuation(enabled)
    }

    fun exportAllTranscriptions() {
        // Placeholder for export logic
    }

    fun clearTranscriptionHistory() {
        viewModelScope.launch {
            transcriptionRepository.deleteAll()
        }
    }
}
