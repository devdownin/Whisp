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
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.audiotranscription.R
import com.example.audiotranscription.domain.models.TranscriptionSegment
import com.example.audiotranscription.ui.components.WaveformVisualizer
import com.example.audiotranscription.ui.screens.HistoryScreen
import com.example.audiotranscription.ui.screens.SettingsScreen
import com.example.audiotranscription.ui.theme.AudioTranscriptionTheme
import com.example.audiotranscription.viewmodels.RecordingViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
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
    @OptIn(ExperimentalAnimationApi::class)
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
                    val navController = rememberAnimatedNavController()
                    AnimatedNavHost(navController = navController, startDestination = "recording") {
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
    navController: NavController,
    viewModel: RecordingViewModel = hiltViewModel()
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val audioData by viewModel.audioData.collectAsState()
    val transcriptionSegments by viewModel.transcriptionSegments.collectAsState()

    RecordingScreenContent(
        navController = navController,
        isRecording = isRecording,
        audioData = audioData,
        transcriptionSegments = transcriptionSegments,
        onStartRecording = viewModel::startRecording,
        onStopRecording = viewModel::stopRecording
    )
}

@Composable
fun RecordingScreenContent(
    navController: NavController,
    isRecording: Boolean,
    audioData: ByteArray?,
    transcriptionSegments: List<TranscriptionSegment>,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val transcriptionText = remember(transcriptionSegments) {
        transcriptionSegments.joinToString(separator = "\n") { it.text }.trim()
    }
    val copyShareEnabled = transcriptionText.isNotEmpty()
    val srtEnabled = transcriptionSegments.isNotEmpty()
    val infiniteTransition = rememberInfiniteTransition(label = "recording_scale")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recording_scale_anim"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(transcriptionSegments) { segment ->
                Text("${segment.startTimestamp} - ${segment.endTimestamp}: ${segment.text}")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        WaveformVisualizer(audioData = audioData)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilledTonalButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (isRecording) {
                        onStopRecording()
                    } else {
                        onStartRecording()
                    }
                },
                modifier = Modifier.scale(scale)
            ) {
                Icon(
                    if (isRecording) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    if (isRecording) {
                        stringResource(R.string.stop_recording)
                    } else {
                        stringResource(R.string.start_recording)
                    }
                )
            }
            FilledTonalButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    copyToClipboard(context, transcriptionText)
                },
                enabled = copyShareEnabled
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.copy))
            }
            FilledTonalButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    shareText(context, transcriptionText)
                },
                enabled = copyShareEnabled
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.share))
            }
            FilledTonalButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    saveTranscriptionToFile(context, transcriptionText)
                },
                enabled = copyShareEnabled
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.save))
            }
            FilledTonalButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                navController.navigate("settings")
            }) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.settings))
            }
            FilledTonalButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                navController.navigate("history")
            }) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.history))
            }
            FilledTonalButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val srtText = toSrt(transcriptionSegments)
                    saveTranscriptionToFile(context, srtText, "transcription.srt")
                },
                enabled = srtEnabled
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Export SRT")
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

fun saveTranscriptionToFile(context: Context, text: String, fileName: String = "transcription.txt") {
    val file = File(context.getExternalFilesDir(null), fileName)
    FileOutputStream(file).use {
        it.write(text.toByteArray())
    }
}

fun toSrt(segments: List<TranscriptionSegment>): String {
    val builder = StringBuilder()
    segments.forEachIndexed { index, segment ->
        builder.append(index + 1)
        builder.append("\n")
        builder.append(formatSrtTimestamp(segment.startTimestamp))
        builder.append(" --> ")
        builder.append(formatSrtTimestamp(segment.endTimestamp))
        builder.append("\n")
        builder.append(segment.text)
        builder.append("\n\n")
    }
    return builder.toString()
}

fun formatSrtTimestamp(timestamp: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(timestamp)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timestamp) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timestamp) % 60
    val milliseconds = timestamp % 1000
    return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, milliseconds)
}

@Preview(showBackground = true)
@Composable
fun RecordingScreenPreview() {
    AudioTranscriptionTheme {
        RecordingScreenContent(
            navController = rememberNavController(),
            isRecording = false,
            audioData = ByteArray(128) { ((it % 32) * 4).toByte() },
            transcriptionSegments = listOf(
                TranscriptionSegment("Hello world", 0, 2_000),
                TranscriptionSegment("This is a preview", 2_000, 5_000)
            ),
            onStartRecording = {},
            onStopRecording = {}
        )
    }
}
