package com.earthquakealert.app.ui.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthquakealert.app.ui.components.AppDivider
import com.earthquakealert.app.ui.components.PrimaryButton
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EarthquakeTheme
import com.earthquakealert.app.ui.theme.EmergencyTypography

@Composable
fun ActiveAlertScreen(
    onDismiss: () -> Unit,
    onViewDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    magnitude: Double = 5.4,
    distanceKm: Double = 32.0,
    region: String = "Southern Java",
    depthKm: Double = 10.0,
    earthquakeId: String = "sample-alert-1"
) {
    val strings = EarthquakeTheme.strings

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EarthquakeTheme.colors.background)
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = strings.alertWarning,
            style = AppTypography.labelLarge,
            color = EarthquakeTheme.colors.emergency,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        Text(
            text = strings.alertDetected,
            style = EmergencyTypography.alertTitle,
            color = EarthquakeTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        Text(
            text = "M %.1f".format(magnitude),
            style = EmergencyTypography.magnitude,
            color = EarthquakeTheme.colors.emergency,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppSpacing.md))

        Text(
            text = strings.alertAwayFormat(distanceKm),
            style = EmergencyTypography.distance,
            color = EarthquakeTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppSpacing.xs))

        Text(
            text = strings.alertRegionDepthFormat(region, depthKm),
            style = EmergencyTypography.supporting,
            color = EarthquakeTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))
        AppDivider(modifier = Modifier.padding(horizontal = AppSpacing.md))
        Spacer(modifier = Modifier.height(AppSpacing.xl))

        Text(
            text = strings.alertSafetyInstruction,
            style = AppTypography.bodyLarge,
            color = EarthquakeTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(AppSpacing.xxl))

        PrimaryButton(
            text = strings.alertViewDetails,
            onClick = { onViewDetails(earthquakeId) },
            isEmergency = true
        )

        Spacer(modifier = Modifier.height(AppSpacing.sm))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(
                text = strings.alertDismiss,
                style = AppTypography.labelLarge,
                color = EarthquakeTheme.colors.textSecondary
            )
        }
    }
}
