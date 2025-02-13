package com.example.weathery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CityEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
}
