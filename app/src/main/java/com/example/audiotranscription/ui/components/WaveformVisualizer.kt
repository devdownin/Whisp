package com.example.audiotranscription.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun WaveformVisualizer(audioData: ByteArray?) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        if (audioData != null) {
            val path = Path()
            path.moveTo(0f, size.height / 2)
            audioData.forEachIndexed { index, byte ->
                val x = index.toFloat() * size.width / audioData.size
                val y = size.height / 2 + byte * size.height / 2 / 128
                path.lineTo(x, y)
            }
            drawPath(path, color = Color.Red)
        }
    }
}