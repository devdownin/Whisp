package com.example.audiotranscription.data.repository

import com.example.audiotranscription.domain.models.Transcription
import kotlinx.coroutines.flow.Flow

class TranscriptionRepository(private val transcriptionDao: TranscriptionDao) {
    val allTranscriptions: Flow<List<Transcription>> = transcriptionDao.getAllTranscriptions()

    suspend fun insert(transcription: Transcription) {
        transcriptionDao.insert(transcription)
    }
}