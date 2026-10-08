package com.earthquakealert.app.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.earthquakealert.app.ui.components.PrimaryButton
import com.earthquakealert.app.ui.components.SecondaryButton
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EarthquakeTheme

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(0) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        step = 2
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        step = 3
    }

    val strings = EarthquakeTheme.strings

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EarthquakeTheme.colors.background)
            .padding(AppSpacing.lg),
        verticalArrangement = Arrangement.Center
    ) {
        when (step) {
            0 -> {
                Text(
                    text = strings.onboardingWelcomeTitle,
                    style = AppTypography.displayMedium,
                    color = EarthquakeTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(AppSpacing.md))
                Text(
                    text = strings.onboardingWelcomeDesc,
                    style = AppTypography.bodyLarge,
                    color = EarthquakeTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xl))
                PrimaryButton(
                    text = strings.onboardingGetStarted,
                    onClick = { step = 1 }
                )
            }

            1 -> {
                Text(
                    text = strings.onboardingLocationTitle,
                    style = AppTypography.headlineLarge,
                    color = EarthquakeTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(AppSpacing.md))
                Text(
                    text = strings.onboardingLocationDesc,
                    style = AppTypography.bodyLarge,
                    color = EarthquakeTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xl))
                PrimaryButton(
                    text = strings.onboardingAllowLocation,
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                SecondaryButton(
                    text = strings.onboardingSkip,
                    onClick = { step = 2 }
                )
            }

            2 -> {
                Text(
                    text = strings.onboardingNotificationTitle,
                    style = AppTypography.headlineLarge,
                    color = EarthquakeTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(AppSpacing.md))
                Text(
                    text = strings.onboardingNotificationDesc,
                    style = AppTypography.bodyLarge,
                    color = EarthquakeTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xl))
                PrimaryButton(
                    text = strings.onboardingEnableNotifications,
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            step = 3
                        }
                    }
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                SecondaryButton(
                    text = strings.onboardingSkip,
                    onClick = { step = 3 }
                )
            }

            3 -> {
                Text(
                    text = strings.onboardingReadyTitle,
                    style = AppTypography.headlineLarge,
                    color = EarthquakeTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(AppSpacing.md))
                Text(
                    text = strings.onboardingReadyDesc,
                    style = AppTypography.bodyLarge,
                    color = EarthquakeTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xl))
                PrimaryButton(
                    text = strings.onboardingContinue,
                    onClick = onFinish
                )
            }
        }
    }
}
