package com.example.weathery.database

import androidx.room.Embedded
import androidx.room.Relation

// 도시와 날씨 관계 설정
data class CityWithWeather(
    @Embedded val city: City,
    @Relation(
        parentColumn = "weatherId",
        entityColumn = "cityId"

    )
    val weather: Weather?
)