package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Core Colors
val SearxIndigo = Color(0xFF6366F1)
val SearxViolet = Color(0xFF8B5CF6)
val SearxPurple = Color(0xFFA855F7)
val SearxCyan = Color(0xFF38BDF8)
val SearxSky = Color(0xFF0EA5E9)

// Dark Theme Surfaces
val DarkBg = Color(0xFF0B0F19)
val DarkSurface = Color(0xFF111827)
val DarkSurfaceVariant = Color(0xFF1E293B)
val DarkSurfaceContainer = Color(0xFF182234)
val DarkBorder = Color(0xFF334155)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextTertiary = Color(0xFF64748B)

// Light Theme Surfaces
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightBorder = Color(0xFFE2E8F0)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)

// Status Colors
val StatusSuccess = Color(0xFF10B981)
val StatusWarning = Color(0xFFF59E0B)
val StatusError = Color(0xFFEF4444)
val StatusActive = Color(0xFF38BDF8)
val StatusPaused = Color(0xFFFBBF24)

// Platform Colors
val PlatformYouTube = Color(0xFFFF0000)
val PlatformFacebook = Color(0xFF1877F2)
val PlatformInstagram = Color(0xFFE1306C)
val PlatformTikTok = Color(0xFF00F2FE)

// Gradients
val SearxBrandGradient = Brush.horizontalGradient(
    colors = listOf(SearxIndigo, SearxViolet, SearxCyan)
)

val SearxAccentGradient = Brush.linearGradient(
    colors = listOf(SearxViolet, SearxSky)
)

val SearxCardGradient = Brush.linearGradient(
    colors = listOf(DarkSurfaceVariant.copy(alpha = 0.8f), DarkSurface.copy(alpha = 0.95f))
)
