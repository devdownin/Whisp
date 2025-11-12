package com.example.audiotranscription.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.core.content.ContextCompat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.audiotranscription.ui.components.WaveformVisualizer
import com.example.audiotranscription.data.transcription.WhisperEngine
import java.io.File
import java.io.FileOutputStream
import com.example.audiotranscription.ui.theme.AudioTranscriptionTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.audiotranscription.ui.screens.HistoryScreen
import com.example.audiotranscription.ui.screens.SettingsScreen
import com.example.audiotranscription.viewmodels.RecordingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audiotranscription.data.audio.AudioRecorder

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                // showInContextUI(...)
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }


        setContent {
            AudioTranscriptionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "recording") {
                        composable("recording") { RecordingScreen(navController) }
                        composable("settings") { SettingsScreen() }
                        composable("history") { HistoryScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun RecordingScreen(
    navController: androidx.navigation.NavController,
    // TODO: Replace with a real ViewModel instance
    viewModel: RecordingViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val context = androidx.compose.ui.platform.LocalContext.current
                return RecordingViewModel(
                    context.applicationContext as android.app.Application,
                    AudioRecorder(context),
                    WhisperEngine(context)
                ) as T
            }
        }
    )
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val audioData by viewModel.audioData.collectAsState()
    val transcription by viewModel.transcription.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = transcription,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        WaveformVisualizer(audioData = audioData)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilledTonalButton(onClick = {
                if (isRecording) {
                    viewModel.stopRecording()
                } else {
                    viewModel.startRecording()
                }
            }) {
                Icon(if (isRecording) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(if (isRecording) stringResource(R.string.stop_recording) else stringResource(R.string.start_recording))
            }
            val context = LocalContext.current
            FilledTonalButton(onClick = {
                copyToClipboard(context, transcription)
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.copy))
            }
            FilledTonalButton(onClick = {
                shareText(context, transcription)
            }) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.share))
            }
            FilledTonalButton(onClick = {
                saveTranscriptionToFile(context, transcription)
            }) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.save))
            }
            FilledTonalButton(onClick = {
                navController.navigate("settings")
            }) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.settings))
            }
            FilledTonalButton(onClick = {
                navController.navigate("history")
            }) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.history))
            }
        }
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("transcription", text)
    clipboard.setPrimaryClip(clip)
}

fun shareText(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

fun saveTranscriptionToFile(context: Context, text: String) {
    val file = File(context.getExternalFilesDir(null), "transcription.txt")
    FileOutputStream(file).use {
        it.write(text.toByteArray())
    }
}

@Preview(showBackground = true)
@Composable
fun RecordingScreenPreview() {
    AudioTranscriptionTheme {
        // Dummy NavController for preview
        RecordingScreen(navController = rememberNavController())
    }
}