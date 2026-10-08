package com.earthquakealert.app

import com.earthquakealert.app.data.local.dao.EarthquakeDao
import com.earthquakealert.app.data.local.dao.ProcessedEventDao
import com.earthquakealert.app.data.local.entity.EarthquakeEntity
import com.earthquakealert.app.data.local.entity.ProcessedEventEntity
import com.earthquakealert.app.data.local.entity.toDomain
import com.earthquakealert.app.data.local.entity.toEntity
import com.earthquakealert.app.data.remote.service.EarthquakeRemoteDataSource
import com.earthquakealert.app.data.repository.EarthquakeRepositoryImpl
import com.earthquakealert.app.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase4DatabaseTest {

    @Test
    fun testEntityDomainMapping() {
        val original = Earthquake(
            id = "eq-entity-01",
            timestampMillis = 1712000000000L,
            latitude = -7.50,
            longitude = 110.25,
            magnitude = 5.6,
            depthKm = 12.0,
            region = "Yogyakarta Region",
            tsunamiPotential = false,
            mmi = "III MMI",
            source = "BMKG"
        )

        val entity = original.toEntity(distanceKm = 45.0, alertTriggered = true)
        assertEquals("eq-entity-01", entity.id)
        assertEquals(45.0, entity.distanceKm ?: 0.0, 0.01)
        assertTrue(entity.alertTriggered)

        val domain = entity.toDomain()
        assertEquals(original.id, domain.id)
        assertEquals(original.latitude, domain.latitude, 0.001)
        assertEquals(original.longitude, domain.longitude, 0.001)
        assertEquals(original.magnitude, domain.magnitude, 0.01)
        assertEquals(original.depthKm, domain.depthKm)
        assertEquals(original.region, domain.region)
        assertEquals(original.mmi, domain.mmi)
    }

    @Test
    fun testDuplicateAlertPreventionContract() = runBlocking {
        val processedEvents = mutableMapOf<String, ProcessedEventEntity>()

        val fakeProcessedDao = object : ProcessedEventDao {
            override suspend fun insert(event: ProcessedEventEntity) {
                processedEvents[event.eventId] = event
            }

            override suspend fun getById(eventId: String): ProcessedEventEntity? {
                return processedEvents[eventId]
            }

            override suspend fun isProcessed(eventId: String): Boolean {
                return processedEvents.containsKey(eventId)
            }
        }

        val fakeEarthquakeDao = object : EarthquakeDao {
            private val items = mutableListOf<EarthquakeEntity>()
            override suspend fun insertOrUpdate(earthquake: EarthquakeEntity) { items.add(earthquake) }
            override suspend fun insertOrUpdateAll(earthquakes: List<EarthquakeEntity>) { items.addAll(earthquakes) }
            override fun observeAll(): Flow<List<EarthquakeEntity>> = flowOf(items)
            override suspend fun getLatest(): EarthquakeEntity? = items.lastOrNull()
            override suspend fun getById(id: String): EarthquakeEntity? = items.find { it.id == id }
            override suspend fun getHistory(limit: Int): List<EarthquakeEntity> = items.take(limit)
        }

        val fakeRemote = object : EarthquakeRemoteDataSource {
            override suspend fun getLatestEarthquake(): Earthquake? = null
            override suspend fun getRecentEarthquakes(): List<Earthquake> = emptyList()
            override suspend fun getFeltEarthquakes(): List<Earthquake> = emptyList()
        }

        val repository = EarthquakeRepositoryImpl(fakeRemote, fakeEarthquakeDao, fakeProcessedDao)

        val eventId = "2026-10-06T03:42:45+00:00_-7.86,120.38"

        // Initially not processed
        assertFalse(repository.isEventProcessed(eventId))

        // Mark as processed
        repository.markEventProcessed(eventId, alertTriggered = true)

        // Now is processed - duplicate alert prevented!
        assertTrue(repository.isEventProcessed(eventId))
    }

    @Test
    fun testOfflineFallbackToDatabase() = runBlocking {
        val cachedEntity = EarthquakeEntity(
            id = "cached-01",
            timestamp = 1711000000000L,
            latitude = -8.0,
            longitude = 112.0,
            magnitude = 4.8,
            depthKm = 10.0,
            region = "East Java",
            tsunamiPotential = false,
            mmi = null,
            source = "BMKG"
        )

        val fakeEarthquakeDao = object : EarthquakeDao {
            private val items = mutableListOf(cachedEntity)
            override suspend fun insertOrUpdate(earthquake: EarthquakeEntity) { items.add(earthquake) }
            override suspend fun insertOrUpdateAll(earthquakes: List<EarthquakeEntity>) { items.addAll(earthquakes) }
            override fun observeAll(): Flow<List<EarthquakeEntity>> = flowOf(items)
            override suspend fun getLatest(): EarthquakeEntity? = items.firstOrNull()
            override suspend fun getById(id: String): EarthquakeEntity? = items.find { it.id == id }
            override suspend fun getHistory(limit: Int): List<EarthquakeEntity> = items.take(limit)
        }

        val throwingRemote = object : EarthquakeRemoteDataSource {
            override suspend fun getLatestEarthquake(): Earthquake {
                throw java.io.IOException("No internet")
            }
            override suspend fun getRecentEarthquakes(): List<Earthquake> {
                throw java.io.IOException("No internet")
            }
            override suspend fun getFeltEarthquakes(): List<Earthquake> {
                throw java.io.IOException("No internet")
            }
        }

        val fakeProcessedDao = object : ProcessedEventDao {
            override suspend fun insert(event: ProcessedEventEntity) {}
            override suspend fun getById(eventId: String): ProcessedEventEntity? = null
            override suspend fun isProcessed(eventId: String): Boolean = false
        }

        val repository = EarthquakeRepositoryImpl(throwingRemote, fakeEarthquakeDao, fakeProcessedDao)

        // Must not crash, should return cached data
        val result = repository.getLatestEarthquakes()
        assertEquals(1, result.size)
        assertEquals("cached-01", result[0].id)
    }
}
