package com.earthquakealert.app.domain.repository

import com.earthquakealert.app.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface EarthquakeRepository {
    suspend fun getLatestEarthquakes(): List<Earthquake>
    fun observeHistory(): Flow<List<Earthquake>>
    suspend fun getEarthquakeById(id: String): Earthquake?

    suspend fun isEventProcessed(eventId: String): Boolean
    suspend fun markEventProcessed(eventId: String, alertTriggered: Boolean)
}
