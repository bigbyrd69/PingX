package com.ping.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ping.app.domain.model.MeshPacket
import kotlin.math.max

@Composable
fun MessageVisualization(packet: MeshPacket?) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val pathColor = MaterialTheme.colorScheme.primary
        val inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

        val maxSteps = max(packet?.hopCount ?: 0, 1)
        val spacing = size.width / (maxSteps + 1)
        val centerY = size.height / 2

        for (index in 0..maxSteps) {
            val x = spacing * (index + 1)
            drawCircle(
                color = if (index <= (packet?.hopCount ?: 0)) pathColor else inactiveColor,
                radius = 12f,
                center = Offset(x, centerY)
            )
            if (index < maxSteps) {
                drawLine(
                    color = if (index < (packet?.hopCount ?: 0)) pathColor else inactiveColor,
                    start = Offset(x + 12f, centerY),
                    end = Offset(x + spacing - 12f, centerY),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
            }
        }

        val ttlProgress = ((packet?.ttl ?: 0).coerceAtLeast(0) / 8f).coerceIn(0f, 1f)
        drawArc(
            color = pathColor,
            startAngle = 180f,
            sweepAngle = 180f * ttlProgress,
            useCenter = false,
            style = Stroke(width = 6f),
            topLeft = Offset(size.width * 0.15f, size.height * 0.15f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.7f, size.height * 0.7f)
        )
    }
}
