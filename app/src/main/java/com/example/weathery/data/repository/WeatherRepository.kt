package com.example.weathery.data.repository

import android.util.Log
import com.example.weathery.data.local.CityDao
import com.example.weathery.data.local.CityEntity
import com.example.weathery.data.remote.RetrofitClient
import com.example.weathery.data.remote.WeatherApi
import com.example.weathery.model.WeatherUiModel
import com.example.weathery.utils.ApiKey
import com.example.weathery.utils.WeatherDataProcessor
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val TAG = "WeatherRepository"

/**
 * 네트워크(API 호출)와 로컬 DB(Room)를 통합적으로 관리
 * - 날씨 데이터: Retrofit을 통해 API 호출 및 데이터 반환
 * - 도시 데이터: Room DB에서 데이터를 저장하고 불러오기
 */
class WeatherRepository(private val cityDao: CityDao) {

    suspend fun addCity(cityEntity: CityEntity) {
        val existingCity = cityDao.getCityByName(cityEntity.cityName)
        if (existingCity == null) {
            cityDao.insertCity(cityEntity)
        } else {
            Log.d(TAG, "addCity :: city already exists")
        }
    }

    fun getCities(): Flow<List<CityEntity>> = cityDao.getAllCities()

    /**
     * 좌표(latitude, longitude)로 날씨 api 호출하기
     * - 비동기 작업이므로 suspend 함수로 선언
     */
    suspend fun fetchWeatherData(lat: Double, lon: Double): WeatherUiModel? {
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
                    lon.toInt()
                )

            if (response.response.header.resultCode == "00") {
                WeatherDataProcessor(response).toUiModel()
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