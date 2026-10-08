package com.earthquakealert.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.earthquakealert.app.EarthquakeApplication
import com.earthquakealert.app.di.DefaultAppContainer
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.notification.AlarmPlayer
import com.earthquakealert.app.ui.AppViewModelFactory
import com.earthquakealert.app.ui.alert.ActiveAlertScreen
import com.earthquakealert.app.ui.detail.EarthquakeDetailScreen
import com.earthquakealert.app.ui.detail.EarthquakeDetailViewModel
import com.earthquakealert.app.ui.history.HistoryScreen
import com.earthquakealert.app.ui.history.HistoryViewModel
import com.earthquakealert.app.ui.home.HomeScreen
import com.earthquakealert.app.ui.home.HomeViewModel
import com.earthquakealert.app.ui.onboarding.OnboardingScreen
import com.earthquakealert.app.ui.settings.SettingsScreen
import com.earthquakealert.app.ui.settings.SettingsViewModel
import com.earthquakealert.app.ui.theme.AppDimensions
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EarthquakeTheme

@Composable
fun EarthquakeApp(
    initialEarthquakeId: String? = null,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    var activeSimulatedQuake by remember { mutableStateOf<Earthquake?>(null) }

    val factory = remember {
        val app = context.applicationContext as? EarthquakeApplication
        val container = app?.container ?: DefaultAppContainer(context.applicationContext)
        AppViewModelFactory(container)
    }

    LaunchedEffect(initialEarthquakeId) {
        if (!initialEarthquakeId.isNullOrBlank()) {
            val app = context.applicationContext as? EarthquakeApplication
            val container = app?.container ?: DefaultAppContainer(context.applicationContext)
            AlarmPlayer.stop()
            container.notificationService.cancelAlert(initialEarthquakeId)
            container.notificationService.cancelAllAlerts()
            navController.navigate(Screen.Detail.createRoute(initialEarthquakeId))
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom navigation on detail, onboarding, and active alert screens
    val isBottomNavVisible = when {
        currentRoute == Screen.Onboarding.route -> false
        currentRoute == Screen.ActiveAlert.route -> false
        currentRoute?.startsWith("detail") == true -> false
        else -> true
    }

    Scaffold(
        containerColor = EarthquakeTheme.colors.background,
        bottomBar = {
            if (isBottomNavVisible) {
                AppBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(180)) },
            exitTransition = { fadeOut(animationSpec = tween(180)) },
            popEnterTransition = { fadeIn(animationSpec = tween(180)) },
            popExitTransition = { fadeOut(animationSpec = tween(180)) }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(factory = factory)
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    },
                    onNavigateToAlert = {
                        navController.navigate(Screen.Simulator.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            composable(Screen.History.route) {
                val historyViewModel: HistoryViewModel = viewModel(factory = factory)
                HistoryScreen(
                    viewModel = historyViewModel,
                    onNavigateToDetail = { id ->
                        navController.navigate(Screen.Detail.createRoute(id))
                    }
                )
            }

            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onPreviewAlarm = {
                        navController.navigate(Screen.Simulator.route)
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("earthquakeId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("earthquakeId") ?: "unknown"
                val detailViewModel: EarthquakeDetailViewModel = viewModel(factory = factory)
                EarthquakeDetailScreen(
                    earthquakeId = id,
                    viewModel = detailViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ActiveAlert.route) {
                val container = (context.applicationContext as? EarthquakeApplication)?.container
                    ?: DefaultAppContainer(context.applicationContext)
                val quake = activeSimulatedQuake
                ActiveAlertScreen(
                    earthquakeId = quake?.id ?: "sample-alert-1",
                    magnitude = quake?.magnitude ?: 5.4,
                    distanceKm = 25.0,
                    region = quake?.region ?: "Southern Java",
                    depthKm = quake?.depthKm ?: 10.0,
                    onDismiss = {
                        AlarmPlayer.stop()
                        if (quake != null) {
                            container.notificationService.cancelAlert(quake.id)
                        }
                        container.notificationService.cancelAllAlerts()
                        navController.popBackStack()
                    },
                    onViewDetails = { id ->
                        AlarmPlayer.stop()
                        if (quake != null) {
                            container.notificationService.cancelAlert(quake.id)
                        }
                        container.notificationService.cancelAllAlerts()
                        navController.navigate(Screen.Detail.createRoute(id)) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            composable(Screen.Simulator.route) {
                val container = (context.applicationContext as? EarthquakeApplication)?.container
                    ?: DefaultAppContainer(context.applicationContext)
                com.earthquakealert.app.ui.simulator.AlertSimulatorScreen(
                    notificationService = container.notificationService,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenActiveAlert = { simulatedQuake ->
                        activeSimulatedQuake = simulatedQuake
                        navController.navigate(Screen.ActiveAlert.route)
                    }
                )
            }
        }
    }
}


@Composable
private fun AppBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = BottomNavDestination.items

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = EarthquakeTheme.colors.border
            )
            .background(EarthquakeTheme.colors.surface)
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { dest ->
                val isSelected = currentRoute == dest.route

                val str = EarthquakeTheme.strings
                val label = when (dest) {
                    BottomNavDestination.Home -> str.navHome
                    BottomNavDestination.History -> str.navHistory
                    BottomNavDestination.Settings -> str.navSettings
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(dest.route) }
                        .padding(vertical = 4.dp)
                        .semantics {
                            selected = isSelected
                            role = Role.Tab
                        }
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = EarthquakeTheme.colors.brandGreen,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = label,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = label,
                                tint = EarthquakeTheme.colors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        style = AppTypography.labelSmall,
                        color = if (isSelected) EarthquakeTheme.colors.brandGreen else EarthquakeTheme.colors.textSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

