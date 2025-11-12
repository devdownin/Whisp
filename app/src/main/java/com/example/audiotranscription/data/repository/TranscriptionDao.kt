package com.example.audiotranscription.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.audiotranscription.domain.models.Transcription
import kotlinx.coroutines.flow.Flow

@Dao
interface TranscriptionDao {
    @Insert
    suspend fun insert(transcription: Transcription)

    @Query("SELECT * FROM transcriptions ORDER BY timestamp DESC")
    fun getAllTranscriptions(): Flow<List<Transcription>>
}