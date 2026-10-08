package com.earthquakealert.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.earthquakealert.app.ui.i18n.LocalAppStrings
import com.earthquakealert.app.ui.i18n.Strings

private val LightExtendedColors = ExtendedColors(
    background = LightBackground,
    surface = LightSurface,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    border = LightBorder,
    muted = LightMuted,
    emergency = LightEmergency,
    emergencyDark = LightEmergencyDark,
    warning = LightWarning,
    success = LightSuccess,
    info = LightInfo,
    headerBackground = BmkgHeaderBg,
    brandGreen = BmkgGreen,
    titleMaroon = BmkgTitleMaroon,
    isDark = false
)

private val DarkExtendedColors = ExtendedColors(
    background = DarkBackground,
    surface = DarkSurface,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    border = DarkBorder,
    muted = DarkMuted,
    emergency = DarkEmergency,
    emergencyDark = DarkEmergencyDark,
    warning = DarkWarning,
    success = DarkSuccess,
    info = DarkInfo,
    headerBackground = BmkgHeaderDark,
    brandGreen = BmkgGreenLight,
    titleMaroon = BmkgTitleMaroonDark,
    isDark = true
)

private val LightColorScheme = lightColorScheme(
    primary = LightTextPrimary,
    onPrimary = LightSurface,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    error = LightEmergency,
    onError = LightSurface,
    outline = LightBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkTextPrimary,
    onPrimary = DarkSurface,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    error = DarkEmergency,
    onError = DarkTextPrimary,
    outline = DarkBorder
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

object EarthquakeTheme {
    val colors: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current

    val strings: Strings
        @Composable
        @ReadOnlyComposable
        get() = LocalAppStrings.current

    val spacing: AppSpacing
        get() = AppSpacing

    val radius: AppRadius
        get() = AppRadius

    val dimensions: AppDimensions
        get() = AppDimensions
}

@Composable
fun EarthquakeAlertTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
