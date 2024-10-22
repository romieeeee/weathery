package com.example.weathery.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CityEntity::class, WeatherEntity::class],
    version = 1,
    exportSchema = false
) // exportSchema = false 추가
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun weatherDao(): WeatherDao
}
