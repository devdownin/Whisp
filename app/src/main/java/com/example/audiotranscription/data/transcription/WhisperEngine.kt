package com.example.audiotranscription.data.transcription

import android.content.Context
import android.util.Log
import com.argmaxinc.whisperkit.WhisperKit
import com.argmaxinc.whisperkit.ExperimentalWhisperKit
import com.example.audiotranscription.domain.models.TranscriptionSegment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalWhisperKit::class)
class WhisperEngine(
    private val context: Context
) {
    private var whisperKit: WhisperKit? = null
    private val _transcriptionSegments = MutableStateFlow<List<TranscriptionSegment>>(emptyList())
    val transcriptionSegments: StateFlow<List<TranscriptionSegment>> = _transcriptionSegments

    suspend fun initialize() {
        whisperKit = WhisperKit.Builder()
            .setModel(WhisperKit.OPENAI_TINY_EN)
            .setApplicationContext(context.applicationContext)
            .build()
    }

    suspend fun startTranscription(audioData: ByteArray) {
        whisperKit?.let {
            val result = it.transcribe(audioData)
            val segments = result?.segments?.map { segment ->
                TranscriptionSegment(
                    text = segment.text,
                    startTimestamp = segment.start,
                    endTimestamp = segment.end
                )
            } ?: emptyList()
            _transcriptionSegments.value = segments
        }
    }

    fun stopTranscription() {
        // TODO: Stop audio recording and finalize transcription
    }

    fun release() {
        whisperKit?.close()
    }
}