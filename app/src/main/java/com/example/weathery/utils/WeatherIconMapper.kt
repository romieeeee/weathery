package com.example.weathery.utils

import android.util.Log
import com.example.weathery.R

object WeatherIconMapper {
    fun getWeatherIcon(skyCondition: String): Int {
        return when (skyCondition) {
            "맑음" -> R.drawable.ic_sunny
            "구름 많음" -> R.drawable.ic_sunnycloud
            "흐림" -> R.drawable.ic_cloudy
            "비" -> R.drawable.ic_rainy
            "비/눈" -> R.drawable.ic_rainysnow
            "눈" -> R.drawable.ic_snow
            else -> {
                Log.w("WeatherFragment", "Unknown weather condition: $skyCondition") // 로그 추가
                R.drawable.ic_unknown
            }
        }
    }
}