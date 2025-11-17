package com.example.audiotranscription.data.transcription

import android.content.Context
import com.argmaxinc.whisperkit.ExperimentalWhisperKit
import com.argmaxinc.whisperkit.WhisperKit
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

    private var isTranscribing: Boolean = false

    suspend fun initialize() {
        whisperKit = WhisperKit.Builder()
            .setModel("whisperkit-litert/openai_whisper-base")
            .setApplicationContext(context.applicationContext)
            // WhisperKit 0.3.x expects a (progress, result) callback.
            // Incremental updates are not used here because
            // transcription is handled synchronously in startTranscription.
            .setCallback { _, _ -> }
            .build()
    }

    suspend fun startTranscription(audioData: ByteArray) {
        // Avoid overlapping transcriptions on the same engine instance.
        if (isTranscribing) return

        whisperKit?.let { kit ->
            isTranscribing = true
            val transcriptionResult = kit.transcribe(audioData)
            val text = transcriptionResult?.toString().orEmpty()
            val segments =
                if (text.isNotBlank()) listOf(TranscriptionSegment(text, 0L, 0L)) else emptyList()

            _transcriptionSegments.value = segments
            isTranscribing = false
        }
    }

    fun stopTranscription() {
        // Currently, transcription is done on-demand from the latest audio buffer.
        // This method is kept for future streaming/cancellation support.
    }

    fun clearTranscription() {
        _transcriptionSegments.value = emptyList()
    }

    fun release() {
        whisperKit = null
    }
}
