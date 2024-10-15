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
interface CityDao {
    // 도시 데이터 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City)

    // 특정 도시 데이터 가져오기
    @Query("SELECT * FROM city_table WHERE cityName = :cityName LIMIT 1")
    suspend fun getCity(cityName: String): City?

    // 모든 도시 데이터 가져오기
    @Query("SELECT * FROM city_table")
    suspend fun getAllCities(): List<City>
}