package com.example.weathery.network

import com.example.weathery.utils.ApiKey.Companion.API_KEY
import com.example.weathery.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 요청 파라미터에 맞춰 날씨 API 인터페이스 정의
 * API 요청의 end point HTTP 메서드를 정의하는 인터페이스
 */
interface WeatherApi {
    @GET("getUltraSrtNcst") // end point 설정
    suspend fun getWeatherData(
        @Query("ServiceKey") apiKey: String = API_KEY,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("dataType") dataType: String = "JSON", // 기본값을 JSON으로 설정
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): WeatherResponse
}
