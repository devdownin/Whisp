package com.example.audiotranscription.data.repository

import androidx.room.TypeConverter
import com.example.audiotranscription.domain.models.TranscriptionSegment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromTranscriptionSegmentList(value: List<TranscriptionSegment>): String {
        val gson = Gson()
        val type = object : TypeToken<List<TranscriptionSegment>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toTranscriptionSegmentList(value: String): List<TranscriptionSegment> {
        val gson = Gson()
        val type = object : TypeToken<List<TranscriptionSegment>>() {}.type
        return gson.fromJson(value, type)
    }
}