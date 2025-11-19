package com.example.audiotranscription.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audiotranscription.R
import com.example.audiotranscription.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val autoSave by viewModel.autoSaveTranscriptions.collectAsState()
    val profanityFilter by viewModel.profanityFilter.collectAsState()
    val punctuation by viewModel.punctuationAndFormatting.collectAsState()

    val context = LocalContext.current
    val comingSoonMessage = stringResource(R.string.coming_soon)

    var languageMenuExpanded by remember { mutableStateOf(false) }
    val languages = listOf("English", "French", "Spanish", "German", "Japanese") // Example list

    var modelMenuExpanded by remember { mutableStateOf(false) }
    val models = listOf("Tiny", "Base", "Small", "Medium", "Large") // Example list

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { Toast.makeText(context, comingSoonMessage, Toast.LENGTH_SHORT).show() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Language & Voice Section
            SettingsSection(title = stringResource(R.string.language_and_voice)) {
                // Language Dropdown
                Box {
                    SettingItem(
                        icon = Icons.Default.Translate,
                        title = stringResource(R.string.transcription_language),
                        subtitle = selectedLanguage,
                        showArrow = true
                    ) { languageMenuExpanded = true }

                    DropdownMenu(
                        expanded = languageMenuExpanded,
                        onDismissRequest = { languageMenuExpanded = false }
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language) },
                                onClick = {
                                    viewModel.setSelectedLanguage(language)
                                    languageMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Model Dropdown
                Box {
                    SettingItem(
                        icon = Icons.Default.Mic,
                        title = stringResource(R.string.voice_recognition_model),
                        subtitle = selectedModel,
                        showArrow = true
                    ) { modelMenuExpanded = true }

                    DropdownMenu(
                        expanded = modelMenuExpanded,
                        onDismissRequest = { modelMenuExpanded = false }
                    ) {
                        models.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model) },
                                onClick = {
                                    viewModel.setSelectedModel(model)
                                    modelMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Transcription Preferences Section
            SettingsSection(title = stringResource(R.string.transcription_preferences)) {
                SwitchSettingItem(
                    icon = Icons.Default.Save,
                    title = stringResource(R.string.auto_save_transcriptions),
                    checked = autoSave,
                    onCheckedChange = viewModel::setAutoSave
                )
                SwitchSettingItem(
                    icon = Icons.Default.Block,
                    title = stringResource(R.string.profanity_filter),
                    subtitle = stringResource(R.string.profanity_filter_description),
                    checked = profanityFilter,
                    onCheckedChange = viewModel::setProfanityFilter
                )
                SwitchSettingItem(
                    icon = Icons.Default.FormatQuote,
                    title = stringResource(R.string.punctuation_and_formatting),
                    checked = punctuation,
                    onCheckedChange = viewModel::setPunctuation
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Data Management Section
            SettingsSection(title = stringResource(R.string.data_management)) {
                ClickableSettingItem(
                    icon = Icons.Default.Upload,
                    title = stringResource(R.string.export_all_transcriptions),
                    onClick = {
                        viewModel.exportAllTranscriptions()
                        Toast.makeText(context, comingSoonMessage, Toast.LENGTH_SHORT).show()
                    }
                )
                ClickableSettingItem(
                    icon = Icons.Default.Delete,
                    title = stringResource(R.string.clear_transcription_history),
                    onClick = viewModel::clearTranscriptionHistory,
                    isDestructive = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About & Support Section
            SettingsSection(title = stringResource(R.string.about_and_support)) {
                SettingItem(
                    icon = Icons.Default.Help,
                    title = stringResource(R.string.help_and_faq),
                    showArrow = true
                ) { Toast.makeText(context, comingSoonMessage, Toast.LENGTH_SHORT).show() }
                SettingItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.app_version),
                    subtitle = "2.1.3 (Build 45)"
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card {
            Column {
                content()
            }
        }
    }
}


@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (showArrow) {
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun SwitchSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ClickableSettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isDestructive) MaterialTheme.colorScheme.error else LocalContentColor.current
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDestructive) MaterialTheme.colorScheme.error else LocalContentColor.current
        )
    }
}
