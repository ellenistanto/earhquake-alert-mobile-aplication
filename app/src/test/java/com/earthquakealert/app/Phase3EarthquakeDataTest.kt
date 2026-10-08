package com.earthquakealert.app

import com.earthquakealert.app.data.remote.dto.BmkgEarthquakeDto
import com.earthquakealert.app.data.remote.dto.parseCoordinates
import com.earthquakealert.app.data.remote.dto.parseTimestamp
import com.earthquakealert.app.data.remote.dto.toDomain
import com.earthquakealert.app.data.remote.service.EarthquakeRemoteDataSource
import com.earthquakealert.app.data.repository.EarthquakeRepositoryImpl
import com.earthquakealert.app.domain.model.Earthquake
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase3EarthquakeDataTest {

    private val gson = Gson()

    @Test
    fun testParseRealBmkgAutoGempaJson() {
        val json = """
        {
            "Tanggal": "06 Okt 2026",
            "Jam": "10:42:45 WIB",
            "DateTime": "2026-10-06T03:42:45+00:00",
            "Coordinates": "-7.86,120.38",
            "Lintang": "7.86 LS",
            "Bujur": "120.38 BT",
            "Magnitude": "4.4",
            "Kedalaman": "21 km",
            "Wilayah": "Pusat gempa berada di laut 84 km Utara Ruteng",
            "Potensi": "Gempa ini dirasakan untuk diteruskan pada masyarakat",
            "Dirasakan": "II - III Kab. Manggarai",
            "Shakemap": "20261006104245.mmi.jpg"
        }
        """.trimIndent()

        val dto = gson.fromJson(json, BmkgEarthquakeDto::class.java)
        val domain = dto.toDomain()

        assertNotNull(domain)
        domain?.let {
            assertEquals("2026-10-06T03:42:45+00:00_-7.86,120.38", it.id)
            assertEquals(-7.86, it.latitude, 0.001)
            assertEquals(120.38, it.longitude, 0.001)
            assertEquals(4.4, it.magnitude, 0.001)
            assertEquals(21.0, it.depthKm ?: 0.0, 0.001)
            assertEquals("Pusat gempa berada di laut 84 km Utara Ruteng", it.region)
            assertEquals("II - III Kab. Manggarai", it.mmi)
            assertFalse(it.tsunamiPotential == true)
            assertEquals("BMKG", it.source)
        }
    }

    @Test
    fun testParseTsunamiPotential() {
        val tsunamiJson = """
        {
            "DateTime": "2026-10-06T04:00:00+00:00",
            "Coordinates": "-8.50,115.20",
            "Magnitude": "7.2",
            "Kedalaman": "10 km",
            "Wilayah": "Selatan Bali",
            "Potensi": "Berpotensi tsunami"
        }
        """.trimIndent()

        val dto = gson.fromJson(tsunamiJson, BmkgEarthquakeDto::class.java)
        val domain = dto.toDomain()
        assertNotNull(domain)
        assertTrue(domain?.tsunamiPotential == true)

        val noTsunamiJson = """
        {
            "DateTime": "2026-10-06T04:00:00+00:00",
            "Coordinates": "-8.50,115.20",
            "Magnitude": "5.2",
            "Kedalaman": "10 km",
            "Wilayah": "Selatan Bali",
            "Potensi": "Tidak berpotensi tsunami"
        }
        """.trimIndent()

        val noTsunamiDto = gson.fromJson(noTsunamiJson, BmkgEarthquakeDto::class.java)
        assertFalse(noTsunamiDto.toDomain()?.tsunamiPotential == true)
    }

    @Test
    fun testParseCoordinatesFallback() {
        val pairFromComma = parseCoordinates("-7.50,110.20", null, null)
        assertNotNull(pairFromComma)
        assertEquals(-7.50, pairFromComma!!.first, 0.001)
        assertEquals(110.20, pairFromComma.second, 0.001)

        val pairFromDirections = parseCoordinates(null, "8.12 LS", "112.50 BT")
        assertNotNull(pairFromDirections)
        assertEquals(-8.12, pairFromDirections!!.first, 0.001)
        assertEquals(112.50, pairFromDirections.second, 0.001)

        val pairFromNorthWest = parseCoordinates(null, "1.45 LU", "98.20 BB")
        assertNotNull(pairFromNorthWest)
        assertEquals(1.45, pairFromNorthWest!!.first, 0.001)
        assertEquals(-98.20, pairFromNorthWest.second, 0.001)
    }

    @Test
    fun testMalformedDataHandling() {
        val invalidMagDto = BmkgEarthquakeDto(
            magnitude = "abc",
            coordinates = "-7.0,110.0"
        )
        assertNull(invalidMagDto.toDomain())

        val invalidCoordsDto = BmkgEarthquakeDto(
            magnitude = "5.0",
            coordinates = "invalid_coords"
        )
        assertNull(invalidCoordsDto.toDomain())
    }

    @Test
    fun testRepositoryDeduplicationAndSorting() = runBlocking {
        val fakeDataSource = object : EarthquakeRemoteDataSource {
            override suspend fun getLatestEarthquake(): Earthquake {
                return Earthquake(
                    id = "event-1",
                    timestampMillis = 1000L,
                    latitude = -7.0,
                    longitude = 110.0,
                    magnitude = 4.5,
                    depthKm = 10.0,
                    region = "Area 1",
                    tsunamiPotential = false,
                    mmi = null
                )
            }

            override suspend fun getRecentEarthquakes(): List<Earthquake> {
                return listOf(
                    Earthquake(
                        id = "event-1", // duplicate
                        timestampMillis = 1000L,
                        latitude = -7.0,
                        longitude = 110.0,
                        magnitude = 4.5,
                        depthKm = 10.0,
                        region = "Area 1",
                        tsunamiPotential = false,
                        mmi = null
                    ),
                    Earthquake(
                        id = "event-2",
                        timestampMillis = 2000L,
                        latitude = -8.0,
                        longitude = 111.0,
                        magnitude = 5.2,
                        depthKm = 15.0,
                        region = "Area 2",
                        tsunamiPotential = false,
                        mmi = null
                    )
                )
            }

            override suspend fun getFeltEarthquakes(): List<Earthquake> = emptyList()
        }

        val fakeDao = object : com.earthquakealert.app.data.local.dao.EarthquakeDao {
            private val list = mutableListOf<com.earthquakealert.app.data.local.entity.EarthquakeEntity>()

            override suspend fun insertOrUpdate(earthquake: com.earthquakealert.app.data.local.entity.EarthquakeEntity) {
                list.removeAll { it.id == earthquake.id }
                list.add(earthquake)
            }

            override suspend fun insertOrUpdateAll(earthquakes: List<com.earthquakealert.app.data.local.entity.EarthquakeEntity>) {
                earthquakes.forEach { insertOrUpdate(it) }
            }

            override fun observeAll(): kotlinx.coroutines.flow.Flow<List<com.earthquakealert.app.data.local.entity.EarthquakeEntity>> {
                return kotlinx.coroutines.flow.flowOf(list.sortedByDescending { it.timestamp })
            }

            override suspend fun getLatest(): com.earthquakealert.app.data.local.entity.EarthquakeEntity? {
                return list.maxByOrNull { it.timestamp }
            }

            override suspend fun getById(id: String): com.earthquakealert.app.data.local.entity.EarthquakeEntity? {
                return list.find { it.id == id }
            }

            override suspend fun getHistory(limit: Int): List<com.earthquakealert.app.data.local.entity.EarthquakeEntity> {
                return list.sortedByDescending { it.timestamp }.take(limit)
            }
        }

        val fakeProcessedDao = object : com.earthquakealert.app.data.local.dao.ProcessedEventDao {
            private val set = mutableSetOf<String>()
            override suspend fun insert(event: com.earthquakealert.app.data.local.entity.ProcessedEventEntity) {
                set.add(event.eventId)
            }
            override suspend fun getById(eventId: String): com.earthquakealert.app.data.local.entity.ProcessedEventEntity? = null
            override suspend fun isProcessed(eventId: String): Boolean = set.contains(eventId)
        }

        val repository = EarthquakeRepositoryImpl(fakeDataSource, fakeDao, fakeProcessedDao)
        val result = repository.getLatestEarthquakes()

        assertEquals(2, result.size)
        // Check sorted by timestamp descending
        assertEquals("event-2", result[0].id)
        assertEquals("event-1", result[1].id)
    }
}
