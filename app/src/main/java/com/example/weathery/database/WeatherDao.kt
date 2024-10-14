package com.example.weathery.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

/**
 * DAO 인터페이스
 * - 데이터베이스 작업을 수행하는 메서드를 포함한다
 */
@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE cityId = :cityId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWeatherByCityId(cityId: Int): Weather?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: Weather)
}
