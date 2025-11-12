package com.example.audiotranscription.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiotranscription.data.repository.TranscriptionRepository
import com.example.audiotranscription.domain.models.Transcription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: TranscriptionRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

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