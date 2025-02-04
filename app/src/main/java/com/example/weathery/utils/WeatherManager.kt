package com.example.weathery.utils

import android.util.Log
import com.example.weathery.R
import com.example.weathery.data.WeatherResponse
import com.example.weathery.database.CityDao
import com.example.weathery.database.CityEntity
import com.example.weathery.network.RetrofitClient
import com.example.weathery.network.WeatherApi
import com.example.weathery.repository.WeatherRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "WeatheryManager"

class WeatherManager(
    private val cityDao: CityDao,
) {

    /**
     * 도시 데이터를 DB에 저장하는 메소드
     */
    suspend fun saveCity(cityName: String, latitude: Double, longitude: Double){
        // 이미 존재하는 도시인지 확인
        val existingCity = cityDao.getCityByName(cityName)
        if (existingCity == null) {
            val newCity = CityEntity(
                cityName = cityName,
                latitude = latitude,
                longitude = longitude
            )
            // 도시 데이터 삽입
            cityDao.insertCity(newCity) // 삽입 후 cityId 반환
            Log.d(TAG, "saveCity :: new city saved = $cityName")
        } else {
            Log.d(TAG, "saveCity :: city already exists = $cityName")
            existingCity.cityId.toLong()
        }
    }

    /**
     * 좌표(latitude, longitude)로 날씨 api 호출하기
     */
    suspend fun fetchWeatherData(lat: Double, lon: Double): WeatherResponse? {
        return try {
            val apiKey = ApiKey.W_API_KEY
            val baseDate = getFormattedDate()
            val baseTime = getFormattedTime()

            val response = RetrofitClient.getInstance().create(WeatherApi::class.java)
                .getWeatherData(
                    apiKey,
                    1,
                    1000,
                    "JSON",
                    baseDate,
                    "0500",
                    lat.toInt(),
                    lon.toInt())

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