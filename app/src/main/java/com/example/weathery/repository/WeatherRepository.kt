package com.example.weathery.repository

import android.util.Log
import com.example.weathery.data.WeatherDataProcessor
import com.example.weathery.database.City
import com.example.weathery.database.CityDao
import com.example.weathery.database.Weather
import com.example.weathery.database.WeatherDao
import com.example.weathery.network.RetrofitClient
import com.example.weathery.network.WeatherApi
import com.example.weathery.utils.ApiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherRepository(private val cityDao: CityDao, private val weatherDao: WeatherDao) {

    companion object {
        private const val CACHE_EXPIRATION_TIME = 3600 * 1000 // 1시간
    }

    // cityData로 해당 지역의 날씨 데이터 가져오기
    suspend fun fetchWeatherForCity(city: City): Weather? {
        val cachedWeather = weatherDao.getLatestWeatherByCityId(city.cityId)

        // 캐시된 데이터가 존재하고 유효 기간이 지나지 않았다면 캐시 사용
        cachedWeather?.let {
            if (System.currentTimeMillis() - it.timestamp < CACHE_EXPIRATION_TIME) {
                return it
            }
        }

        // 캐시가 없거나 유효하지 않은 경우 API에서 새로운 데이터를 가져옴
        return fetchWeatherFromApi(city.latitude, city.longitude)?.also { newWeather ->
            weatherDao.insertWeather(newWeather)
        }
    }

    /**
     * 날씨 데이터를 받아오는 함수
     * 위치 정보를 바탕으로 API 호출
     */
    private suspend fun fetchWeatherFromApi(lat: Double, lon: Double): Weather?{
        val apiKey = ApiKey.API_KEY
        val baseDate = getFormattedDate()
        val baseTime = getFormattedTime()

        return try {
            val response = RetrofitClient.getInstance().create(WeatherApi::class.java)
                .getWeatherData(apiKey, 1, 1000, "JSON", baseDate, baseTime, lat.toInt(), lon.toInt())

            if (response.response.header.resultCode == "00") {
                val processor = WeatherDataProcessor(response)
                Weather(
                    temperature = processor.getCurrentTemperature(),
                    weatherCondition = processor.getSkyCondition(),
                    rainfall = processor.getRainfall(),
                    windSpeed = processor.getWindSpeed(),
                    humidity = processor.getHumidity(),
                    timestamp = System.currentTimeMillis()
                )
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