package com.earthquakealert.app.data.repository

import com.earthquakealert.app.data.local.dao.EarthquakeDao
import com.earthquakealert.app.data.local.dao.ProcessedEventDao
import com.earthquakealert.app.data.local.entity.ProcessedEventEntity
import com.earthquakealert.app.data.local.entity.toDomain
import com.earthquakealert.app.data.local.entity.toEntity
import com.earthquakealert.app.data.remote.service.EarthquakeRemoteDataSource
import com.earthquakealert.app.domain.model.Earthquake
import com.earthquakealert.app.domain.repository.EarthquakeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EarthquakeRepositoryImpl(
    private val remoteDataSource: EarthquakeRemoteDataSource,
    private val earthquakeDao: EarthquakeDao,
    private val processedEventDao: ProcessedEventDao
) : EarthquakeRepository {

    override suspend fun getLatestEarthquakes(): List<Earthquake> {
        val remoteList = mutableListOf<Earthquake>()

        try {
            val autoQuake = remoteDataSource.getLatestEarthquake()
            if (autoQuake != null) {
                remoteList.add(autoQuake)
            }
        } catch (_: Exception) {
            // Gracefully tolerate network failure
        }

        try {
            val feltQuakes = remoteDataSource.getFeltEarthquakes()
            remoteList.addAll(feltQuakes)
        } catch (_: Exception) {
            // Gracefully tolerate network failure
        }

        try {
            val recentQuakes = remoteDataSource.getRecentEarthquakes()
            remoteList.addAll(recentQuakes)
        } catch (_: Exception) {
            // Gracefully tolerate network failure
        }

        if (remoteList.isNotEmpty()) {
            val unique = remoteList.distinctBy { it.id }
            val entities = unique.map { it.toEntity() }
            earthquakeDao.insertOrUpdateAll(entities)
        }

        // Return latest from local database
        return earthquakeDao.getHistory(50).map { it.toDomain() }
    }

    override fun observeHistory(): Flow<List<Earthquake>> {
        return earthquakeDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getEarthquakeById(id: String): Earthquake? {
        val local = earthquakeDao.getById(id)
        if (local != null) {
            return local.toDomain()
        }

        // Refresh from remote if missing locally
        getLatestEarthquakes()
        return earthquakeDao.getById(id)?.toDomain()
    }

    override suspend fun isEventProcessed(eventId: String): Boolean {
        return processedEventDao.isProcessed(eventId)
    }

    override suspend fun markEventProcessed(eventId: String, alertTriggered: Boolean) {
        processedEventDao.insert(
            ProcessedEventEntity(
                eventId = eventId,
                processedAt = System.currentTimeMillis(),
                alertTriggered = alertTriggered
            )
        )
    }
}
