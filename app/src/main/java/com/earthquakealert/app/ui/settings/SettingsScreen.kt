package com.earthquakealert.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.earthquakealert.app.domain.model.AppLanguage
import com.earthquakealert.app.domain.model.AppThemeMode
import com.earthquakealert.app.domain.model.DistanceMode
import com.earthquakealert.app.ui.components.AppDivider
import com.earthquakealert.app.ui.components.SecondaryButton
import com.earthquakealert.app.ui.components.SegmentedPicker
import com.earthquakealert.app.ui.components.SettingsInfoRow
import com.earthquakealert.app.ui.components.SettingsSectionHeader
import com.earthquakealert.app.ui.components.SettingsToggleRow
import com.earthquakealert.app.ui.theme.AppRadius
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EarthquakeTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onPreviewAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings = uiState.settings
    val scrollState = rememberScrollState()
    val str = EarthquakeTheme.strings

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EarthquakeTheme.colors.background)
            .verticalScroll(scrollState)
            .padding(AppSpacing.md)
    ) {
        Text(
            text = str.settingsTitle,
            style = AppTypography.headlineMedium,
            color = EarthquakeTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Language Selection Group
        SettingsSectionHeader(title = str.languageSection)
        Text(
            text = str.languageSubtitle,
            style = AppTypography.bodySmall,
            color = EarthquakeTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        SegmentedPicker(
            options = listOf(
                AppLanguage.SYSTEM to str.languageSystem,
                AppLanguage.INDONESIAN to str.languageIndonesian,
                AppLanguage.ENGLISH to str.languageEnglish
            ),
            selectedOption = settings.language,
            onOptionSelected = { viewModel.setLanguage(it) }
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))
        AppDivider()
        Spacer(modifier = Modifier.height(AppSpacing.md))

        // Theme Mode Group
        SettingsSectionHeader(title = str.themeSection)
        Text(
            text = str.themeSubtitle,
            style = AppTypography.bodySmall,
            color = EarthquakeTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        SegmentedPicker(
            options = listOf(
                AppThemeMode.SYSTEM to str.themeSystem,
                AppThemeMode.LIGHT to str.themeLight,
                AppThemeMode.DARK to str.themeDark
            ),
            selectedOption = settings.themeMode,
            onOptionSelected = { viewModel.setThemeMode(it) }
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))
        AppDivider()
        Spacer(modifier = Modifier.height(AppSpacing.md))

        // Alerts Master Group
        SettingsSectionHeader(title = str.alertsSection)
        SettingsToggleRow(
            title = str.alertsTitle,
            subtitle = str.alertsSubtitle,
            checked = settings.alertEnabled,
            onCheckedChange = { viewModel.toggleAlertEnabled(it) }
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))
        AppDivider()
        Spacer(modifier = Modifier.height(AppSpacing.md))

        // Sensitivity Group
        SettingsSectionHeader(title = str.criteriaSection)
        SettingsToggleRow(
            title = str.strongQuakeTitle,
            subtitle = str.strongQuakeSubtitle,
            checked = settings.strongEarthquakeEnabled,
            onCheckedChange = { viewModel.toggleStrongEarthquakeEnabled(it) },
            enabled = settings.alertEnabled
        )

        SettingsToggleRow(
            title = str.feltQuakeTitle,
            subtitle = str.feltQuakeSubtitle,
            checked = settings.feltEarthquakeEnabled,
            onCheckedChange = { viewModel.toggleFeltEarthquakeEnabled(it) },
            enabled = settings.alertEnabled
        )

        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Text(
            text = str.distanceModeTitle,
            style = AppTypography.bodyMedium,
            color = EarthquakeTheme.colors.textPrimary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        SegmentedPicker(
            options = listOf(
                DistanceMode.AUTO to str.distanceModeAuto,
                DistanceMode.CUSTOM to str.distanceModeCustom
            ),
            selectedOption = settings.distanceMode,
            onOptionSelected = { viewModel.setDistanceMode(it) }
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))
        AppDivider()
        Spacer(modifier = Modifier.height(AppSpacing.md))

        // Alarm & Notification Behavior
        SettingsSectionHeader(title = str.notificationSection)
        SettingsToggleRow(
            title = str.soundTitle,
            subtitle = str.soundSubtitle,
            checked = settings.soundEnabled,
            onCheckedChange = { viewModel.toggleSoundEnabled(it) },
            enabled = settings.alertEnabled
        )

        SettingsToggleRow(
            title = str.vibrationTitle,
            subtitle = str.vibrationSubtitle,
            checked = settings.vibrationEnabled,
            onCheckedChange = { viewModel.toggleVibrationEnabled(it) },
            enabled = settings.alertEnabled
        )

        SettingsToggleRow(
            title = str.fullScreenTitle,
            subtitle = str.fullScreenSubtitle,
            checked = settings.fullScreenAlertEnabled,
            onCheckedChange = { viewModel.toggleFullScreenAlertEnabled(it) },
            enabled = settings.alertEnabled
        )

        Spacer(modifier = Modifier.height(AppSpacing.sm))
        SecondaryButton(
            text = str.testAlarmButton,
            onClick = onPreviewAlarm,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))
        AppDivider()
        Spacer(modifier = Modifier.height(AppSpacing.md))

        // About & Attributions
        SettingsSectionHeader(title = str.aboutSection)
        SettingsInfoRow(title = str.aboutDataSource, value = "BMKG Indonesia")
        SettingsInfoRow(title = str.aboutAppVersion, value = "1.0.0")
        SettingsInfoRow(title = str.aboutArchitecture, value = "Offline-first Local Evaluator")

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
