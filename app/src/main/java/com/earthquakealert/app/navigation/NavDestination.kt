package com.earthquakealert.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object ActiveAlert : Screen("alert")
    data object History : Screen("history")
    data object Detail : Screen("detail/{earthquakeId}") {
        fun createRoute(earthquakeId: String): String = "detail/$earthquakeId"
    }
    data object Settings : Screen("settings")
    data object Simulator : Screen("simulator")
}

sealed class BottomNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavDestination(Screen.Home.route, "Home", Icons.Outlined.Home)
    data object History : BottomNavDestination(Screen.History.route, "History", Icons.Outlined.History)
    data object Settings : BottomNavDestination(Screen.Settings.route, "Settings", Icons.Outlined.Settings)

    companion object {
        val items = listOf(Home, History, Settings)
    }
}
