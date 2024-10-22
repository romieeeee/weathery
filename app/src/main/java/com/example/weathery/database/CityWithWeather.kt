package com.example.weathery.database

import androidx.room.Embedded
import androidx.room.Relation

/**
 * 두 테이블을 조인한 결과를 담을 데이터 클래스
 */

data class CityWithWeather(
    @Embedded val city: CityEntity,
    @Relation(
        parentColumn = "cityId",
        entityColumn = "cityId"
    )
    val weather: WeatherEntity? // 도시와 연결된 날씨 정보 (nullable)
)
