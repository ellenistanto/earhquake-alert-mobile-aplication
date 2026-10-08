package com.earthquakealert.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earthquakealert.app.data.local.entity.ProcessedEventEntity

@Dao
interface ProcessedEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: ProcessedEventEntity)

    @Query("SELECT * FROM processed_events WHERE eventId = :eventId")
    suspend fun getById(eventId: String): ProcessedEventEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM processed_events WHERE eventId = :eventId)")
    suspend fun isProcessed(eventId: String): Boolean
}
