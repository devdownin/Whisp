package com.example.audiotranscription.viewmodels

import android.app.Application
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiotranscription.data.repository.AppDatabase
import com.example.audiotranscription.data.repository.TranscriptionRepository
import com.example.audiotranscription.domain.models.Transcription
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TranscriptionRepository
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        val transcriptionDao = AppDatabase.getDatabase(application).transcriptionDao()
        repository = TranscriptionRepository(transcriptionDao)
    }

    val transcriptions: Flow<List<Transcription>> = searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            repository.allTranscriptions
        } else {
            repository.searchTranscriptions("%$query%")
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun delete(transcription: Transcription) {
        viewModelScope.launch {
            repository.delete(transcription)
        }
    }
}