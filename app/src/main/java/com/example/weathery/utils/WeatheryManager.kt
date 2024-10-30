package com.example.weathery.utils

import android.util.Log
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.database.CityDao
import com.example.weathery.database.CityEntity
import com.example.weathery.database.WeatherDao
import com.example.weathery.database.WeatherEntity
import com.example.weathery.repository.WeatherRepository

private const val TAG = "WeatheryManager"

class WeatheryManager(
    private val cityDao: CityDao,
    private val weatherDao: WeatherDao,
    private val weatherRepository: WeatherRepository
) {

    /**
     * 도시 데이터를 DB에 저장하는 메소드
     */
    suspend fun saveCity(cityName: String, latitude: Double, longitude: Double): Long {
        // 이미 존재하는 도시인지 확인
        val existingCity = cityDao.getCityByName(cityName)
        return if (existingCity == null) {
            val newCity = CityEntity(
                cityName = cityName,
                latitude = latitude,
                longitude = longitude
            )
            // 도시 데이터 삽입
            val cityId = cityDao.insertCity(newCity) // 삽입 후 cityId 반환
            Log.d(TAG, "saveCity :: new city saved = $cityName")
            cityId
        } else {
            Log.d(TAG, "saveCity :: city already exists = $cityName")
            existingCity.cityId.toLong()
        }
    }

    // 날씨 데이터를 가져오고 저장
    suspend fun fetchWeatherData(cityId: Long, lat: Double, lon: Double): WeatherEntity? {
        return try {
            // API 호출
            val weatherResponse = weatherRepository.fetchWeatherResponseForCity(lat, lon)
            weatherResponse?.let { weatherData ->
                val weatherDataProcessor = WeatherDataProcessor(weatherData)

                // 날씨 데이터를 weather 테이블에 저장
                val weatherEntity = WeatherEntity(
                    cityId = cityId.toInt(),
                    temperature = weatherDataProcessor.getCurrentTemperature(),
                    weatherCondition = weatherDataProcessor.getSkyCondition(),
                    rainfall = weatherDataProcessor.getRainfall(),
                    windSpeed = weatherDataProcessor.getWindSpeed(),
                    humidity = weatherDataProcessor.getHumidity(),
                    timestamp = System.currentTimeMillis()
                )
                weatherDao.insertWeather(weatherEntity)
                Log.d(TAG, "fetchWeatherData :: weather saved(cityId = $cityId")

                return weatherEntity
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchWeatherData :: Error fetching weather data", e)
            null
        }
    }
}