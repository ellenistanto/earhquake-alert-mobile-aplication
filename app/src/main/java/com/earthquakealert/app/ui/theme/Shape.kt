package com.earthquakealert.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object AppRadius {
    val smallControl = 8.dp
    val button = 10.dp
    val card = 12.dp
    val largeContainer = 16.dp
}

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(AppRadius.smallControl),
    small = RoundedCornerShape(AppRadius.smallControl),
    medium = RoundedCornerShape(AppRadius.button),
    large = RoundedCornerShape(AppRadius.card),
    extraLarge = RoundedCornerShape(AppRadius.largeContainer)
)
