package com.earthquakealert.app.domain.repository

import com.earthquakealert.app.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getLastKnownLocation(): UserLocation?
    suspend fun refreshLocation(): UserLocation?
    fun observeLocation(): Flow<UserLocation?>
}
