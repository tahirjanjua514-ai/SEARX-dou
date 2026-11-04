package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SearxIndigo,
    onPrimary = DarkTextPrimary,
    primaryContainer = SearxViolet.copy(alpha = 0.3f),
    onPrimaryContainer = DarkTextPrimary,
    secondary = SearxCyan,
    onSecondary = DarkBg,
    secondaryContainer = SearxCyan.copy(alpha = 0.2f),
    onSecondaryContainer = DarkTextPrimary,
    tertiary = SearxPurple,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = StatusError
)

private val LightColorScheme = lightColorScheme(
    primary = SearxIndigo,
    onPrimary = LightSurface,
    primaryContainer = SearxIndigo.copy(alpha = 0.15f),
    onPrimaryContainer = SearxIndigo,
    secondary = SearxSky,
    onSecondary = LightSurface,
    secondaryContainer = SearxSky.copy(alpha = 0.15f),
    onSecondaryContainer = LightTextPrimary,
    tertiary = SearxPurple,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = StatusError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to premium dark theme
    dynamicColor: Boolean = false, // Keep branded blue/violet theme consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
