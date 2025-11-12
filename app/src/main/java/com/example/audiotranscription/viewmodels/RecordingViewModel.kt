package com.example.audiotranscription.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiotranscription.data.audio.AudioRecorder
import com.example.audiotranscription.data.repository.AppDatabase
import com.example.audiotranscription.data.repository.TranscriptionRepository
import com.example.audiotranscription.data.transcription.WhisperEngine
import com.example.audiotranscription.domain.models.Transcription
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecordingViewModel(
    application: Application,
    private val audioRecorder: AudioRecorder,
    private val whisperEngine: WhisperEngine
) : AndroidViewModel(application) {
    private val repository: TranscriptionRepository

    init {
        val transcriptionDao = AppDatabase.getDatabase(application).transcriptionDao()
        repository = TranscriptionRepository(transcriptionDao)
    }
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    val transcription: StateFlow<String> = whisperEngine.transcriptionState

    init {
        viewModelScope.launch {
            whisperEngine.initialize()
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
            repository.insert(Transcription(text = transcription.value))
        }
    }

    override fun onCleared() {
        super.onCleared()
        whisperEngine.release()
    }

    val audioData: StateFlow<ByteArray?> = audioRecorder.audioData
}