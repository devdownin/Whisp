package com.example.audiotranscription.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audiotranscription.viewmodels.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val transcriptions by viewModel.transcriptions.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showDialog by remember { mutableStateOf<Int?>(null) }

    if (showDialog != null) {
        AlertDialog(
            onDismissRequest = { showDialog = null },
            title = { Text("Delete Transcription") },
            text = { Text("Are you sure you want to delete this transcription?") },
            confirmButton = {
                Button(onClick = {
                    transcriptions.find { it.id == showDialog }?.let {
                        viewModel.delete(it)
                    }
                    showDialog = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.search(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search...") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transcriptions) { transcription ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(transcription.timestamp)),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = transcription.text,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3
                        )
                    }
                    IconButton(onClick = { showDialog = transcription.id }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}