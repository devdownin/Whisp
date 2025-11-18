package com.example.audiotranscription.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.audiotranscription.R
import com.example.audiotranscription.data.models.ModelStatus
import com.example.audiotranscription.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val models by viewModel.models.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(models) { model ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(model.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        when (model.status) {
                            ModelStatus.NOT_DOWNLOADED -> {
                                Button(onClick = { viewModel.downloadModel(model) }) {
                                    Text("Download")
                                }
                            }
                            ModelStatus.DOWNLOADING -> {
                                LinearProgressIndicator(
                                    progress = model.progress,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            ModelStatus.DOWNLOADED -> {
                                Button(onClick = { viewModel.deleteModel(model) }) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}