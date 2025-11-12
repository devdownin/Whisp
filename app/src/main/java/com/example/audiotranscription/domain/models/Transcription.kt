package com.example.audiotranscription.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transcriptions")
data class Transcription(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val segments: List<TranscriptionSegment>,
    val timestamp: Long = System.currentTimeMillis()
)