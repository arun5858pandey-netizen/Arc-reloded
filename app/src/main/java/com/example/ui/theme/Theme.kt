package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ArisePurple,
    onPrimary = AriseBlack,
    primaryContainer = ArisePurpleDark,
    onPrimaryContainer = ArisePurpleNeon,
    secondary = AriseCyan,
    onSecondary = AriseBlack,
    tertiary = AriseMagenta,
    background = AriseBlack,
    onBackground = AriseTextPrimary,
    surface = AriseDarkSurface,
    onSurface = AriseTextPrimary,
    surfaceVariant = AriseCardBg,
    onSurfaceVariant = AriseTextSecondary,
    outline = AriseBorder
)

@Composable
fun AriseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
