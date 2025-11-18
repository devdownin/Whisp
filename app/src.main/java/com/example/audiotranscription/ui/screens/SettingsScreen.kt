package com.example.audiotranscription.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var selectedModel by remember { mutableStateOf("tiny") }
    var selectedLanguage by remember { mutableStateOf("en") }
    var selectedProcessingMode by remember { mutableStateOf("streaming") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Model selection
        Text("Whisper Model")
        DropdownMenu(
            expanded = false,
            onDismissRequest = { /*TODO*/ }
        ) {
            DropdownMenuItem(text = { Text("Tiny") }, onClick = { selectedModel = "tiny" })
            DropdownMenuItem(text = { Text("Base") }, onClick = { selectedModel = "base" })
            DropdownMenuItem(text = { Text("Small") }, onClick = { selectedModel = "small" })
            DropdownMenuItem(text = { Text("Medium") }, onClick = { selectedModel = "medium" })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language selection
        Text("Language")
        // TODO: Add language selection dropdown

        Spacer(modifier = Modifier.height(16.dp))

        // Processing mode selection
        Text("Processing Mode")
        Row {
            RadioButton(
                selected = selectedProcessingMode == "streaming",
                onClick = { selectedProcessingMode = "streaming" }
            )
            Text("Streaming")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = selectedProcessingMode == "batch",
                onClick = { selectedProcessingMode = "batch" }
            )
            Text("Batch")
        }
    }
}