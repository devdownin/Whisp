package com.example.audiotranscription.di

import android.content.Context
import com.example.audiotranscription.data.repository.AppDatabase
import com.example.audiotranscription.data.repository.TranscriptionDao
import com.example.audiotranscription.data.repository.TranscriptionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideTranscriptionDao(appDatabase: AppDatabase): TranscriptionDao {
        return appDatabase.transcriptionDao()
    }

    @Provides
    fun provideTranscriptionRepository(transcriptionDao: TranscriptionDao): TranscriptionRepository {
        return TranscriptionRepository(transcriptionDao)
    }
}