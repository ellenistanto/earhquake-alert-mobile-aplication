package com.earthquakealert.app.domain.repository

import com.earthquakealert.app.domain.model.AlertSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AlertSettings>
    suspend fun getSettings(): AlertSettings
    suspend fun updateSettings(settings: AlertSettings)
}
