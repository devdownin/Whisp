package com.example.audiotranscription.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audiotranscription.viewmodels.HistoryViewModel

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val allTranscriptions by viewModel.allTranscriptions.collectAsState(initial = emptyList())

    LazyColumn {
        items(allTranscriptions) { transcription ->
            Text(text = transcription.text)
        }
    }
}