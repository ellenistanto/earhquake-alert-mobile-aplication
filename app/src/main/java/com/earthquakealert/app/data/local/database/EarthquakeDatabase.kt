package com.earthquakealert.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.earthquakealert.app.data.local.dao.EarthquakeDao
import com.earthquakealert.app.data.local.dao.ProcessedEventDao
import com.earthquakealert.app.data.local.entity.EarthquakeEntity
import com.earthquakealert.app.data.local.entity.ProcessedEventEntity

@Database(
    entities = [
        EarthquakeEntity::class,
        ProcessedEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EarthquakeDatabase : RoomDatabase() {

    abstract fun earthquakeDao(): EarthquakeDao
    abstract fun processedEventDao(): ProcessedEventDao

    companion object {
        private const val DATABASE_NAME = "earthquake_alert.db"

        @Volatile
        private var INSTANCE: EarthquakeDatabase? = null

        fun getInstance(context: Context): EarthquakeDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EarthquakeDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
