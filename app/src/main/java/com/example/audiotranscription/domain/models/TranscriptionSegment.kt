package com.example.audiotranscription.domain.models

data class TranscriptionSegment(
    val text: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val isFinal: Boolean = false
)