package com.example.audiotranscription.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiotranscription.data.audio.AudioRecorder
import com.example.audiotranscription.data.repository.SettingsRepository
import com.example.audiotranscription.data.repository.TranscriptionRepository
import com.example.audiotranscription.data.transcription.WhisperEngine
import com.example.audiotranscription.domain.models.Transcription
import com.example.audiotranscription.domain.models.TranscriptionSegment
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioRecorder: AudioRecorder,
    private val whisperEngine: WhisperEngine,
    private val repository: TranscriptionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _transcriptionSegments = MutableStateFlow<List<TranscriptionSegment>>(emptyList())
    val transcriptionSegments: StateFlow<List<TranscriptionSegment>> = _transcriptionSegments

    init {
        viewModelScope.launch {
            whisperEngine.initialize()
            whisperEngine.transcriptionFlow
                .flowOn(Dispatchers.Main)
                .collect { newSegment ->
                    val currentSegments = _transcriptionSegments.value.toMutableList()
                    if (currentSegments.isNotEmpty() && !currentSegments.last().isFinal) {
                        // Update the last segment
                        currentSegments[currentSegments.lastIndex] = newSegment
                    } else {
                        // Add a new segment
                        currentSegments.add(newSegment)
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
            _transcriptionSegments.value = emptyList() // Clear previous transcription
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

                if (settingsRepository.autoSave.first()) {
                    saveTranscriptionToFile(fullText)
                }
            }
        }
    }

    private fun saveTranscriptionToFile(text: String) {
        val fileName = "transcription_${System.currentTimeMillis()}.txt"
        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use {
            it.write(text.toByteArray())
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
