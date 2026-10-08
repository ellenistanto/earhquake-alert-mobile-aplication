package com.earthquakealert.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.earthquakealert.app.data.local.entity.EarthquakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EarthquakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(earthquake: EarthquakeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(earthquakes: List<EarthquakeEntity>)

    @Query("SELECT * FROM earthquakes ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<EarthquakeEntity>>

    @Query("SELECT * FROM earthquakes ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): EarthquakeEntity?

    @Query("SELECT * FROM earthquakes WHERE id = :id")
    suspend fun getById(id: String): EarthquakeEntity?

    @Query("SELECT * FROM earthquakes ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getHistory(limit: Int): List<EarthquakeEntity>
}
