package com.example.audiotranscription.data.transcription

import android.content.Context
import com.argmaxinc.whisperkit.ExperimentalWhisperKit
import com.argmaxinc.whisperkit.WhisperKit
import com.example.audiotranscription.domain.models.TranscriptionSegment
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalWhisperKit::class)
class WhisperEngine(
    private val context: Context
) {
    private var whisperKit: WhisperKit? = null
    private val _transcriptionFlow = MutableSharedFlow<String>()
    val transcriptionFlow: SharedFlow<String> = _transcriptionFlow

    private var isTranscribing: Boolean = false

    suspend fun initialize() {
        whisperKit = WhisperKit.Builder()
            .setModel("whisperkit-litert/openai_whisper-base")
            .setApplicationContext(context.applicationContext)
            .setCallback { _, result ->
                val text = result?.toString().orEmpty()
                if (text.isNotBlank()) {
                    _transcriptionFlow.tryEmit(text)
                }
            }
            .build()
    }

    suspend fun startTranscription(audioData: ByteArray) {
        // Avoid overlapping transcriptions on the same engine instance.
        if (isTranscribing) return

        whisperKit?.let { kit ->
            isTranscribing = true
            // Transcription results are now handled by the callback
            kit.transcribe(audioData)
            isTranscribing = false
        }
    }

    fun stopTranscription() {
        // Currently, transcription is done on-demand from the latest audio buffer.
        // This method is kept for future streaming/cancellation support.
    }

    fun release() {
        whisperKit = null
    }
}
