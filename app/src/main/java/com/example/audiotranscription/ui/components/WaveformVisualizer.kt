package com.example.audiotranscription.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun WaveformVisualizer(audioData: ByteArray?) {
    val waveformSamples = remember(audioData) {
        audioData?.map { it.toFloat() } ?: emptyList()
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        if (waveformSamples.isNotEmpty()) {
            val path = Path()
            path.moveTo(0f, size.height / 2)
            waveformSamples.forEachIndexed { index, value ->
                val x = index.toFloat() * size.width / waveformSamples.size
                val y = size.height / 2 + value * size.height / 2 / 128
                path.lineTo(x, y)
            }
            drawPath(path, color = Color.Red)
        }
    }
}
