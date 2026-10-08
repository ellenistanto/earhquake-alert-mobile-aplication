package com.earthquakealert.app.data.remote.service

import com.earthquakealert.app.data.remote.api.BmkgApiService
import com.earthquakealert.app.data.remote.dto.toDomain
import com.earthquakealert.app.domain.model.Earthquake

interface EarthquakeRemoteDataSource {
    suspend fun getLatestEarthquake(): Earthquake?
    suspend fun getRecentEarthquakes(): List<Earthquake>
    suspend fun getFeltEarthquakes(): List<Earthquake>
}

class BmkgRemoteDataSourceImpl(
    private val apiService: BmkgApiService
) : EarthquakeRemoteDataSource {

    override suspend fun getLatestEarthquake(): Earthquake? {
        val response = apiService.getAutoGempa()
        return response.infoGempa?.gempa?.toDomain()
    }

    override suspend fun getRecentEarthquakes(): List<Earthquake> {
        val response = apiService.getRecentGempa()
        return response.infoGempa?.gempaList
            ?.mapNotNull { it.toDomain() }
            ?: emptyList()
    }

    override suspend fun getFeltEarthquakes(): List<Earthquake> {
        val response = apiService.getFeltGempa()
        return response.infoGempa?.gempaList
            ?.mapNotNull { it.toDomain() }
            ?: emptyList()
    }
}
