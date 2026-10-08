package com.earthquakealert.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.earthquakealert.app.domain.model.Earthquake

@Entity(tableName = "earthquakes")
data class EarthquakeEntity(
    @PrimaryKey
    val id: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val magnitude: Double,
    val depthKm: Double?,
    val region: String,
    val tsunamiPotential: Boolean?,
    val mmi: String?,
    val source: String,
    val distanceKm: Double? = null,
    val alertTriggered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun EarthquakeEntity.toDomain(): Earthquake {
    return Earthquake(
        id = id,
        timestampMillis = timestamp,
        latitude = latitude,
        longitude = longitude,
        magnitude = magnitude,
        depthKm = depthKm,
        region = region,
        tsunamiPotential = tsunamiPotential,
        mmi = mmi,
        source = source
    )
}

fun Earthquake.toEntity(
    distanceKm: Double? = null,
    alertTriggered: Boolean = false
): EarthquakeEntity {
    return EarthquakeEntity(
        id = id,
        timestamp = timestampMillis,
        latitude = latitude,
        longitude = longitude,
        magnitude = magnitude,
        depthKm = depthKm,
        region = region,
        tsunamiPotential = tsunamiPotential,
        mmi = mmi,
        source = source,
        distanceKm = distanceKm,
        alertTriggered = alertTriggered
    )
}
