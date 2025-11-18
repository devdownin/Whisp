package com.example.audiotranscription.data.models

import com.argmaxinc.whisperkit.WhisperKit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

enum class ModelStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED
}

data class Model(
    val name: String,
    val status: ModelStatus = ModelStatus.NOT_DOWNLOADED,
    val progress: Float = 0f
)

@Singleton
class ModelManager @Inject constructor() {
    private val models = mutableListOf(
        Model("tiny"),
        Model("base"),
        Model("small"),
        Model("medium")
    )

    fun getModels(): List<Model> {
        // TODO: Check download status of each model
        return models
    }

    fun downloadModel(model: Model): Flow<Float> {
        return flow {
            // TODO: Implement actual download logic using WhisperKit
            for (i in 1..100) {
                emit(i.toFloat() / 100)
                kotlinx.coroutines.delay(100)
            }
        }
    }

    fun deleteModel(model: Model) {
        // TODO: Implement delete logic
    }
}