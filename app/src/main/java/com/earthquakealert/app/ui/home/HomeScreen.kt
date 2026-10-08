package com.earthquakealert.app.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.AltRoute
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.ui.components.EarthquakeMapView
import com.earthquakealert.app.ui.components.EmptyState
import com.earthquakealert.app.ui.components.ErrorState
import com.earthquakealert.app.ui.components.ProtectionStatusIndicator
import com.earthquakealert.app.ui.components.SecondaryButton
import com.earthquakealert.app.ui.components.SeverityBadge
import com.earthquakealert.app.ui.theme.AppRadius
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EarthquakeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAlert: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val str = EarthquakeTheme.strings

    val hasFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarseLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasLocation = hasFineLocation || hasCoarseLocation

    val hasNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.refresh()
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = buildList {
            if (!hasLocation) {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (!hasNotification && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.refresh() },
        modifier = modifier
            .fillMaxSize()
            .background(EarthquakeTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // BMKG Curved Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = EarthquakeTheme.colors.headerBackground,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(horizontal = AppSpacing.md, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BMKG",
                                style = AppTypography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1976D2)
                            )
                        }
                        Spacer(modifier = Modifier.width(AppSpacing.xs))
                        Text(
                            text = str.navHome,
                            style = AppTypography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = str.navSettings,
                            tint = Color.White
                        )
                    }
                }
            }

            // Main Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
            ) {
                // Section Title: "Gempabumi Terkini" / "Latest Earthquake"
                Text(
                    text = str.latestEarthquakeSection,
                    style = AppTypography.titleLarge,
                    color = EarthquakeTheme.colors.titleMaroon,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(AppSpacing.sm))

                // Active Alert Notification if alert is currently active
                val assessment = uiState.latestAssessment
                if (assessment != null && assessment.shouldAlert) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                EarthquakeTheme.colors.emergency.copy(alpha = 0.12f),
                                RoundedCornerShape(AppRadius.card)
                            )
                            .border(1.dp, EarthquakeTheme.colors.emergency, RoundedCornerShape(AppRadius.card))
                            .padding(AppSpacing.md)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SeverityBadge(severity = assessment.severity)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = str.activeAlertTitle,
                                    style = AppTypography.labelLarge,
                                    color = EarthquakeTheme.colors.emergency,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = assessment.reason,
                                style = AppTypography.bodyMedium,
                                color = EarthquakeTheme.colors.textPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                }

                // Error message if any
                if (uiState.errorMessage != null && uiState.latestEarthquake == null) {
                    ErrorState(
                        message = uiState.errorMessage ?: "Failed to load data",
                        onRetry = { viewModel.refresh() }
                    )
                } else {
                    val latest = uiState.latestEarthquake
                    if (latest != null) {
                        LatestEarthquakeCard(
                            earthquake = latest,
                            distanceKm = uiState.latestDistanceKm,
                            onClick = { onNavigateToDetail(latest.id) },
                            onShare = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        str.shareFormat(
                                            latest.magnitude,
                                            latest.region,
                                            formatDateTime(latest.timestampMillis),
                                            latest.depthKm ?: 10.0,
                                            latest.latitude,
                                            latest.longitude
                                        )
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, str.shareTitle))
                            }
                        )
                    } else if (!uiState.isLoading) {
                        EmptyState(
                            title = str.noRecentEarthquakes,
                            description = str.noRecentEarthquakesDesc
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.md))

                    // Location & Protection Status Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EarthquakeTheme.colors.border, RoundedCornerShape(AppRadius.card))
                            .background(EarthquakeTheme.colors.surface, RoundedCornerShape(AppRadius.card))
                            .padding(AppSpacing.md)
                    ) {
                        Column {
                            ProtectionStatusIndicator(status = uiState.protectionStatus)
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = str.monitoringLocation,
                                style = AppTypography.labelMedium,
                                color = EarthquakeTheme.colors.textSecondary
                            )
                            val locationText = uiState.userLocation?.let {
                                "%.3f°, %.3f°".format(it.latitude, it.longitude)
                            } ?: str.locationUnavailable
                            Text(
                                text = locationText,
                                style = AppTypography.bodyMedium,
                                color = EarthquakeTheme.colors.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (!hasLocation) {
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                Text(
                                    text = str.enableGpsPrompt,
                                    style = AppTypography.bodySmall,
                                    color = EarthquakeTheme.colors.warning
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.xs))
                                SecondaryButton(
                                    text = str.enableLocationPermission,
                                    onClick = {
                                        val permissions = arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                        permissionLauncher.launch(permissions)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.lg))

                    // Recent Earthquakes Section
                    if (uiState.recentEarthquakes.isNotEmpty()) {
                        Text(
                            text = str.otherEarthquakes,
                            style = AppTypography.titleMedium,
                            color = EarthquakeTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.sm))

                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                            uiState.recentEarthquakes.forEach { item ->
                                RecentEarthquakeRow(
                                    item = item,
                                    onClick = { onNavigateToDetail(item.earthquake.id) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                Text(
                    text = str.dataSourceBmkg,
                    style = AppTypography.bodySmall,
                    color = EarthquakeTheme.colors.muted
                )
                Spacer(modifier = Modifier.height(AppSpacing.md))
            }
        }
    }
}

@Composable
private fun LatestEarthquakeCard(
    earthquake: Earthquake,
    distanceKm: Double?,
    onClick: () -> Unit,
    onShare: () -> Unit
) {
    val str = EarthquakeTheme.strings

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = EarthquakeTheme.colors.border,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = EarthquakeTheme.colors.surface,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column {
            // Map Header Container - clean, unobstructed
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                EarthquakeMapView(
                    latitude = earthquake.latitude,
                    longitude = earthquake.longitude,
                    magnitude = earthquake.magnitude,
                    isDark = EarthquakeTheme.colors.isDark,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Metrics 3 Columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Magnitudo
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ShowChart,
                            contentDescription = str.magnitude,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.1f".format(earthquake.magnitude),
                            style = AppTypography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EarthquakeTheme.colors.textPrimary
                        )
                    }
                    Text(
                        text = str.magnitude,
                        style = AppTypography.bodySmall,
                        color = EarthquakeTheme.colors.textSecondary
                    )
                }

                // Kedalaman
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.GraphicEq,
                            contentDescription = str.depth,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.0f Km".format(earthquake.depthKm ?: 10.0),
                            style = AppTypography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EarthquakeTheme.colors.textPrimary
                        )
                    }
                    Text(
                        text = str.depth,
                        style = AppTypography.bodySmall,
                        color = EarthquakeTheme.colors.textSecondary
                    )
                }

                // Koordinat
                val latStr = if (earthquake.latitude >= 0) "%.2f LU".format(earthquake.latitude) else "%.2f LS".format(-earthquake.latitude)
                val lonStr = if (earthquake.longitude >= 0) "%.2f BT".format(earthquake.longitude) else "%.2f BB".format(-earthquake.longitude)

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = str.coordinates,
                            tint = Color(0xFFF57C00),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = latStr,
                                style = AppTypography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EarthquakeTheme.colors.textPrimary
                            )
                            Text(
                                text = lonStr,
                                style = AppTypography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EarthquakeTheme.colors.textPrimary
                            )
                        }
                    }
                }
            }

            // Status Pill
            val statusText = when {
                earthquake.mmi != null -> str.feltEarthquake
                earthquake.tsunamiPotential == true -> str.tsunamiWarning
                earthquake.magnitude >= 5.0 -> str.significantEarthquake
                else -> str.recentEarthquake
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .border(1.dp, EarthquakeTheme.colors.border, RoundedCornerShape(20.dp))
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = statusText,
                        style = AppTypography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (earthquake.tsunamiPotential == true) EarthquakeTheme.colors.emergency else EarthquakeTheme.colors.textPrimary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = EarthquakeTheme.colors.border
            )

            // Info Details with Amber Icons
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Waktu
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = "Time",
                        tint = Color(0xFFF57C00),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = str.time,
                            style = AppTypography.bodySmall,
                            color = EarthquakeTheme.colors.textSecondary
                        )
                        Text(
                            text = formatFullDateTime(earthquake.timestampMillis),
                            style = AppTypography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = EarthquakeTheme.colors.textPrimary
                        )
                    }
                }

                // Lokasi Gempa
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = "Location",
                        tint = Color(0xFFF57C00),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = str.earthquakeLocation,
                            style = AppTypography.bodySmall,
                            color = EarthquakeTheme.colors.textSecondary
                        )
                        Text(
                            text = earthquake.region,
                            style = AppTypography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = EarthquakeTheme.colors.textPrimary
                        )
                    }
                }

                // Wilayah Dirasakan (Skala MMI)
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.Adjust,
                        contentDescription = "MMI",
                        tint = Color(0xFFF57C00),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = str.feltScale,
                            style = AppTypography.bodySmall,
                            color = EarthquakeTheme.colors.textSecondary
                        )
                        Text(
                            text = earthquake.mmi ?: str.noMmiReport,
                            style = AppTypography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = EarthquakeTheme.colors.textPrimary
                        )
                    }
                }

                // Jarak
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.AltRoute,
                        contentDescription = "Distance",
                        tint = Color(0xFFF57C00),
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = str.distance,
                            style = AppTypography.bodySmall,
                            color = EarthquakeTheme.colors.textSecondary
                        )
                        val distString = distanceKm?.let {
                            str.distanceFromYou(it)
                        } ?: str.calculatingDistance
                        Text(
                            text = distString,
                            style = AppTypography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = EarthquakeTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action row: Bagikan & Lihat Rincian
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = str.share,
                        tint = EarthquakeTheme.colors.info,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = str.share,
                        style = AppTypography.labelMedium,
                        color = EarthquakeTheme.colors.info,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(onClick = onClick) {
                    Text(
                        text = str.viewFullDetails,
                        style = AppTypography.labelMedium,
                        color = EarthquakeTheme.colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentEarthquakeRow(
    item: EarthquakeWithDistance,
    onClick: () -> Unit
) {
    val quake = item.earthquake
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
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "M %.1f".format(quake.magnitude),
                style = AppTypography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (quake.magnitude >= 5.0) EarthquakeTheme.colors.emergency else EarthquakeTheme.colors.textPrimary,
                modifier = Modifier.padding(end = AppSpacing.md)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = quake.region,
                    style = AppTypography.bodyMedium,
                    color = EarthquakeTheme.colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
                val timeStr = formatDateTime(quake.timestampMillis)
                val distStr = item.distanceKm?.let { " | %.0f km".format(it) } ?: ""
                Text(
                    text = "$timeStr$distStr",
                    style = AppTypography.bodySmall,
                    color = EarthquakeTheme.colors.muted
                )
            }
        }
    }
}

private fun formatDateTime(timestamp: Long): String {
    if (timestamp <= 0) return "Recent"
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatFullDateTime(timestamp: Long): String {
    if (timestamp <= 0) return "Recent"
    val sdf = SimpleDateFormat("d MMM yyyy, HH:mm:ss 'WIB'", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
