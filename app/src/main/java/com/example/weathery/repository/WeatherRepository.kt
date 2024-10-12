package com.example.weathery.repository

import android.util.Log
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.network.RetrofitClient
import com.example.weathery.network.WeatherApi
import com.example.weathery.utils.ApiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherRepository {

    /**
     * 날씨 데이터를 받아오는 함수
     * 위치 정보를 바탕으로 API 호출
     */
    suspend fun fetchWeatherData(lat: Double, lon: Double): Result<WeatherDataProcessor> {
        val baseDate = getFormattedDate()
        val baseTime = getFormattedTime()
        val apiKey = ApiKey.API_KEY
//        Log.d("API", "fetchWeatherData :: called (${lat}, ${lon})")


        return withContext(Dispatchers.IO) {
            Log.d("API", "fetchWeatherData :: called success (${lat}, ${lon})")

            try {
                val response = RetrofitClient.getInstance().create(WeatherApi::class.java)
                    .getWeatherData(
                        apiKey,
                        1,
                        1000,
                        "JSON",
                        baseDate,
                        baseTime,
                        lat.toInt(),
                        lon.toInt()
                    )

                if (response.response.header.resultCode == "00") {
                    val processor = WeatherDataProcessor(response)
                    Result.success(processor)
                } else {
                    Log.e("API", "Error: ${response.response.header.resultMsg}")
                    Result.failure(Exception(response.response.header.resultMsg))
                }
            } catch (e: Exception) {
                Log.e("API", "API Call Failed: ${e.message}")
                Result.failure(e)
            }
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