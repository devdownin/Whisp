package com.example.audiotranscription.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun WaveformVisualizer(audioData: ByteArray?) {
    val animatedAudioData = audioData?.map {
        animateFloatAsState(targetValue = it.toFloat()).value
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        if (animatedAudioData != null) {
            val path = Path()
            path.moveTo(0f, size.height / 2)
            animatedAudioData.forEachIndexed { index, value ->
                val x = index.toFloat() * size.width / animatedAudioData.size
                val y = size.height / 2 + value * size.height / 2 / 128
                path.lineTo(x, y)
            }
            drawPath(path, color = Color.Red)
        }
    }
}