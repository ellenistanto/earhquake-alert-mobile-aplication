package com.earthquakealert.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "processed_events")
data class ProcessedEventEntity(
    @PrimaryKey
    val eventId: String,
    val processedAt: Long = System.currentTimeMillis(),
    val alertTriggered: Boolean
)
