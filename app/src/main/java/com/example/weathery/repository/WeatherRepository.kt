package com.example.weathery.repository

import com.example.weathery.data.WeatherResponse
import com.example.weathery.database.CityDao
import com.example.weathery.database.WeatherDao
import com.example.weathery.network.RetrofitClient
import com.example.weathery.network.WeatherApi
import com.example.weathery.utils.ApiKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherRepository(private val cityDao: CityDao, private val weatherDao: WeatherDao) {

    companion object {
        private const val CACHE_EXPIRATION_TIME = 3600 * 1000 // 1시간
    }

    // cityData로 해당 지역의 날씨 데이터 가져오기
    suspend fun fetchWeatherResponseForCity(lat: Double, lon: Double): WeatherResponse? {
        return try {
            val apiKey = ApiKey.W_API_KEY
            val baseDate = getFormattedDate()
            val baseTime = getFormattedTime()

            val response = RetrofitClient.getInstance().create(WeatherApi::class.java)
                .getWeatherData(apiKey, 1, 1000, "JSON", baseDate, baseTime, lat.toInt(), lon.toInt())

            if (response.response.header.resultCode == "00") {
                response // 성공 시 WeatherResponse 반환
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 현재 날짜를 yyyyMMdd 형식으로 반환하는 함수
     */
    private fun getFormattedDate(): String {
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return currentTime.format(formatter)
    }

    /**
     * 현재 시간을 HHmm 형식으로 반환하는 함수
     */
    private fun getFormattedTime(): String {
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        return currentTime.format(formatter)
    }
}