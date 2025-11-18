package com.example.audiotranscription.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiotranscription.data.models.Model
import com.example.audiotranscription.data.models.ModelManager
import com.example.audiotranscription.data.models.ModelStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val modelManager: ModelManager
) : ViewModel() {
    private val _models = MutableStateFlow<List<Model>>(emptyList())
    val models: StateFlow<List<Model>> = _models.asStateFlow()

    init {
        _models.value = modelManager.getModels()
    }

    fun downloadModel(model: Model) {
        viewModelScope.launch {
            modelManager.downloadModel(model).collect { progress ->
                updateModel(model.name, ModelStatus.DOWNLOADING, progress)
            }
            updateModel(model.name, ModelStatus.DOWNLOADED)
        }
    }

    fun deleteModel(model: Model) {
        modelManager.deleteModel(model)
        updateModel(model.name, ModelStatus.NOT_DOWNLOADED)
    }

    private fun updateModel(name: String, status: ModelStatus, progress: Float = 0f) {
        _models.value = _models.value.map {
            if (it.name == name) {
                it.copy(status = status, progress = progress)
            } else {
                it
            }
        }
    }
}