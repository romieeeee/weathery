package com.example.weathery.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/**
 * DAO 인터페이스
 * - 데이터베이스 작업을 수행하는 메서드를 포함한다
 */

@Dao
interface CityDao {
    // 도시 데이터 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(cityEntity: CityEntity): Long

    // 특정 도시 데이터 가져오기
    @Query("SELECT * FROM city_table WHERE cityName = :cityName LIMIT 1")
    suspend fun getCity(cityName: String): CityEntity?

    // 모든 도시 데이터 가져오기
    @Query("SELECT * FROM city_table")
    suspend fun getAllCities(): List<CityEntity>

    // 도시와 날씨 정보를 cityId로 조인하여 가져옴
    @Transaction
    @Query("SELECT * FROM city_table")
    suspend fun getCitiesWithWeather(): List<CityWithWeather>
}