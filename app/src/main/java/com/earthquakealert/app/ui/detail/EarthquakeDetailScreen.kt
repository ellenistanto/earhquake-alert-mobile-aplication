package com.earthquakealert.app.ui.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.earthquakealert.app.ui.components.AppDivider
import com.earthquakealert.app.ui.components.ErrorState
import com.earthquakealert.app.ui.components.InfoRow
import com.earthquakealert.app.ui.components.PrimaryButton
import com.earthquakealert.app.ui.components.SecondaryButton
import com.earthquakealert.app.ui.theme.AppRadius
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EarthquakeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EarthquakeDetailScreen(
    earthquakeId: String,
    viewModel: EarthquakeDetailViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(earthquakeId) {
        viewModel.loadDetail(earthquakeId)
    }

    val strings = EarthquakeTheme.strings

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EarthquakeTheme.colors.background)
            .verticalScroll(scrollState)
            .padding(AppSpacing.md)
    ) {
        SecondaryButton(
            text = strings.back,
            onClick = onNavigateBack,
            modifier = Modifier.padding(bottom = AppSpacing.md)
        )

        if (uiState.errorMessage != null && uiState.earthquake == null) {
            ErrorState(
                message = uiState.errorMessage ?: strings.failedToLoadDetails,
                onRetry = { viewModel.loadDetail(earthquakeId) }
            )
        } else {
            val quake = uiState.earthquake
            if (quake != null) {
                Text(
                    text = strings.detailsTitle,
                    style = AppTypography.headlineSmall,
                    color = EarthquakeTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(AppSpacing.micro))
                Text(
                    text = "M %.1f".format(quake.magnitude),
                    style = AppTypography.displayMedium,
                    color = if (quake.magnitude >= 5.0) EarthquakeTheme.colors.emergency else EarthquakeTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = quake.region,
                    style = AppTypography.titleLarge,
                    color = EarthquakeTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(AppSpacing.lg))
                AppDivider()
                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Parameters Table
                Text(
                    text = strings.paramsTable,
                    style = AppTypography.labelLarge,
                    color = EarthquakeTheme.colors.textSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))

                InfoRow(
                    label = strings.time.removeSuffix(" :").removeSuffix(":").trim(),
                    value = formatDateTime(quake.timestampMillis, strings.unknownValue)
                )
                InfoRow(
                    label = strings.coordinates,
                    value = "%.3f°, %.3f°".format(quake.latitude, quake.longitude)
                )
                InfoRow(
                    label = strings.depth,
                    value = quake.depthKm?.let { "%.0f km".format(it) } ?: strings.unknownValue
                )
                InfoRow(
                    label = strings.distanceFromYouLabel,
                    value = uiState.distanceKm?.let { "%.0f km".format(it) } ?: strings.locationUnavailableDetail
                )
                InfoRow(
                    label = strings.tsunamiThreat,
                    value = if (quake.tsunamiPotential == true) strings.tsunamiPotentialYes else strings.tsunamiPotentialNo
                )
                if (!quake.mmi.isNullOrBlank()) {
                    InfoRow(
                        label = strings.feltReports,
                        value = quake.mmi
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))
                AppDivider()
                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Safety Guidance Section
                Text(
                    text = strings.safetyGuidelines,
                    style = AppTypography.labelLarge,
                    color = EarthquakeTheme.colors.textSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = EarthquakeTheme.colors.border,
                            shape = RoundedCornerShape(AppRadius.card)
                        )
                        .background(
                            color = EarthquakeTheme.colors.surface,
                            shape = RoundedCornerShape(AppRadius.card)
                        )
                        .padding(AppSpacing.md)
                ) {
                    Text(
                        text = if (uiState.safetyRecommendation.isNotBlank()) uiState.safetyRecommendation else strings.guidelinesContent,
                        style = AppTypography.bodyMedium,
                        color = EarthquakeTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                // Share Button
                PrimaryButton(
                    text = strings.shareTitle,
                    onClick = {
                        val shareText = strings.shareFormat(
                            quake.magnitude,
                            quake.region,
                            formatDateTime(quake.timestampMillis, strings.unknownValue),
                            quake.depthKm ?: 0.0,
                            quake.latitude,
                            quake.longitude
                        )
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, strings.shareVia))
                    }
                )

                Spacer(modifier = Modifier.height(AppSpacing.md))
                Text(
                    text = strings.officialBmkgData,
                    style = AppTypography.bodySmall,
                    color = EarthquakeTheme.colors.muted
                )
            }
        }
    }
}

private fun formatDateTime(timestamp: Long, fallback: String): String {
    if (timestamp <= 0) return fallback
    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss 'WIB'", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
