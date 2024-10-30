package com.example.weathery.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CityEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
}
