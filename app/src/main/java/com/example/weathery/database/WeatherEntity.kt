package com.example.weathery.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 날씨 데이터를 정의하는 클래스
 */

@Entity(
    tableName = "weather_table",
    foreignKeys = [ForeignKey(
        entity = CityEntity::class,  // City 클래스 참조
        parentColumns = ["cityId"],  // 부모 테이블의 cityId 참조
        childColumns = ["cityId"],
        onDelete = ForeignKey.CASCADE // 부모 삭제 시 자식도 삭제
    )],
    indices = [Index(value = ["cityId"])] // cityId에 인덱스 추가
)
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val weatherId: Int = 0, // 날씨 데이터의 고유 ID
    val cityId: Int,  // 도시와 연결되는 외래키
    val temperature: String?, // 온도
    val weatherCondition: String?, // 날씨 상태
    val rainfall: String?, // 강수량
    val windSpeed: String?, // 풍속
    val humidity: String?, // 습도
    val timestamp: Long // 데이터를 불러온 시간
)
