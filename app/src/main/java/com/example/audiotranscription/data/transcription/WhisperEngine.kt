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
    private val _transcriptionFlow = MutableSharedFlow<TranscriptionSegment>()
    val transcriptionFlow: SharedFlow<TranscriptionSegment> = _transcriptionFlow

    private var isTranscribing: Boolean = false

    suspend fun initialize() {
        whisperKit = WhisperKit.Builder()
            .setModel("whisperkit-litert/openai_whisper-base")
            .setApplicationContext(context.applicationContext)
            .setCallback { _, result ->
                result?.segments?.forEach { segment ->
                    val text = segment.text
                    if (text.isNotBlank()) {
                        _transcriptionFlow.tryEmit(
                            TranscriptionSegment(
                                text = text,
                                startTimestamp = segment.start.toLong(),
                                endTimestamp = segment.end.toLong(),
                                isFinal = false // Assuming all segments from callback are not final
                            )
                        )
                    }
                }
            }
            .build()
    }

    suspend fun startTranscription(audioData: ByteArray) {
        // Avoid overlapping transcriptions on the same engine instance.
        if (isTranscribing) return

        whisperKit?.let { kit ->
            isTranscribing = true
            val result = kit.transcribe(audioData)
            result?.segments?.forEach { segment ->
                val text = segment.text
                if (text.isNotBlank()) {
                    _transcriptionFlow.tryEmit(
                        TranscriptionSegment(
                            text = text,
                            startTimestamp = segment.start.toLong(),
                            endTimestamp = segment.end.toLong(),
                            isFinal = true // Mark the final segment
                        )
                    )
                }
            }
            isTranscribing = false
        }
    }

    fun stopTranscription() {
        // Since we are now handling the final transcription in startTranscription,
        // this method can be simplified.
    }

    fun release() {
        whisperKit = null
    }
}
