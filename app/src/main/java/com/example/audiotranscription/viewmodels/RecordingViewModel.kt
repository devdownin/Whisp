package com.example.audiotranscription.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiotranscription.data.audio.AudioRecorder
import com.example.audiotranscription.data.repository.TranscriptionRepository
import com.example.audiotranscription.data.transcription.WhisperEngine
import com.example.audiotranscription.domain.models.Transcription
import com.example.audiotranscription.domain.models.TranscriptionSegment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val whisperEngine: WhisperEngine,
    private val repository: TranscriptionRepository
) : ViewModel() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _transcriptionSegments = MutableStateFlow<List<TranscriptionSegment>>(emptyList())
    val transcriptionSegments: StateFlow<List<TranscriptionSegment>> = _transcriptionSegments

    init {
        viewModelScope.launch {
            whisperEngine.initialize()
            whisperEngine.transcriptionFlow.collect { text ->
                val currentSegments = _transcriptionSegments.value.toMutableList()
                if (currentSegments.isNotEmpty()) {
                    currentSegments[currentSegments.lastIndex] = TranscriptionSegment(text, 0L, 0L)
                } else {
                    currentSegments.add(TranscriptionSegment(text, 0L, 0L))
                }
                _transcriptionSegments.value = currentSegments
            }
        }
        observeAudioData()
    }

    private fun observeAudioData() {
        viewModelScope.launch {
            audioRecorder.audioData
                .filterNotNull()
                .collect { audioData ->
                    if (_isRecording.value) {
                        whisperEngine.startTranscription(audioData)
                    }
                }
        }
    }

    fun startRecording() {
        viewModelScope.launch {
            audioRecorder.startRecording()
            _isRecording.value = true
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            audioRecorder.stopRecording()
            _isRecording.value = false
            val segments = transcriptionSegments.value
            if (segments.isNotEmpty()) {
                val fullText = segments.joinToString(separator = "\n") { it.text }
                repository.insert(Transcription(text = fullText, segments = segments))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        whisperEngine.release()
    }

    fun clearTranscription() {
        _transcriptionSegments.value = emptyList()
    }

    val audioData: StateFlow<ByteArray?> = audioRecorder.audioData
}