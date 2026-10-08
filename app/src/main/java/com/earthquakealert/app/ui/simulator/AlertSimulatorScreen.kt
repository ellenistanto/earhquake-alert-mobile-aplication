package com.earthquakealert.app.ui.simulator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.domain.model.AlertSettings
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.model.UserLocation
import com.earthquakealert.app.evaluator.DefaultAlertEvaluator
import com.earthquakealert.app.notification.NotificationService
import com.earthquakealert.app.ui.components.AppDivider
import com.earthquakealert.app.ui.components.PrimaryButton
import com.earthquakealert.app.ui.components.SecondaryButton
import com.earthquakealert.app.ui.components.SettingsToggleRow
import com.earthquakealert.app.ui.components.SeverityBadge
import com.earthquakealert.app.ui.theme.AppRadius
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EarthquakeTheme
import kotlin.math.roundToInt

@Composable
fun AlertSimulatorScreen(
    notificationService: NotificationService,
    onNavigateBack: () -> Unit,
    onOpenActiveAlert: (Earthquake) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val evaluator = remember { DefaultAlertEvaluator() }

    var customMagnitude by remember { mutableDoubleStateOf(5.8) }
    var customDistanceKm by remember { mutableDoubleStateOf(45.0) }
    var tsunamiPotential by remember { mutableStateOf(false) }
    var lastSimulatedResult by remember { mutableStateOf<String?>(null) }
    var lastSeverity by remember { mutableStateOf<AlertSeverity?>(null) }

    fun triggerSimulation(
        magnitude: Double,
        distanceKm: Double,
        tsunami: Boolean,
        regionName: String
    ) {
        // Construct simulated quake
        val simulatedQuake = Earthquake(
            id = "sim-${System.currentTimeMillis()}",
            timestampMillis = System.currentTimeMillis(),
            latitude = -7.7956,
            longitude = 110.3695 + (distanceKm / 111.0),
            magnitude = magnitude,
            depthKm = 10.0,
            region = regionName,
            tsunamiPotential = tsunami,
            mmi = if (magnitude >= 5.0) "V MMI" else "III MMI"
        )

        val simulatedUserLocation = UserLocation(
            latitude = -7.7956,
            longitude = 110.3695
        )

        val assessment = evaluator.evaluate(
            earthquake = simulatedQuake,
            userLocation = simulatedUserLocation,
            settings = AlertSettings()
        )

        lastSeverity = assessment.severity
        lastSimulatedResult = "Evaluation: ${assessment.severity} | Should Alert: ${assessment.shouldAlert}\nReason: ${assessment.reason}"

        if (assessment.shouldAlert) {
            notificationService.sendEarthquakeAlert(simulatedQuake, assessment)
            if (assessment.severity == AlertSeverity.HIGH) {
                onOpenActiveAlert(simulatedQuake)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EarthquakeTheme.colors.background)
            .verticalScroll(scrollState)
            .padding(AppSpacing.md)
    ) {
        SecondaryButton(
            text = EarthquakeTheme.strings.back,
            onClick = onNavigateBack,
            modifier = Modifier.padding(bottom = AppSpacing.md)
        )

        Text(
            text = "Emergency Simulator",
            style = AppTypography.headlineMedium,
            color = EarthquakeTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = "Simulate earthquake alerts of varying intensity to verify notifications, vibration, sound, and screen wake safely.",
            style = AppTypography.bodyMedium,
            color = EarthquakeTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(AppSpacing.lg))
        AppDivider()
        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Quick Presets
        Text(
            text = "Quick Presets",
            style = AppTypography.labelLarge,
            color = EarthquakeTheme.colors.textSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))

        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
            SecondaryButton(
                text = "Preset 1: Major Nearby (M 6.2, 25 km)",
                onClick = {
                    triggerSimulation(6.2, 25.0, false, "Yogyakarta Fault Region")
                },
                modifier = Modifier.fillMaxWidth()
            )

            SecondaryButton(
                text = "Preset 2: Moderate Distant (M 5.1, 80 km)",
                onClick = {
                    triggerSimulation(5.1, 80.0, false, "Southern Java Coast")
                },
                modifier = Modifier.fillMaxWidth()
            )

            SecondaryButton(
                text = "Preset 3: Tsunami Advisory (M 7.2, 90 km)",
                onClick = {
                    triggerSimulation(7.2, 90.0, true, "South Coast of Sumatra")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))
        AppDivider()
        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Custom Simulation
        Text(
            text = "Custom Simulation Parameters",
            style = AppTypography.labelLarge,
            color = EarthquakeTheme.colors.textSecondary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))

        Text(
            text = "Magnitude: M %.1f".format(customMagnitude),
            style = AppTypography.bodyLarge,
            color = EarthquakeTheme.colors.textPrimary,
            fontWeight = FontWeight.Medium
        )
        Slider(
            value = customMagnitude.toFloat(),
            onValueChange = { customMagnitude = ((it * 10).roundToInt() / 10.0) },
            valueRange = 3.0f..8.5f,
            colors = SliderDefaults.colors(
                thumbColor = EarthquakeTheme.colors.textPrimary,
                activeTrackColor = EarthquakeTheme.colors.textPrimary
            )
        )

        Spacer(modifier = Modifier.height(AppSpacing.sm))

        Text(
            text = "Distance: %.0f km".format(customDistanceKm),
            style = AppTypography.bodyLarge,
            color = EarthquakeTheme.colors.textPrimary,
            fontWeight = FontWeight.Medium
        )
        Slider(
            value = customDistanceKm.toFloat(),
            onValueChange = { customDistanceKm = it.roundToInt().toDouble() },
            valueRange = 5.0f..400.0f,
            colors = SliderDefaults.colors(
                thumbColor = EarthquakeTheme.colors.textPrimary,
                activeTrackColor = EarthquakeTheme.colors.textPrimary
            )
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))
        SettingsToggleRow(
            title = "Tsunami Potential Advisory",
            checked = tsunamiPotential,
            onCheckedChange = { tsunamiPotential = it }
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))
        PrimaryButton(
            text = "Trigger Custom Simulation",
            onClick = {
                triggerSimulation(customMagnitude, customDistanceKm, tsunamiPotential, "Simulated Epicenter")
            }
        )


        // Simulation Feedback Result Box
        if (lastSimulatedResult != null) {
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        EarthquakeTheme.colors.border,
                        RoundedCornerShape(AppRadius.card)
                    )
                    .background(
                        EarthquakeTheme.colors.surface,
                        RoundedCornerShape(AppRadius.card)
                    )
                    .padding(AppSpacing.md)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        lastSeverity?.let { SeverityBadge(severity = it) }
                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                        Text(
                            text = "Simulation Result",
                            style = AppTypography.labelLarge,
                            color = EarthquakeTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    Text(
                        text = lastSimulatedResult ?: "",
                        style = AppTypography.bodySmall,
                        color = EarthquakeTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.xl))
    }
}
