package com.example.weathery.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 도시 데이터를 정의하는 클래스
 * - @Entity: DB 테이블을 나타낸다
 */

@Entity(tableName = "city")
data class City(
    @PrimaryKey(autoGenerate = true) val cityId: Int = 0, // 도시 데이터의 고유 ID (자동생성)
    val cityName: String, // 도시 이름
    val latitude: Double, // 위도
    val longitude: Double, // 경도
)