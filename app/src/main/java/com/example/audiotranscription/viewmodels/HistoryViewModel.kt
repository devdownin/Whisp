package com.example.audiotranscription.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.audiotranscription.data.repository.AppDatabase
import com.example.audiotranscription.data.repository.TranscriptionRepository
import com.example.audiotranscription.domain.models.Transcription
import kotlinx.coroutines.flow.Flow

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TranscriptionRepository

    init {
        val transcriptionDao = AppDatabase.getDatabase(application).transcriptionDao()
        repository = TranscriptionRepository(transcriptionDao)
    }

    val allTranscriptions: Flow<List<Transcription>> = repository.allTranscriptions
}