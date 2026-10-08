package com.earthquakealert.app.domain.model

data class Earthquake(
    val id: String,
    val timestampMillis: Long,
    val latitude: Double,
    val longitude: Double,
    val magnitude: Double,
    val depthKm: Double?,
    val region: String,
    val tsunamiPotential: Boolean?,
    val mmi: String?,
    val source: String = "BMKG"
)
