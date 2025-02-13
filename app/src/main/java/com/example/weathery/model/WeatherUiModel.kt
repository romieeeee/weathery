package com.example.weathery.model

data class WeatherUiModel(
    val temperature: String,
    val skyCondition: String,
    val humidity: String,
    val rainfall: String,
    val windSpeed: String,
    val hourlyForecasts: List<HourlyWeather>,
    val weeklyForecasts: List<WeeklyWeather>
)
