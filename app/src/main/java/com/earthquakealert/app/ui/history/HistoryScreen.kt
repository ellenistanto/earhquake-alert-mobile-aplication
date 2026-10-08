package com.earthquakealert.app.ui.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.earthquakealert.app.ui.components.EmptyState
import com.earthquakealert.app.ui.components.ErrorState
import com.earthquakealert.app.ui.components.SegmentedPicker
import com.earthquakealert.app.ui.theme.AppRadius
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EarthquakeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EarthquakeTheme.colors.background)
            .padding(horizontal = AppSpacing.md)
    ) {
        item {
            Spacer(modifier = Modifier.height(AppSpacing.md))
            Text(
                text = EarthquakeTheme.strings.historyTitle,
                style = AppTypography.headlineMedium,
                color = EarthquakeTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = EarthquakeTheme.strings.dataSourceBmkg,
                style = AppTypography.bodyMedium,
                color = EarthquakeTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Filter Selector
            FilterSelector(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )
            Spacer(modifier = Modifier.height(AppSpacing.md))
        }

        if (uiState.errorMessage != null && uiState.earthquakes.isEmpty()) {
            item {
                ErrorState(
                    message = uiState.errorMessage ?: "Failed to load history",
                    onRetry = { viewModel.refresh() }
                )
            }
        } else if (uiState.earthquakes.isEmpty() && !uiState.isLoading) {
            item {
                EmptyState(
                    title = EarthquakeTheme.strings.noHistoryData,
                    description = EarthquakeTheme.strings.noRecentEarthquakesDesc
                )
            }
        } else {
            items(uiState.earthquakes, key = { it.id }) { item ->
                HistoryItemCard(
                    item = item,
                    onClick = { onNavigateToDetail(item.id) }
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
            }
        }

        item {
            Spacer(modifier = Modifier.height(AppSpacing.xl))
        }
    }
}

@Composable
private fun FilterSelector(
    selectedFilter: HistoryFilter,
    onFilterSelected: (HistoryFilter) -> Unit
) {
    val str = EarthquakeTheme.strings
    SegmentedPicker(
        options = listOf(
            HistoryFilter.ALL to str.filterAll,
            HistoryFilter.FELT to str.filterFelt,
            HistoryFilter.STRONG to str.filterMagnitude5
        ),
        selectedOption = selectedFilter,
        onOptionSelected = onFilterSelected
    )
}

@Composable
private fun HistoryItemCard(
    item: HistoryItemUiModel,
    onClick: () -> Unit
) {
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
            .clickable(onClick = onClick)
            .padding(AppSpacing.md)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "M %.1f".format(item.magnitude),
                    style = AppTypography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (item.magnitude >= 5.0) EarthquakeTheme.colors.emergency else EarthquakeTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                if (item.distanceKm != null) {
                    Text(
                        text = "%.0f km away".format(item.distanceKm),
                        style = AppTypography.bodySmall,
                        color = EarthquakeTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.micro))
            Text(
                text = item.region,
                style = AppTypography.titleMedium,
                color = EarthquakeTheme.colors.textPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(AppSpacing.micro))
            val dateStr = formatDateTime(item.timestampMillis)
            val depthStr = item.depthKm?.let { " | %.0f km".format(it) } ?: ""
            Text(
                text = "$dateStr$depthStr",
                style = AppTypography.bodySmall,
                color = EarthquakeTheme.colors.muted
            )

            if (!item.mmi.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = "Felt: ${item.mmi}",
                    style = AppTypography.labelMedium,
                    color = EarthquakeTheme.colors.warning,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (item.potentialTsunami == true) {
                Spacer(modifier = Modifier.height(AppSpacing.micro))
                Text(
                    text = "TSUNAMI ADVISORY",
                    style = AppTypography.labelMedium,
                    color = EarthquakeTheme.colors.emergency,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    if (timestamp <= 0) return "Unknown"
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
