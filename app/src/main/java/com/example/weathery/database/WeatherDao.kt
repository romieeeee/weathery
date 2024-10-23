package com.example.weathery.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO 인터페이스
 * - 데이터베이스 작업을 수행하는 메서드를 포함한다
 */

@Dao
interface WeatherDao {
    // 날씨 데이터 삽입 (cityId 중복 시 업데이트)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherEntity: WeatherEntity)

    // 도시 ID로 날씨 데이터를 가져옴
    @Query("SELECT * FROM weather WHERE cityId = :cityId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestWeatherByCityId(cityId: Int): WeatherEntity?
}
