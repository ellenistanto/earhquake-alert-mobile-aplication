package com.earthquakealert.app

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.navigation.BottomNavDestination
import com.earthquakealert.app.ui.theme.AppDimensions
import com.earthquakealert.app.ui.theme.AppRadius
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EmergencyTypography
import com.earthquakealert.app.ui.theme.LightEmergency
import com.earthquakealert.app.ui.theme.DarkEmergency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase2DesignSystemTest {

    @Test
    fun testSpacingTokens() {
        assertEquals(4.dp, AppSpacing.micro)
        assertEquals(8.dp, AppSpacing.xs)
        assertEquals(12.dp, AppSpacing.sm)
        assertEquals(16.dp, AppSpacing.md)
        assertEquals(24.dp, AppSpacing.lg)
        assertEquals(32.dp, AppSpacing.xl)
        assertEquals(48.dp, AppSpacing.xxl)
        assertEquals(64.dp, AppSpacing.emergency)
    }

    @Test
    fun testDimensionsTokens() {
        assertEquals(48.dp, AppDimensions.minTouchTarget)
        assertEquals(50.dp, AppDimensions.buttonHeight)
        assertEquals(58.dp, AppDimensions.bottomNavHeight)
        assertEquals(8.dp, AppDimensions.statusDotSize)
    }

    @Test
    fun testCornerRadiiTokens() {
        assertEquals(8.dp, AppRadius.smallControl)
        assertEquals(10.dp, AppRadius.button)
        assertEquals(12.dp, AppRadius.card)
        assertEquals(16.dp, AppRadius.largeContainer)
    }

    @Test
    fun testEmergencyTypography() {
        assertEquals(56.sp, EmergencyTypography.magnitude.fontSize)
        assertEquals(26.sp, EmergencyTypography.alertTitle.fontSize)
        assertEquals(20.sp, EmergencyTypography.distance.fontSize)
    }

    @Test
    fun testBottomNavDestinations() {
        assertEquals(3, BottomNavDestination.items.size)
        assertEquals("Home", BottomNavDestination.Home.title)
        assertEquals("History", BottomNavDestination.History.title)
        assertEquals("Settings", BottomNavDestination.Settings.title)
    }
}
