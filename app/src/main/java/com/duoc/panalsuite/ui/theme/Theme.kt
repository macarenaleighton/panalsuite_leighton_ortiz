package com.duoc.panalsuite.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Lavender,
    onPrimary = Color.White,
    primaryContainer = LavenderLight,
    onPrimaryContainer = Color(0xFF25105C),
    secondary = Lilac,
    onSecondary = Color.White,
    secondaryContainer = LilacLight,
    onSecondaryContainer = Color(0xFF2A1F3D),
    background = Mist,
    onBackground = Plum,
    surface = Mist,
    onSurface = Plum,
    surfaceVariant = MistVariant,
    onSurfaceVariant = PlumVariant
)

private val DarkColors = darkColorScheme(
    primary = LavenderPastel,
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = LavenderLight,
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = LilacLight,
    background = NightPlum,
    onBackground = Color(0xFFE6E0EC),
    surface = NightPlum,
    onSurface = Color(0xFFE6E0EC),
    surfaceVariant = NightPlumVariant,
    onSurfaceVariant = Color(0xFFCBC4D6)
)

@Composable
fun PanalSuiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}