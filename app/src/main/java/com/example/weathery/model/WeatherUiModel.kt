package com.example.weathery.model

data class WeatherUiModel(
    val cityName: String? = "현재 위치는?",
    val temperature: String,
    val skyCondition: String,
    val humidity: String,
    val rainfall: String,
    val windSpeed: String,
    val hourlyForecasts: List<HourlyWeather>,
    val weeklyForecasts: List<WeeklyWeather>
)
