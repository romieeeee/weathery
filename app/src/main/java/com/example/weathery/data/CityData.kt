package com.example.weathery.data

/**
 * 도시 데이터를 정의하는 클래스
 */
data class CityData(
    val cityName: String,
    val cityTemp: String,
//    val cityCoord: Pair<Double, Double> // 위도와 경도를 나타내는 Pair
)
