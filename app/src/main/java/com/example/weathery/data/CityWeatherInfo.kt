package com.example.weathery.data

/**
 * 도시 이름과 날씨 데이터를 함께 저장하는 클래스
 */
data class CityWeatherInfo(
    val cityName: String,
    val date: String,
    val weatherDataProcessor: WeatherDataProcessor // 날씨 데이터 프로세서
)
