package com.example.weathery.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO 인터페이스
 * - 데이터베이스 작업을 수행하는 메서드를 포함한다
 */

@Dao
interface CityDao {
    // 도시 데이터 삽입 (중복된 cityName이 있으면 무시)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(cityEntity: CityEntity)

    // 특정 도시 데이터 가져오기
    @Query("SELECT * FROM city WHERE cityName = :cityName LIMIT 1")
    suspend fun getCityByName(cityName: String): CityEntity?

    // 모든 도시 데이터 가져오기
    @Query("SELECT * FROM city")
    fun getAllCities(): Flow<List<CityEntity>>
}