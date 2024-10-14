package com.example.weathery.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 날씨 데이터를 정의하는 클래스
 */

@Entity(tableName = "weather")
data class Weather(
    @PrimaryKey(autoGenerate = true) val weatherId: Int = 0,
    val cityId: Int, // 도시와 연결
    val temperature: String, // 온도
    val weatherCondition: String, // 날씨 상태
    val rainfall: String, // 강수량
    val windSpeed: String, // 풍속
    val humidity: String, // 습도
    val timestamp: Long // 데이터를 불러온 시간
)