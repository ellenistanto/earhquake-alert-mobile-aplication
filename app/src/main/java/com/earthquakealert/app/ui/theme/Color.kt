package com.earthquakealert.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// BMKG Brand Palette
val BmkgBlue = Color(0xFF1E88E5)
val BmkgBlueDark = Color(0xFF1565C0)
val BmkgHeaderBg = Color(0xFF1B72D0)
val BmkgHeaderDark = Color(0xFF131F2E)
val BmkgGreen = Color(0xFF2E7D32)
val BmkgGreenLight = Color(0xFF4CAF50)
val BmkgRed = Color(0xFFD32F2F)
val BmkgOrange = Color(0xFFF57C00)
val BmkgTitleMaroon = Color(0xFF531215)
val BmkgTitleMaroonDark = Color(0xFFFF8A80)

// Light Theme Colors per DESIGN.md & BMKG
val LightBackground = Color(0xFFF4F6F9)
val LightSurface = Color(0xFFFFFFFF)
val LightTextPrimary = Color(0xFF1A1A1A)
val LightTextSecondary = Color(0xFF616161)
val LightBorder = Color(0xFFE2E4E8)
val LightMuted = Color(0xFF8E8E93)

val LightEmergency = Color(0xFFD32F2F)
val LightEmergencyDark = Color(0xFFB71C1C)
val LightWarning = Color(0xFFF57C00)
val LightSuccess = Color(0xFF2E7D32)
val LightInfo = Color(0xFF1976D2)

// Dark Theme Colors per DESIGN.md & BMKG
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkTextPrimary = Color(0xFFF2F2F2)
val DarkTextSecondary = Color(0xFFA0A0A5)
val DarkBorder = Color(0xFF2E2E32)
val DarkMuted = Color(0xFF75757A)

val DarkEmergency = Color(0xFFFF5252)
val DarkEmergencyDark = Color(0xFFD32F2F)
val DarkWarning = Color(0xFFFFB74D)
val DarkSuccess = Color(0xFF4CAF50)
val DarkInfo = Color(0xFF64B5F6)

@Immutable
data class ExtendedColors(
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val muted: Color,
    val emergency: Color,
    val emergencyDark: Color,
    val warning: Color,
    val success: Color,
    val info: Color,
    val headerBackground: Color = BmkgHeaderBg,
    val brandGreen: Color = BmkgGreen,
    val titleMaroon: Color = BmkgTitleMaroon,
    val isDark: Boolean = false
)
