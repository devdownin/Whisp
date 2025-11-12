package com.example.audiotranscription.data.transcription

import android.content.Context
import com.argmaxinc.whisperkit.WhisperKit
import com.argmaxinc.whisperkit.ExperimentalWhisperKit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalWhisperKit::class)
class WhisperEngine(
    private val context: Context
) {
    private var whisperKit: WhisperKit? = null
    private val _transcriptionState = MutableStateFlow("")
    val transcriptionState: StateFlow<String> = _transcriptionState

    suspend fun initialize() {
        whisperKit = WhisperKit.Builder()
            .setModel(WhisperKit.OPENAI_TINY_EN)
            .setApplicationContext(context.applicationContext)
            .build()
    }

    suspend fun startTranscription(audioData: ByteArray) {
        whisperKit?.let {
            val result = it.transcribe(audioData)
            _transcriptionState.value = result?.text ?: ""
        }
    }

    fun stopTranscription() {
        // TODO: Stop audio recording and finalize transcription
    }

    fun release() {
        whisperKit?.close()
    }
}