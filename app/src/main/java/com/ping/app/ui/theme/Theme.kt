package com.ping.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PingColorScheme = darkColorScheme(
    primary = PingGreen,
    secondary = PingGreenDim,
    background = PingBlack,
    surface = PingDarkSurface,
    onPrimary = PingBlack,
    onSecondary = PingBlack,
    onBackground = PingText,
    onSurface = PingText,
    error = androidx.compose.ui.graphics.Color(0xFFFF3B30),
    onError = androidx.compose.ui.graphics.Color(0xFF101010)
)

@Composable
fun PingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PingColorScheme,
        typography = Typography,
        content = content
    )
}
