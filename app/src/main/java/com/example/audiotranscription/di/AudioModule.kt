package com.example.audiotranscription.di

import android.content.Context
import com.example.audiotranscription.data.audio.AudioRecorder
import com.example.audiotranscription.data.models.ModelManager
import com.example.audiotranscription.data.transcription.WhisperEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioModule {

    @Provides
    @Singleton
    fun provideAudioRecorder(@ApplicationContext context: Context): AudioRecorder {
        return AudioRecorder(context)
    }

    @Provides
    @Singleton
    fun provideWhisperEngine(@ApplicationContext context: Context): WhisperEngine {
        return WhisperEngine(context)
    }

    @Provides
    @Singleton
    fun provideModelManager(): ModelManager {
        return ModelManager()
    }
}