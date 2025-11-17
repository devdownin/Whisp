package com.example.audiotranscription.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audiotranscription.R
import com.example.audiotranscription.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedProcessingMode by viewModel.selectedProcessingMode.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Model selection
        Text(stringResource(R.string.whisper_model))
        var modelMenuExpanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { modelMenuExpanded = true }) {
                Text(selectedModel)
            }
            DropdownMenu(
                expanded = modelMenuExpanded,
                onDismissRequest = { modelMenuExpanded = false }
            ) {
                DropdownMenuItem(text = { Text(stringResource(R.string.tiny)) }, onClick = {
                    viewModel.setModel("tiny")
                    modelMenuExpanded = false
                })
                DropdownMenuItem(text = { Text(stringResource(R.string.base)) }, onClick = {
                    viewModel.setModel("base")
                    modelMenuExpanded = false
                })
                DropdownMenuItem(text = { Text(stringResource(R.string.small)) }, onClick = {
                    viewModel.setModel("small")
                    modelMenuExpanded = false
                })
                DropdownMenuItem(text = { Text(stringResource(R.string.medium)) }, onClick = {
                    viewModel.setModel("medium")
                    modelMenuExpanded = false
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language selection
        Text(stringResource(R.string.language))
        var languageMenuExpanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { languageMenuExpanded = true }) {
                Text(selectedLanguage)
            }
            DropdownMenu(
                expanded = languageMenuExpanded,
                onDismissRequest = { languageMenuExpanded = false }
            ) {
                // Example set of common Whisper languages; use ISO codes
                DropdownMenuItem(text = { Text("English") }, onClick = {
                    viewModel.setLanguage("en")
                    languageMenuExpanded = false
                })
                DropdownMenuItem(text = { Text("Français") }, onClick = {
                    viewModel.setLanguage("fr")
                    languageMenuExpanded = false
                })
                DropdownMenuItem(text = { Text("Español") }, onClick = {
                    viewModel.setLanguage("es")
                    languageMenuExpanded = false
                })
                DropdownMenuItem(text = { Text("French") }, onClick = {
                    viewModel.setLanguage("fr")
                    languageMenuExpanded = false
                })
                DropdownMenuItem(text = { Text("Auto") }, onClick = {
                    viewModel.setLanguage("auto")
                    languageMenuExpanded = false
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Processing mode selection
        Text(stringResource(R.string.processing_mode))
        Row {
            RadioButton(
                selected = selectedProcessingMode == "streaming",
                onClick = { viewModel.setProcessingMode("streaming") }
            )
            Text(stringResource(R.string.streaming))
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = selectedProcessingMode == "batch",
                onClick = { viewModel.setProcessingMode("batch") }
            )
            Text(stringResource(R.string.batch))
        }
    }
}
