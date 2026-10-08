package com.earthquakealert.app.domain.model

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float? = null,
    val updatedAtMillis: Long = System.currentTimeMillis(),
    val areaName: String? = null
)
