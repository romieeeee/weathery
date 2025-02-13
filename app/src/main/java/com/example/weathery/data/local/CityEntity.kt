package com.example.weathery.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 도시 데이터를 정의하는 클래스
 * - 데이터 중복 방지를 위해 cityName을 unique로 설정
 */

@Entity(tableName = "city", indices = [Index(value = ["cityName"], unique = true)])
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val cityId: Int = 0, // 도시 데이터의 고유 ID (자동생성)
    val cityName: String, // 도시 이름
    val latitude: Double, // 위도
    val longitude: Double, // 경도
)
